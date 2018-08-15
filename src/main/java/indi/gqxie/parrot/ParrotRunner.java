package indi.gqxie.parrot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import indi.gqxie.parrot.mq.rocketmq.RocketMQProducer;
import indi.gqxie.parrot.system.entity.TErrorMessage;
import indi.gqxie.parrot.system.service.parrot.TErrorMessageService;
import indi.gqxie.parrot.util.CanalUtils;
import indi.gqxie.parrot.util.StringCompress;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.storm.shade.org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Component
@Order(1)
public class ParrotRunner implements ApplicationRunner
{
    @Value("${canal.batch.size}")
    private Integer batchSize;
    @Value("${rocket.mq.topic}")
    private String  topic;
    @Value("${rocket.mq.tag}")
    private String  tag;

    private CanalConnector connector;

    @Autowired
    TErrorMessageService errorMessageService;

    @Autowired
    RocketMQProducer rocketMQProducer;

    @Override
    public void run(ApplicationArguments applicationArguments) throws InterruptedException
    {
        connector = CanalUtils.getCanalConnector();
        int emptyCount = 0;
        try
        {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            while (true)
            {
                try
                {
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0)
                    {
                        emptyCount++;
                        System.out.println("empty count : " + emptyCount);
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    else
                    {
                        emptyCount = 0;
                        sendMqMessage(message.getEntries());
                    }
                    connector.ack(batchId); // 提交确认
                }
                catch (Exception e)
                {
                    log.error("disconnect from canal server, try to reconnect...");
                    Thread.sleep(20000);
                    connector = CanalUtils.getCanalConnector();
                    connector.connect();
                    connector.subscribe(".*\\..*");
                    connector.rollback();
                }
            }
        }
        finally
        {
            connector.disconnect();
        }
    }

    private void sendMqMessage(List<Entry> entrys)
    {
        for (Entry entry : entrys)
        {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND)
            {
                continue;
            }

            String schemeName = entry.getHeader().getSchemaName();
            String tableName = entry.getHeader().getTableName();
            boolean blankTable = StringUtils.isEmpty(schemeName) || StringUtils.isEmpty(tableName);
            if (blankTable)
            {
                continue;
            }

            RowChange rowChange = null;
            try
            {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            }
            catch (Exception e)
            {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChange.getEventType();
            log.info("database={},table={},eventType={}", schemeName, tableName, eventType.name());

            //ddl
            if (rowChange.getIsDdl())
            {
                buildAndSendDdl(schemeName, tableName, eventType, rowChange.getSql());
            }

            for (RowData rowData : rowChange.getRowDatasList())
            {
                if (eventType == EventType.DELETE)
                {
                    buildAndSend(rowData.getBeforeColumnsList(), schemeName, tableName, eventType);
                }
                else if (eventType == EventType.INSERT)
                {
                    buildAndSend(rowData.getAfterColumnsList(), schemeName, tableName, eventType);
                }
                else
                {
                    buildAndSend(rowData.getAfterColumnsList(), schemeName, tableName, eventType);
                }
            }
        }
    }

    private void buildAndSendDdl(String schemaName, String tableName, EventType eventType, String sql)
    {
        JSONObject obj = new JSONObject();
        obj.put("schemaName", schemaName);
        obj.put("tableName", tableName);
        obj.put("eventType", eventType.name());
        obj.put("sql", sql);
        obj.put("idDdl", true);
        String line = StringCompress.compress(obj.toJSONString());
        try
        {
            org.apache.rocketmq.common.message.Message message = new org.apache.rocketmq.common.message.Message(topic,
                    tag, line.getBytes(RemotingHelper.DEFAULT_CHARSET));
            rocketMQProducer.sendMessage(message);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("send message to mq error!", e);
            TErrorMessage message = new TErrorMessage();
            message.setSchemaName(schemaName);
            message.setEventType(eventType.name());
            message.setMessage(obj.toJSONString());
            message.setTableName(tableName);
            message.setIsDdl(1);
            message.setCreateTime(DateTime.now().toDate());
            saveErrorMessage(message);
        }
    }

    private void buildAndSend(List<Column> columns, String schemaName, String tableName, EventType eventType)
    {
        JSONObject obj = new JSONObject();
        JSONArray fieldArray = new JSONArray();
        for (Column column : columns)
        {
            JSONObject columnObj = new JSONObject();
            columnObj.put("name", column.getName());
            columnObj.put("value", column.getValue());
            columnObj.put("update", column.getUpdated());
            columnObj.put("isKey", column.getIsKey());
            columnObj.put("type",
                    column.getUnknownFields().toString().replace("\n", "").replace("10: \"", "").replace("\"", ""));
            fieldArray.add(columnObj);
        }
        obj.put("fields", fieldArray);
        obj.put("schemaName", schemaName);
        obj.put("tableName", tableName);
        obj.put("eventType", eventType.name());
        String line = StringCompress.compress(obj.toJSONString());

        try
        {
            org.apache.rocketmq.common.message.Message message = new org.apache.rocketmq.common.message.Message(topic,
                    tag, line.getBytes(RemotingHelper.DEFAULT_CHARSET));
            rocketMQProducer.sendMessage(message);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("send message to mq error!", e);
            TErrorMessage message = new TErrorMessage();
            message.setSchemaName(schemaName);
            message.setEventType(eventType.name());
            message.setMessage(obj.toJSONString());
            message.setTableName(tableName);
            message.setIsDdl(0);
            message.setCreateTime(DateTime.now().toDate());
            saveErrorMessage(message);
        }
    }

    private void saveErrorMessage(TErrorMessage message)
    {
        errorMessageService.save(message);
    }

}

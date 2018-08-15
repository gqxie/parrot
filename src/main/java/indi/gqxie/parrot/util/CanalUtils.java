package indi.gqxie.parrot.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class CanalUtils
{
    private static String  zookeeperQuorum;
    private static Integer zookeeperSessionTimeOut;
    private static String  canalServerAddress;
    private static Integer canalServerPort;
    private static String  canalInstanceName;

    @Value("${zk.quorum}")
    public void setZookeeperQuorum(String zkQuorum)
    {
        zookeeperQuorum = zkQuorum;
    }

    @Value("${zk.session.timeout}")
    public void setZookeeperSessionTimeOut(Integer timeOut)
    {
        zookeeperSessionTimeOut = timeOut;
    }

    @Value("${canal.server.address}")
    public void setCanalServerAddress(String address)
    {
        canalServerAddress = address;
    }

    @Value("${canal.server.port}")
    public void setCanalServerPort(Integer port)
    {
        canalServerPort = port;
    }

    @Value("${canal.instance.name}")
    public void setCanalInstanceName(String instanceName)
    {
        canalInstanceName = instanceName;
    }

    private static Watcher watcher = event -> {
        log.debug("--------canal server changed---------");
        System.out.println("--------canal server changed---------");
    };

    /**
     * canal server地址动态切换
     *
     * @return
     */
    public static CanalConnector getCanalConnector()
    {
        CanalConnector defaultConnector = CanalConnectors
                .newSingleConnector(new InetSocketAddress(canalServerAddress, canalServerPort), canalInstanceName, "",
                        "");
        try
        {
            ZooKeeper zooKeeper = new ZooKeeper(zookeeperQuorum, zookeeperSessionTimeOut, watcher);
            String zkNodeName = "/otter/canal/destinations/" + canalInstanceName + "/running";
            byte[] data = zooKeeper.getData(zkNodeName, watcher, null);
            if (data.length == 0)
            {
                return defaultConnector;
            }
            zooKeeper.close();
            String dataStr = new String(data);
            JSONObject object = JSONObject.parseObject(dataStr);
            String url = object.getString("address");
            String[] urlArray = url.split(":");
            String address = urlArray[0];
            Integer port = Integer.valueOf(urlArray[1]);
            return CanalConnectors.newSingleConnector(new InetSocketAddress(address, port), canalInstanceName, "", "");

        }
        catch (Exception e)
        {
            log.error("get canal server address from zookeeper error, use default.", e);
            return defaultConnector;
        }
    }

}
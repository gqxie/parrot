package indi.gqxie.parrot.system.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xieguoqiang
 * @since 2018-08-07
 */
public class TErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("eventType")
    private String  eventType;
    private String  message;
    @TableField("schemaName")
    private String  schemaName;
    @TableField("tableName")
    private String  tableName;
    @TableField("createTime")
    private Date    createTime;
    @TableField("isDdl")
    private Integer isDdl;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIsDdl() {
        return isDdl;
    }

    public void setIsDdl(Integer isDdl) {
        this.isDdl = isDdl;
    }

    @Override
    public String toString() {
        return "TErrorMessage{" +
        "id=" + id +
        ", eventType=" + eventType +
        ", message=" + message +
        ", schemaName=" + schemaName +
        ", tableName=" + tableName +
        ", createTime=" + createTime +
        ", isDdl=" + isDdl +
        "}";
    }
}

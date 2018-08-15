package indi.gqxie.parrot.system.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * test
 * </p>
 *
 * @author xieguoqiang
 * @since 2018-08-07
 */
public class DDetect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * carid
     */
    private Integer carid;
    /**
     * status
     */
    private Integer status;
    private Date cutime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCarid() {
        return carid;
    }

    public void setCarid(Integer carid) {
        this.carid = carid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCutime() {
        return cutime;
    }

    public void setCutime(Date cutime) {
        this.cutime = cutime;
    }

    @Override
    public String toString() {
        return "DDetect{" +
        "id=" + id +
        ", carid=" + carid +
        ", status=" + status +
        ", cutime=" + cutime +
        "}";
    }
}

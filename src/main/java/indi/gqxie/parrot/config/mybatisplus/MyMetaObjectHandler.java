package indi.gqxie.parrot.config.mybatisplus;

import com.baomidou.mybatisplus.mapper.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Timestamp;

public class MyMetaObjectHandler extends MetaObjectHandler
{

    public void insertFill(MetaObject metaObject)
    {
        Object createTime = getFieldValByName("createTime", metaObject);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (createTime == null)
        {
            setFieldValByName("createTime", timestamp, metaObject);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject)
    {
        setFieldValByName("modifyTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }

}

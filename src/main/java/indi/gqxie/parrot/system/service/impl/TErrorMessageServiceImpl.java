package indi.gqxie.parrot.system.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import indi.gqxie.parrot.common.DBTypeEnum;
import indi.gqxie.parrot.common.DataSourceSwitch;
import indi.gqxie.parrot.system.entity.TErrorMessage;
import indi.gqxie.parrot.system.mapper.DDetectMapper;
import indi.gqxie.parrot.system.mapper.TErrorMessageMapper;
import indi.gqxie.parrot.system.service.parrot.TErrorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xieguoqiang
 * @since 2018-08-07
 */
@Service
public class TErrorMessageServiceImpl extends ServiceImpl<TErrorMessageMapper, TErrorMessage>
        implements TErrorMessageService
{
    @Autowired
    DDetectMapper dDetectMapper;

    @Autowired
    TErrorMessageMapper errorMessageMapper;

    @Override
    public List<TErrorMessage> getErrorMessage()
    {
        return selectList(null);
    }

    @DataSourceSwitch(DBTypeEnum.TEST)
    @Override
    public Integer getStatusByCarId(Integer carId)
    {
        return dDetectMapper.getStatusByCarId(carId);
    }

    @Override
    public Integer save(TErrorMessage message)
    {
        return errorMessageMapper.insert(message);
    }
}

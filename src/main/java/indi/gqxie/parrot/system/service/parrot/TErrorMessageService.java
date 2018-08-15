package indi.gqxie.parrot.system.service.parrot;

import com.baomidou.mybatisplus.service.IService;
import indi.gqxie.parrot.system.entity.TErrorMessage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xieguoqiang
 * @since 2018-08-07
 */
public interface TErrorMessageService extends IService<TErrorMessage>
{
    List<TErrorMessage> getErrorMessage();

    Integer getStatusByCarId(Integer carId);

    Integer save(TErrorMessage message);
}

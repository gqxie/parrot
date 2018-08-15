package indi.gqxie.parrot.system.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import indi.gqxie.parrot.system.entity.DDetect;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * test Mapper 接口
 * </p>
 *
 * @author xieguoqiang
 * @since 2018-08-07
 */
public interface DDetectMapper extends BaseMapper<DDetect>
{
    @Select("SELECT `status` from d_detect where carid =#{carId}")
    Integer getStatusByCarId(Integer carId);
}

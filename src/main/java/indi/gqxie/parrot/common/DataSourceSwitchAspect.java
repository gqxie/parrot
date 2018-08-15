package indi.gqxie.parrot.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Aspect
@Order(-100)
@Slf4j
public class DataSourceSwitchAspect
{

    @Pointcut("execution(* indi.gqxie.parrot.system.service.parrot..*.*(..))")
    private void parrotAspect()
    {
    }

    @Pointcut("execution(* indi.gqxie.parrot.system.service.test..*.*(..))")
    private void testAspect()
    {
    }

    @Before("parrotAspect()")
    public void db1(JoinPoint joinPoint)
    {
        setDataSource(joinPoint, DBTypeEnum.PARROT);
    }

    @Before("testAspect()")
    public void db2(JoinPoint joinPoint)
    {
        setDataSource(joinPoint, DBTypeEnum.TEST);
    }

    /**
     * 添加注解方式,如果有注解优先注解,没有则按传过来的数据源配置
     *
     * @param joinPoint
     * @param dbTypeEnum
     */
    private void setDataSource(JoinPoint joinPoint, DBTypeEnum dbTypeEnum)
    {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DataSourceSwitch dataSourceSwitch = methodSignature.getMethod().getAnnotation(DataSourceSwitch.class);
        if (Objects.isNull(dataSourceSwitch) || Objects.isNull(dataSourceSwitch.value()))
        {
            DbContextHolder.setDbType(dbTypeEnum);
        }
        else
        {
            log.info("根据注解来切换数据源,注解值为:" + dataSourceSwitch.value());
            switch (dataSourceSwitch.value().getValue())
            {
                case "parrot":
                    DbContextHolder.setDbType(DBTypeEnum.PARROT);
                    break;
                case "test":
                    DbContextHolder.setDbType(DBTypeEnum.TEST);
                    break;
                default:
                    DbContextHolder.setDbType(dbTypeEnum);
            }
        }
    }
}

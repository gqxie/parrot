package indi.gqxie.parrot.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DataSourceSwitch
{
    DBTypeEnum value() default DBTypeEnum.PARROT;
}

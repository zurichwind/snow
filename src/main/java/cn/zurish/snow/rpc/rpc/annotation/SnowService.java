package cn.zurish.snow.rpc.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2024/1/11 20:39
 */
@Target(ElementType.TYPE) //可以用在方法和类型上（类和接口），但是不能放在属性等其他位置。
@Retention(RetentionPolicy.RUNTIME)
public @interface SnowService {

    String serviceName() default "";

}

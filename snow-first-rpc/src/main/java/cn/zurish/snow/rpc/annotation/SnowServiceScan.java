package cn.zurish.snow.rpc.annotation;

import java.lang.annotation.*;

/**
 * 2024/1/11 20:41
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import({SnowComponentScanRegistrar.class})
public @interface SnowServiceScan {
    String value() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}

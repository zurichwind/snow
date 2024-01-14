package cn.zurish.snow.rpc.test.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.lock.qual.NewObject;

import java.io.Serializable;

/**
 * 2024/1/14 15:28:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;
    private String message;
}

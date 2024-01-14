package cn.zurish.snow.rpc.utils;

import lombok.NonNull;

/**
 * 2024/1/12 11:30
 */
public class CommonUtils {

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

}

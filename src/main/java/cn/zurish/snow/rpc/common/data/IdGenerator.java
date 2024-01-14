package cn.zurish.snow.rpc.common.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 2024/1/12 15:23
 */
public interface IdGenerator {

  public static long genarateId() {
      long timestamp = System.currentTimeMillis();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssssss");
      String formattedTimestamp = sdf.format(new Date(timestamp));

      return Long.parseLong(formattedTimestamp);
  }

    public static void main(String[] args) {
        System.out.println(genarateId());
    }
}

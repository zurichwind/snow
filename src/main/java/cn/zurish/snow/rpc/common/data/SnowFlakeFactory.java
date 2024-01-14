package cn.zurish.snow.rpc.common.data;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
/**
 * 2024/1/12 15:29
 */
public class SnowFlakeFactory {

    /**
     * 起始的时间戳 2020-01-01 00:00:00  毫秒
     */
    private final static long START_STMP = 1577808000000L;
    /**
     * 序列号占用的位数
     */
    private final static long SEQUENCE_BIT = 12;
    /**
     * 机器标识占用的位数
     */
    private final static long MACHINE_BIT = 5;
    /**
     * 数据中心占用的位数
     */
    private final static long DATACENTER_BIT = 5;

    /**
     * 每一部分的最大值
     * MAX_DATACENTER_NUM = 31
     * MAX_MACHINE_NUM = 31
     * MAX_SEQUENCE = 4095
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    /**
     * 数据中心
     */
    private long datacenterId = 0;
    /**
     * 机器标识
     */
    private long machineId = 0;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private long lastStmp = -1L;

    /**
     * 最大容忍时间, 单位毫秒, 即如果时钟只是回拨了该变量指定的时间, 那么等待相应的时间即可;
     * 考虑到sequence服务的高性能, 这个值不易过大
     */
    private static final long MAX_BACKWARD_MS = 5;

    /**
     * 最大扩展字段
     */
    private long maxExtension = 2L;

    /**
     * 保留machineId和lastTimestamp, 以及备用machineId和其对应的lastTimestamp
     */
    private static Map<Long, Long> machineIdLastTimeMap = new ConcurrentHashMap<>();

    /**
     * 初始化数据中心位，和机器标识
     * 0 < datacenterId < MAX_DATACENTER_NUM 31
     * 0 < machineId < MAX_MACHINE_NUM 31
     */
    public SnowFlakeFactory(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException(" datacenterId 必须介于[0,31] ");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(" machineId 必须介于[0,31] ");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        //初始化时间 machineIdLastTimeMap
        machineIdLastTimeMap.put(machineId, getNewstmp());
    }

    /**
     * 产生下一个ID
     */
    public synchronized String nextId() {
        //现存的扩展字段
        long extension = 0L;
        //获取当前时间毫秒数
        long currStmp = getNewstmp();
        //lastStmp = currStmp + 100;
        if (currStmp < lastStmp) {
            //throw new RuntimeException("时钟向后移动,拒绝生成id");

            // 如果时钟回拨在可接受范围内, 等待即可
            long offset = lastStmp - currStmp;
            //如果回拨时间不超过5毫秒，就等待相应的时间
            if (offset <= MAX_BACKWARD_MS) {
                try {
                    //睡（lastTimestamp - currentTimestamp）ms让其追上
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(offset));

                    currStmp = getNewstmp();
                    //如果时间还小于当前时间，那么利用扩展字段加1
                    //或者是采用抛异常并上报
                    if (currStmp < lastStmp) {
                        //扩展字段
                        extension += 1;
                        if (extension > maxExtension) {
                            //服务器时钟被调整了,ID生成器停止服务.
                            throw new RuntimeException(String.format("时钟向后移动。拒绝生成的id %d 毫秒", lastStmp - currStmp));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //扩展字段
                extension += 1;
                if (extension > maxExtension) {
                    //服务器时钟被调整了,ID生成器停止服务.
                    throw new RuntimeException(String.format("时钟向后移动，超出扩展位，拒绝生成的id %d 毫秒", lastStmp - currStmp));
                }
                //获取可以用的workid，对应的时间戳，必须大于当前时间戳
                tryGenerateKeyOnBackup(currStmp);
            }
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        long id = (currStmp - START_STMP) << (TIMESTMP_LEFT - extension)
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
        //如果时间戳回拨就让时间少移动一位
        return String.valueOf(id);
    }

    /**
     * 自旋锁获取当前时间戳
     */
    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    /**
     * 获取当前时间毫秒数
     */
    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    /**
     * 尝试在machineId的备份machineId上生成
     * 核心优化代码在方法tryGenerateKeyOnBackup()中，BACKUP_COUNT即备份machineId数越多，
     * sequence服务避免时钟回拨影响的能力越强，但是可部署的sequence服务越少，
     * 设置BACKUP_COUNT为3，最多可以部署1024/(3+1)即256个sequence服务，完全够用，
     * 抗时钟回拨影响的能力也得到非常大的保障。
     */
    private long tryGenerateKeyOnBackup(long currentMillis) {
        // 遍历所有machineId(包括备用machineId, 查看哪些machineId可用)
        for (Map.Entry<Long, Long> entry : machineIdLastTimeMap.entrySet()) {
            this.machineId = entry.getKey();
            // 取得备用machineId的lastTime
            Long tempLastTime = entry.getValue();
            lastStmp = tempLastTime == null ? 0L : tempLastTime;

            // 如果找到了合适的machineId，返回合适的时间，
            if (lastStmp <= currentMillis) {
                return lastStmp;
            }
        }
        // 如果所有machineId以及备用machineId都处于时钟回拨, 那么抛出异常
        throw new IllegalStateException("时钟在向后移动，当前时间是 " + currentMillis + " 毫秒，machineId映射 = " + machineIdLastTimeMap);
    }

    public static void main(String[] args) {
        SnowFlakeFactory snowFlakeFactory = new SnowFlakeFactory(1, 2);
        System.out.println(snowFlakeFactory.nextId());
    }
}
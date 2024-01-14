package cn.zurish.snow.rpc.factory;

import cn.zurish.snow.rpc.config.ThreadPoolConfig;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 自定义线程池工厂
 * 2024/1/13 8:55
 */
@Slf4j
public class SnowThreadPoolFactory {
    /**
     * 通过 threadNamePrefix 来区分不同的线程池 (我们可以把相同 threadNamePrefix 的线程池看作是为同一业务场景服务)
     * key: threadNamePrefix
     * value: threadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private SnowThreadPoolFactory() {

    }

    public static ExecutorService createThreadPoolIfAbsent(String threadNamePrefix) {
        ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
        return createThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix,false);
    }

    public static ExecutorService createThreadPoolIfAbsent(String threadNamePrefix, ThreadPoolConfig threadPoolConfig) {
        return createThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createThreadPoolIfAbsent( ThreadPoolConfig threadPoolConfig,String threadNamePrefix, Boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadPoolConfig,threadNamePrefix, daemon));
        // 如果 threadPool 被shutdown 的话就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(threadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }



    /**
     * shutdown 所有线程池
     */
    public static void shutDownAllThreadPool() {
        log.info(" call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());

            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("thread pool never terminated! ");
                executorService.shutdownNow();
            }
        });
    }

    private static ExecutorService createThreadPool(ThreadPoolConfig threadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(),threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(),
                threadPoolConfig.getUnit(), threadPoolConfig.getWorkQueue(),threadFactory
        );
    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    /**
     * 打印线程池的状态
     *
     * @param threadPool  线程池对象
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status",false));
        scheduledExecutorService.scheduleAtFixedRate( () -> {
            log.info("=============== ThreadPool Status ==============");
            log.info("=======>  ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("=======> Active Threads: [{}]", threadPool.getActiveCount());
            log.info("=======> Number of Tasks: [{}]", threadPool.getCompletedTaskCount());
            log.info("=======> Number of Tasks in QUeue: [{}]", threadPool.getQueue().size());
            log.info("================================================");
        }, 0, 1, TimeUnit.SECONDS);
    }


}

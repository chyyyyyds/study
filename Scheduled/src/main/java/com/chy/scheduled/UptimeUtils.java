package com.chy.scheduled;

public class UptimeUtils {

    // 记录系统启动时间（以毫秒为单位）
    private static final long START_TIME = System.currentTimeMillis();

    /**
     * 获取系统运行的秒数
     * @return 系统从启动到现在的运行时间（秒）
     */
    public static long getUptimeInSeconds() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - START_TIME) / 1000;
    }
}

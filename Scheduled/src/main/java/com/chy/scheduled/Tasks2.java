package com.chy.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.chy.scheduled.UptimeUtils.getUptimeInSeconds;

@Component
public class Tasks2 {
    /**
     *
     * 进task2()了就出不来了 一直在sleep  默认情况下定时任务用的同一个线程跑的
     */


    @Scheduled(cron = "0/20 * * * * ?")
    public void task1() {
        System.out.println("task1  20s一次");
    }

    @Scheduled(fixedRate = 1000 * 10)
    public void task2() throws InterruptedException {
        Thread.sleep(500000);  //睡很久
        System.out.println(getUptimeInSeconds() + " task2 " + getThreadId() + " 10s一次");
    }


    @Scheduled(fixedDelay = 1000 * 8)
    public void task3() throws InterruptedException {
        System.out.println(getUptimeInSeconds() + " task3 " + getThreadId() + "  任务开始，准备睡5秒");
        Thread.sleep(5000);  //睡5秒
        System.out.println(getUptimeInSeconds() + " task3 " + getThreadId() + " 任务完成");
    }


    @Scheduled(cron = "0 0 15 * * ?")
    public void task4() {
        System.out.println(getUptimeInSeconds() + " task4  每天15点");
    }

    private String getThreadId() {
        return "[" + Thread.currentThread().getId() + "]";
    }
}

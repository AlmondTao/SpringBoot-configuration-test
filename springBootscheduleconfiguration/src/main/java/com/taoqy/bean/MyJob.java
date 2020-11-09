package com.taoqy.bean;

import org.quartz.Job;

public interface MyJob extends Job {
    //设置定时任务执行周期
    String getCron();
    //设置定时任务名称
    String getJobName();
    //设置定时任务组-jlfz -tbfz -sskf -zjgl
    String getJobGroup();
    //是否启用
    boolean disabled();
}

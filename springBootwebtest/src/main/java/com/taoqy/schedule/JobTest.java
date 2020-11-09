package com.taoqy.schedule;

import com.taoqy.bean.MyJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 *
 * @author Taoqy
 * @version 1.0, 2020/10/22
 * @see [相关类/方法]
 * @since bapfopm-pfpsmas-cbfsms-service 1.0
 */
@Component
public class JobTest implements MyJob {
    @Override
    public String getCron() {
        return "30 30 * * * ? *";
    }

    @Override
    public String getJobName() {
        return "Job1";
    }

    @Override
    public String getJobGroup() {
        return "JobGroup1";
    }

    @Override
    public boolean disabled() {
        return true;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Job1正在执行——————"+new Date());
    }
}

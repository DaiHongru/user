package com.freework.user.config.quartz;

import com.freework.user.quartz.MessageLogJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daihongru
 */
@Configuration
public class QuartzConfiguration {
    private static Logger logger = LoggerFactory.getLogger(QuartzConfiguration.class);

    /**
     * JobDetail
     * 定时检查投递失败的消息
     *
     * @return
     */
    @Bean
    public JobDetail checkMessageJobDetail() {
        return JobBuilder.newJob(MessageLogJob.class).withIdentity("MessageJobDetail", "Message").storeDurably().build();
    }

    /**
     * Trigger
     * 定时检查投递失败的消息
     * 每隔2分钟检查一次
     *
     * @return
     */
    @Bean
    public Trigger checkMessageTrigger() {
        logger.info("定时MessageLogJob任务开启，每隔2分钟检查一次投递失败的消息");
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/2 * * * ?");
        return TriggerBuilder.newTrigger().forJob(checkMessageJobDetail())
                .withIdentity("MessageJobTrigger", "Message")
                .withSchedule(scheduleBuilder)
                .build();
    }
}

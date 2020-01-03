package org.javaboy.quartz.config;

import org.javaboy.quartz.job.MyJob2;
import org.javaboy.quartz.service.HelloService;
import org.quartz.JobDataMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

import java.util.Date;

@Configuration
public class QuartzConfig {
    /**
     * 方法 一 工厂bean，设置job 和method
     * 这是普通定时 不可传参
     * @return
     */
//    @Bean
//    MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean() {
//        MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
//        bean.setTargetBeanName("myJob1");
//        bean.setTargetMethod("sayHello");
//        return bean;
//    }

    /**
     * 方法 二 工厂bean，类，方法
     * 可传参任务是（JobDetailFactoryBean）
     * @return
     */
    @Bean
    JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean bean = new JobDetailFactoryBean();
        bean.setJobClass(MyJob2.class);
        JobDataMap map = new JobDataMap();
        map.put("helloService", helloService());
        map.put("say","I want to say hello");
        bean.setJobDataMap(map);
        return bean;
    }

    /**
     * 将方法一 定时信息装配到触发器工厂bean，同时将方法一的实例也装备到此bean中
     * @return
     */
//    @Bean
//    SimpleTriggerFactoryBean simpleTriggerFactoryBean() {
//        SimpleTriggerFactoryBean bean = new SimpleTriggerFactoryBean();
//        bean.setStartTime(new Date());
//        bean.setRepeatCount(5);
//        bean.setJobDetail(methodInvokingJobDetailFactoryBean().getObject());
//        bean.setRepeatInterval(3000);
//        return bean;
//    }

    /**
     * 设置方法二的定时信息，及方法二的实体bean
     * @return
     */
    @Bean
    CronTriggerFactoryBean cronTrigger() {
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setCronExpression("0/10 * * * * ?");
        bean.setJobDetail(jobDetailFactoryBean().getObject());
        return bean;
    }

    /**
     * 触发器，也就是启动
     * @return
     */
    @Bean
    SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setTriggers(cronTrigger().getObject());
        return bean;
    }

    /**
     * 因为此处已经将 HelloService 装配到spring容器，所以service中不能再将此类用@Service注入spring容器
     * @return
     */
    @Bean
    HelloService helloService() {
        return new HelloService();
    }
}

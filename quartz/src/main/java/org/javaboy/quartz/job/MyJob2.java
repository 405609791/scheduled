package org.javaboy.quartz.job;

import org.javaboy.quartz.service.HelloService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class MyJob2 extends QuartzJobBean {
    HelloService helloService;
    public HelloService getHelloService() {
        return helloService;
    }
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * 可传参任务必须继承QuartzJobBean，重写protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException方法，
     * 其中JobExecutionContext就是我们在定义调度器明细时传入参数的上下文，我们可以通过JobExecutionContext取出传入的map，调度任务最终执行的就是executeInternal方法，
     * 使用该调度明细任务无法使用自定义方法。
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        String say = mergedJobDataMap.get("say").toString();
        helloService.sayHello(say);
    }

}

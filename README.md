定时任务的2种方法：Scheduled、Quartz
# scheduled
# 注 在启动类上 须要加开启定时的注解 @EnableScheduling
#
    @Scheduled(fixedRate = 2000)
    public void fixedRate() {
        System.out.println("fixedRate>>>"+new Date());    
    }
    @Scheduled(fixedDelay = 2000)
    public void fixedDelay() {
        System.out.println("fixedDelay>>>"+new Date());
    }
    @Scheduled(initialDelay = 2000,fixedDelay = 2000)
    public void initialDelay() {
        System.out.println("initialDelay>>>"+new Date());
    }
	1、首先使用 @Scheduled 注解开启一个定时任务。
	2、fixedRate 表示任务执行之间的时间间隔，具体是指两次任务的开始时间间隔，即第二次任务开始时，第一次任务可能还没结束。
	3、fixedDelay 表示任务执行之间的时间间隔，具体是指本次任务结束到下次任务开始之间的时间间隔。
	4、initialDelay 表示首次任务启动的延迟时间。
	5、所有时间的单位都是毫秒。
	除了这几个基本属性之外，@Scheduled 注解也支持 cron 表达式，
	使用 cron 表达式，可以非常丰富的描述定时任务的时间。cron 表达式格式如下：
	[秒] [分] [小时] [日] [月] [周] [年]
	序号	说明	是否必填	允许填写的值	允许的通配符
	1	秒	是	0-59	- * /
	2	分	是	0-59	- * /
	3	时	是	0-23	- * /
	4	日	是	1-31	- * ? / L W
	5	月	是	1-12 or JAN-DEC	- * /
	6	周	是	1-7 or SUN-SAT	- * ? / L #
	7	年	否	1970-2099	- * /
	这一块需要大家注意的是，月份中的日期和星期可能会起冲突，因此在配置时这两个得有一个是 ?
	
	通配符含义：
	? 表示不指定值，即不关心某个字段的取值时使用。需要注意的是，月份中的日期和星期可能会起冲突，因此在配置时这两个得有一个是 ?
	* 表示所有值，例如:在秒的字段上设置 *,表示每一秒都会触发
	, 用来分开多个值，例如在周字段上设置 “MON,WED,FRI” 表示周一，周三和周五触发
	- 表示区间，例如在秒上设置 “10-12”,表示 10,11,12秒都会触发
	/ 用于递增触发，如在秒上面设置”5/15” 表示从5秒开始，每增15秒触发(5,20,35,50)
	# 序号(表示每月的第几个周几)，例如在周字段上设置”6#3”表示在每月的第三个周六，(用 在母亲节和父亲节再合适不过了)
	周字段的设置，若使用英文字母是不区分大小写的 ，即 MON 与mon相同
	L 表示最后的意思。在日字段设置上，表示当月的最后一天(依据当前月份，如果是二月还会自动判断是否是润年), 在周字段上表示星期六，相当于”7”或”SAT”（注意周日算是第一天）。如果在”L”前加上数字，则表示该数据的最后一个。例如在周字段上设置”6L”这样的格式,则表示”本月最后一个星期五”
	W 表示离指定日期的最近工作日(周一至周五)，例如在日字段上设置”15W”，表示离每月15号最近的那个工作日触发。如果15号正好是周六，则找最近的周五(14号)触发, 如果15号是周未，则找最近的下周一(16号)触发，如果15号正好在工作日(周一至周五)，则就在该天触发。如果指定格式为 “1W”,它则表示每月1号往后最近的工作日触发。如果1号正是周六，则将在3号下周一触发。(注，”W”前只能设置具体的数字,不允许区间”-“)
	L 和 W 可以一组合使用。如果在日字段上设置”LW”,则表示在本月的最后一个工作日触发(一般指发工资 )
	
# Quartz 定时 需加依赖：

		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
# 同样需要添加开启定时任务的注解 @EnableScheduling
Quartz 在使用过程中，有两个关键概念，一个是JobDetail（要做的事情），
另一个是触发器（什么时候做），要定义 JobDetail，需要先定义 Job，Job 的定义有两种方式：
第一种方式，直接定义一个Bean：
@Component
public class MyJob1 {
    public void sayHello() {
        System.out.println("MyJob1>>>"+new Date());
    }
}
关于这种定义方式说两点：

1、首先将这个 Job 注册到 Spring 容器中。
2、这种定义方式有一个缺陷，就是无法传参。

第二种定义方式，则是继承 QuartzJobBean 并实现默认的方法：
public class MyJob2 extends QuartzJobBean {
    HelloService helloService;
    public HelloService getHelloService() {
        return helloService;
    }
    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        helloService.sayHello();
    }
}
public class HelloService {
    public void sayHello() {
        System.out.println("hello service >>>"+new Date());
    }
}
和第1种方式相比，这种方式支持传参，任务启动时，executeInternal 方法将会被执行。
Job 有了之后，接下来创建类，配置 JobDetail 和 Trigger 触发器，如下：

@Configuration
public class QuartzConfig {
    @Bean
    MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean();
        bean.setTargetBeanName("myJob1");
        bean.setTargetMethod("sayHello");
        return bean;
    }
    @Bean
    JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean bean = new JobDetailFactoryBean();
        bean.setJobClass(MyJob2.class);
        JobDataMap map = new JobDataMap();
        map.put("helloService", helloService());
        bean.setJobDataMap(map);
        return bean;
    }
    @Bean
    SimpleTriggerFactoryBean simpleTriggerFactoryBean() {
        SimpleTriggerFactoryBean bean = new SimpleTriggerFactoryBean();
        bean.setStartTime(new Date());
        bean.setRepeatCount(5);
        bean.setJobDetail(methodInvokingJobDetailFactoryBean().getObject());
        bean.setRepeatInterval(3000);
        return bean;
    }
    @Bean
    CronTriggerFactoryBean cronTrigger() {
        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();
        bean.setCronExpression("0/10 * * * * ?");
        bean.setJobDetail(jobDetailFactoryBean().getObject());
        return bean;
    }
    @Bean
    SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setTriggers(cronTrigger().getObject(), simpleTriggerFactoryBean().getObject());
        return bean;
    }
    @Bean
    HelloService helloService() {
        return new HelloService();
    }
}
关于这个配置说如下几点：

JobDetail 的配置有两种方式：MethodInvokingJobDetailFactoryBean 和 JobDetailFactoryBean 。
使用 MethodInvokingJobDetailFactoryBean 可以配置目标 Bean 的名字和目标方法的名字，这种方式不支持传参。
使用 JobDetailFactoryBean 可以配置 JobDetail ，任务类继承自 QuartzJobBean ，这种方式支持传参，将参数封装在 JobDataMap 中进行传递。
Trigger 是指触发器，Quartz 中定义了多个触发器，这里向大家展示其中两种的用法，SimpleTrigger 和 CronTrigger 。
SimpleTrigger 有点类似于前面说的 @Scheduled 的基本用法。
CronTrigger 则有点类似于 @Scheduled 中 cron 表达式的用法。


@bean和@component

Spring帮助我们管理Bean分为两个部分，一个是注册Bean，一个装配Bean。
完成这两个动作有三种方式，
一种是使用自动配置的方式、一种是使用JavaConfig的方式，一种就是使用XML配置的方式

@Component
1.1 注解表明一个类会作为组件类，并告知Spring要为这个类创建bean，（@Controller,@Service, @Repository实际上都包含了@Component注解）
@Bean
2.1 用在方法上，一般有返回值，@Bean注解告诉Spring这个方法将会返回一个对象，这个对象要注册为Spring应用上下文中的bean。通常方法体中包含了最终产生bean实例的逻辑
2.2 第三方的类，如果要注册到spring中，一般用bean的方式

总结：@Component和@Bean都是用来注册Bean并装配到Spring容器中，但是Bean比Component的自定义性更强。可以实现一些Component实现不了的自定义加载类。

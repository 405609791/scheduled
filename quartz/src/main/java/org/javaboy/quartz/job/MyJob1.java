package org.javaboy.quartz.job;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyJob1 {
    public void sayHello() {
        System.out.println("MyJob1>>>"+new Date());
    }
}

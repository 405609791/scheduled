package org.javaboy.quartz.service;

import org.springframework.stereotype.Service;

import java.util.Date;


public class HelloService {
    public void sayHello(String say) {
        System.out.println("hello service >>>"+say+new Date());
    }
}

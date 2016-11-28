package io.netty.mvc.service;

import org.springframework.stereotype.Service;

import io.netty.mvc.model.Demo;


@Service
public class DemoService {

    public Demo selectDB() {
    	return new Demo(1, "test");
    }
}
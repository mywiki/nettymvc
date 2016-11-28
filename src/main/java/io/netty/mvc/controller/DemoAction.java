package io.netty.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.netty.mvc.model.Demo;
import io.netty.mvc.service.DemoService;

@Controller
@RequestMapping(value = "/demo",name = "DEMO示例ACTION")
public class DemoAction {

    @Autowired
    DemoService demoService;

    /**
     * 返回JSON数据
     * @param test
     * @param www
     * @return
     */
    @RequestMapping(name = "接受参数，返回Json数据",value = "/json",method = RequestMethod.GET)
    @ResponseBody
    public Demo json(String json,String www){
        return demoService.selectDB();
    }
    
    @RequestMapping(name = "使用@RequestBody接受数据",value = "/body",method = RequestMethod.POST)
    @ResponseBody
    public String body(@RequestBody String body){
        return body;
    }
}
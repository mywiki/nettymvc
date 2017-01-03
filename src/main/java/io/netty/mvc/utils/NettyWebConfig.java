package io.netty.mvc.utils;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages="org.inull.netty4.springmvc")
public class NettyWebConfig extends WebMvcConfigurerAdapter {
	
}

package com.my.springboot4demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@ImportHttpServices(basePackages = "com.my.springboot4demo.proxy.client")
public class SpringBoot4DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot4DemoApplication.class, args);
	}

}

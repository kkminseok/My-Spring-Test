package com.my.springboot4demo.proxy.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:8080/proxy/test")
public interface ProxyClientService {

    @GetExchange
    String proxy(@RequestParam ("name") String name);
}

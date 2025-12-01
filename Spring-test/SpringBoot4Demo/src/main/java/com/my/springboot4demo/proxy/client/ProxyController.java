package com.my.springboot4demo.proxy.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@RestController
public class ProxyController {

    @GetMapping("/")
    public String testProxy(@RequestParam("name") String name) {
        RestClient restClient = RestClient.create();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                .build();
        return factory.createClient(ProxyClientService.class).proxy(name);
    }

    @GetMapping("/proxy/test")
    public String test(@RequestParam("name") String name) {
        return "Hello, " + name + "!";
    }
}

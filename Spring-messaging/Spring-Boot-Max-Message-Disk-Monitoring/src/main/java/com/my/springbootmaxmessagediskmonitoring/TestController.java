package com.my.springbootmaxmessagediskmonitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final LargeMessageProducer largeMessageProducer;

    // http://localhost:8080/test
    @GetMapping("/test")
    public String test() {
        largeMessageProducer.sendBulk();
        return "OK";
    }
}

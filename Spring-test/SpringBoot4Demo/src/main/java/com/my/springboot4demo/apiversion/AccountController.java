package com.my.springboot4demo.apiversion;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account/{id}")
public class AccountController {

    @GetMapping
    public String getAccount() {
        return "Account details";
    }

    @GetMapping(version = "1.1")
    public String getAccountV1_1(@PathVariable("id") String id) {
        return "Account details for version 1.1, ID: " + id;
    }

    @GetMapping(version = "2.0")
    public String getAccountV2_0(@PathVariable("id") String id) {
        return "Account details for version 2.0, ID: " + id;
    }
}

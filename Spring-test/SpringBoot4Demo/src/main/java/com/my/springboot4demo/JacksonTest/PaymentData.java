package com.my.springboot4demo.JacksonTest;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PaymentData {
    private String id;

    @JsonIgnore
    private String sensitiveData;

    // getters, setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSensitiveData() { return sensitiveData; }
    public void setSensitiveData(String sensitiveData) { this.sensitiveData = sensitiveData; }
}
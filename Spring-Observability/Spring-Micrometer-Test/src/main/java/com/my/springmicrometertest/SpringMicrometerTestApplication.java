package com.my.springmicrometertest;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringMicrometerTestApplication {

	@Bean
	public MeterRegistry basicMeterRegistry() {
		return new LoggingMeterRegistry();
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringMicrometerTestApplication.class, args);
	}

}

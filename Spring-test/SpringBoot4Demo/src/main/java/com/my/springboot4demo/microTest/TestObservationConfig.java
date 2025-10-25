package com.my.springboot4demo.microTest;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class TestObservationConfig implements ObservationHandler<Observation.Context> {

    private final List<Observation.Context> capturedContexts = new ArrayList<>();

    @Override
    public void onStart(Observation.Context context) {
        log.info("Observation started: {}", context.getName());
        capturedContexts.add(context);
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("Observation stopped: {} with tags: {}", context.getName(), context.getAllKeyValues());
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    public List<Observation.Context> getCapturedContexts() {
        return new ArrayList<>(capturedContexts);
    }

    public void clear() {
        capturedContexts.clear();
    }
}
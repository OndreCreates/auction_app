package com.ondrecreates.auctionapp.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    // Injected wherever "now" matters so tests can swap in a fixed Clock instead of Thread.sleep.
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

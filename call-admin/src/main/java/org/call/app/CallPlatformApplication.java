package org.call.app;

import org.call.common.IDWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = {"org.call.app","org.call.api","org.call.mq","org.call.config", "org.call.common", "org.call.pojo"})
@Configuration
@EnableDiscoveryClient
public class CallPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(CallPlatformApplication.class, args);
    }

    @Bean
    public static IDWorker idWorker() {
        return new IDWorker(1, 2);
    }
}

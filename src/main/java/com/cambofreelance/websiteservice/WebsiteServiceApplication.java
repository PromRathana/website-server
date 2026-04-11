package com.cambofreelance.websiteservice;

//import com.cambofreelance.lotteryserver.startup.Startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class WebsiteServiceApplication {
//    private final Startup startup;

    public WebsiteServiceApplication() {
//        this.startup = startup;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebsiteServiceApplication.class, args);
    }

//    @PostConstruct
//    private void init() {
//        log.info("Initializing api route Manager ...");
//        startup.initApiMigrate();
//        log.info("Finished api route Manager ...");
//    }

}

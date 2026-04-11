//package com.cambofreelance.lotteryserver.startup;
//
//import com.cambofreelance.lotteryserver.registry.ApiMigrateRegistry;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class Startup {
//
//    private final ApiMigrateRegistry apiMngRegistry;
//
//    private void init() {
//        apiMngRegistry.loadComponentInit();
//    }
//
//    public void initApiMigrate() {
//        log.info("Setting up Api route Manager context ...");
//        this.init();
//    }
//}
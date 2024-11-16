package com.walnut.sparta;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// Dummy boot for spring to found the main classpath.
@SpringBootApplication
public class SpartaBoot {
    //    @PostConstruct
//    public void init() {
//        try {
//            ShadowBoot.shadow.vitalize();
//        }
//        catch ( Exception e ) {
//            ShadowBoot.shadow.console().error( e.getMessage() );
//        }
//    }
//    public static void main(String[] args) {
//        SpringApplication.run(SpartaBoot.class, args);
//    }
}

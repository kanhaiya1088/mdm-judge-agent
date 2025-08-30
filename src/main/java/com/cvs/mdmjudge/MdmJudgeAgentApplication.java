package com.cvs.mdmjudge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableCaching
public class MdmJudgeAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MdmJudgeAgentApplication.class, args);
    }
}

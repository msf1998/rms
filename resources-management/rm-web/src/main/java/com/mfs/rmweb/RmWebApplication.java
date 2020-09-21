package com.mfs.rmweb;

import javafx.application.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.mfs.rmcore.mapper")
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.mfs.rmcore","com.mfs.rmweb"})
public class RmWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RmWebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}

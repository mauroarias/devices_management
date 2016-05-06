package org.etcsoft.devicemanagement;

import com.sun.scenario.effect.Blend;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * Created by mauro on 04/04/16.
 */

@SpringBootApplication()
//It's equivalent to:
//  @Configuration
//  @EnableAutoConfiguration
//  @ComponentScan(value = "org.etcsoft")
//    all components are loaded automatically: @Component, @Service, @Repository, @Controller
public class DeviceApp {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DeviceApp.class);
//        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

//or use a builder
//        new SpringApplicationBuilder().bannerMode(Banner.Mode.OFF).run(args);

//Or directly run
//        SpringApplication.run(DeviceApp.class, args);
    }
}
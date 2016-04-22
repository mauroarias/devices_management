package org.etcsoft.devicemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by mauro on 04/04/16.
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(value = "org.etcsoft")
public final class DeviceApp {

    public static void main(String[] args) {
        SpringApplication.run(DeviceApp.class, args);
    }
}
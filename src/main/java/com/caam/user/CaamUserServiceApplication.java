package com.caam.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = { "com.caam" })
@EnableJpaRepositories("com.caam")
@EntityScan("com.caam")
@ComponentScan("com.caam")
@PropertySource(value = { "classpath:application.properties" })
@PropertySource(value = { "classpath:application.yml" })
@EnableFeignClients(basePackages = { "com.caam" })

public class CaamUserServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CaamUserServiceApplication.class, args);

	}

	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CaamUserServiceApplication.class);
	}



}

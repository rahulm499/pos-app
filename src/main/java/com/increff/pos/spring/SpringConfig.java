package com.increff.pos.spring;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@Configuration
@ComponentScan("com.increff.pos")
@PropertySources({ //
		@PropertySource(value = "file:./pos.properties", ignoreResourceNotFound = true), //
		@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true)
})
public class SpringConfig {


}

package com.increff.pos.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AboutAppApi {

	@Value("${app.name}")
	private String name;
	@Value("${app.version}")
	
	private String version;
	
	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

}

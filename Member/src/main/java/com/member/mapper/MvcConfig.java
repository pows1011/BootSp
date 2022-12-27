package com.member.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{

	
	@Value("${file.findPath}")
	private String findpath;
	@Value("${file.review}")
	private String reviewpath;
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/stsimg/**")
		.addResourceLocations(findpath);				
		
		registry.addResourceHandler("/stsreview/**")
		.addResourceLocations(reviewpath);
		
	}
	
	
	
}

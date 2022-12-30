package com.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{ 
	// WebMvcConfigurer = Spring에서 기본 제공하는 자동 설정 외에 추가적으로 설정을 자동화 할 때 사용
	// Spring에서 제공하는 Bean 설정자(~Configurer로 끝나는 Interface)를 Class로 구현하고 @Configuration을 통해 Bean으로 등록
	// 메서드 접근제한자가 default로 선언되어 있어서 필요한 메서드만 재정의해서 사용할 수 있다.
	@Value("${file.findPath}")
	private String findpath;
	@Value("${file.review}")
	private String reviewpath;
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 정적 리소스를 설정해주는 메소드
		registry.addResourceHandler("/stsimg/**") 	//리소스 등록 및 핸들러를 관리하는 객체인 ResourceHandlerRegistry를 통해 리소스 위치와 이 리소스와 매칭될 url을 등록합니다. 
		.addResourceLocations(findpath); 		 	//stsimg로 시작하는url로 요청을 할경우
													//프로퍼티에 설정해둔 file:///stsimg/ 값으로 인해서
													//리눅스 root(최상위 경로)에서 시작하는 폴더경로 > stsimg의 폴더에 있는 파일을 불러온다
		
		registry.addResourceHandler("/stsreview/**")
		.addResourceLocations(reviewpath);
		
	}
	
	
	
}

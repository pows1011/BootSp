package com.member.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	    		.authorizeHttpRequests()	
	            .requestMatchers("/").permitAll()
	            .requestMatchers(
	                    "/css/**",
	                    "/js/**",
	                    "/images/**",
	                    "/webjars/**",
	                    "/favicon.*",
	                    "/*/icon-*"
	            ).permitAll()
	            .requestMatchers("/mypage").hasRole("USER")
	            .requestMatchers("/message").hasRole("MANAGER")
	            .requestMatchers("/config").hasRole("ADMIN")
	            .anyRequest().authenticated();

	    http
	            .formLogin();

	    return http.build();
	}
	

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
    }

}

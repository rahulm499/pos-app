package com.increff.pos.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static Logger logger = Logger.getLogger(SecurityConfig.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http//
			// Match only these URLs
				.requestMatchers()//
				.antMatchers("/api/**")//
				.antMatchers("/ui/**")//
				.and().authorizeRequests()//
				.antMatchers("/ui/admin").hasAnyAuthority("supervisor")//
				.antMatchers("/ui/**").hasAnyAuthority("supervisor", "operator")//
				.antMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority("supervisor", "operator")
				.antMatchers("/api/order", "/api/order/**", "/api/order-item", "/api/order-item/**").hasAnyAuthority("supervisor", "operator")
				.antMatchers("/api/invoice", "/api/invoice/**").hasAnyAuthority("supervisor", "operator")
				.antMatchers("/api/sales-report", "/api/inventory-report", "/api/daily-report", "/api/brand-report").hasAnyAuthority("supervisor", "operator")
				.antMatchers("/api/sales-report/**", "/api/inventory-report/**", "/api/daily-report/**", "/api/brand-report/**").hasAnyAuthority("supervisor", "operator")
				.antMatchers("/api/**").hasAnyAuthority("supervisor")//
				.anyRequest().authenticated()
				// Ignore CSRF and CORS
				.and().csrf().disable().cors().disable()
				.exceptionHandling()
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().write("{ \"error\": \"You do not have access to this.\" }");

				});
		logger.info("Configuration complete");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
				"/swagger-ui.html", "/webjars/**");
	}

}

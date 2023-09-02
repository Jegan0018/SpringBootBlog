package com.jegan.blogapplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcDaoImpl jdbcUserDetailsService = new JdbcDaoImpl();
        jdbcUserDetailsService.setDataSource(dataSource);
        jdbcUserDetailsService.setUsersByUsernameQuery("SELECT name, password,active as enabled FROM users WHERE name = ?");
        jdbcUserDetailsService.setAuthoritiesByUsernameQuery("SELECT name, role FROM users WHERE name = ?");
        return jdbcUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/comments/save").permitAll()
                                .requestMatchers("/blogs/showFormForAdd").authenticated()
                                .requestMatchers("/blogs/showFormForDrafts").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(form ->
                        form
                                .loginPage("/users/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/blogs/list",true)
                                .permitAll()
                )
                .logout(logout -> logout.permitAll()
                );
        return http.build();
    }
}

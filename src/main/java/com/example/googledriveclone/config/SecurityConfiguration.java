package com.example.googledriveclone.config;

import com.example.googledriveclone.services.Impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/", "/login", "/static/**").permitAll()
                        .anyRequest().authenticated()
                  )
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform-login")
                .usernameParameter("user")
                .passwordParameter("pass")
                .defaultSuccessUrl("/profile")
                .permitAll()
                .and()
                .logout()
                .permitAll();

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
        return authenticationManagerBuilder.build();
    }

}

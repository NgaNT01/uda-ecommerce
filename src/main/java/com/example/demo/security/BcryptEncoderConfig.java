package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BcryptEncoderConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoderBean() {
        return new BCryptPasswordEncoder();
    }
}

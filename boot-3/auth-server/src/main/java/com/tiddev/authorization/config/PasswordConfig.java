package com.tiddev.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * @author : Yaser(Amin) sadeghi
 */
@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    public static class NoOpPasswordEncoder implements PasswordEncoder {

        private static final PasswordEncoder INSTANCE = new NoOpPasswordEncoder();

        private NoOpPasswordEncoder() {
        }

        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }

        public static PasswordEncoder getInstance() {
            return INSTANCE;
        }

    }

}

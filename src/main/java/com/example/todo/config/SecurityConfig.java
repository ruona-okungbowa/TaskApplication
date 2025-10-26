package com.example.todo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * Configuration class for security settings in the application.
 *
 * <p>This class defines various security configurations, including
 * authentication, authorization, password encoding, and support for
 * advanced HTTP methods like PUT and DELETE in forms. It utilizes
 * Spring Security features to ensure secure access to application resources.</p>
 *
 * <h3>Key Components:</h3>
 * <ul>
 *   <li><strong>SecurityFilterChain:</strong> Configures which resources are publicly accessible,
 *       sets up custom login and logout pages, and enforces authentication for restricted URLs.</li>
 *   <li><strong>Password Encoder:</strong> Provides a {@link BCryptPasswordEncoder}
 *       to securely hash user passwords with configurable strength.</li>
 *   <li><strong>Hidden HTTP Method Filter:</strong> Enables the use of advanced HTTP methods
 *       in HTML forms, like PUT and DELETE, by translating a hidden field's value into the desired HTTP method.</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <p>This configuration is designed for a typical web application. Adjustments may be required
 * for production environments, particularly with regard to CSRF protection and API security.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Defines the strength level for the password encoder.
     *
     * <p>This constant specifies the computational complexity of the
     * password hashing process used by the password encoder. The strength
     * value influences the time required to hash passwords, providing a balance
     * between security and performance.</p>
     *
     * <h3>Key Details:</h3>
     * <ul>
     *   <li><strong>Value:</strong> Set to <code>4</code>, representing a moderate
     *       level of computational effort.</li>
     *   <li><strong>Usage:</strong> Used to configure the {@link BCryptPasswordEncoder}
     *       or similar password encoding mechanisms.</li>
     *   <li><strong>Security:</strong> Higher values increase security but require
     *       more processing time, making brute-force attacks more difficult.</li>
     * </ul>
     *
     * <p><strong>Note:</strong> Consider adjusting the strength value based on the
     * security requirements and processing capabilities of your application.</p>
     */
    public static final int PASSWORD_ENCODER_STRENGTH = 4;
    private final UserDetailsService jpaUserDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/login", "/register", "/").permitAll()
                                .requestMatchers("/api/tasks/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/",true)
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                )
                .userDetailsService(jpaUserDetailsService)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){return  new HiddenHttpMethodFilter();}

}

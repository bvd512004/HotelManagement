package com.hsf302.hotelmanagement.config;

import com.hsf302.hotelmanagement.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .addFilterBefore(sessionAuthenticationFilter(), AuthorizationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                // Static resources và error pages - public
                .requestMatchers("/css/**", "/js/**", "/images/**", "/error/**").permitAll()

                // Public URLs - không cần đăng nhập
                .requestMatchers("/", "/index", "/booking", "/booking-details",
                                "/booking/complete", "/booking/success", "/login", "/logout",
                                "/gallery", "/rooms", "/rooms/gallery", "/rooms/rooms").permitAll()
                
                // Manager URLs - chỉ Manager hoặc Admin
                .requestMatchers("/manager/**", "/rooms/manager/**", "/reservations/manager/**")
                    .hasAnyRole("MANAGER", "ADMIN")
                
                // Admin URLs - chỉ Admin
                .requestMatchers("/users/**", "/rooms/create",
                                "/rooms/edit/**", "/rooms/save").hasRole("ADMIN")
                                   
                    .requestMatchers("/rooms/list", "/tasks-management/**").hasAnyRole("ADMIN","RECEPTIONIST")
                
                // Housekeeping Staff URLs - chỉ HouseKeeping Staff
                .requestMatchers("/tasks/**").hasAnyRole("HOUSEKEEPING_STAFF","RECEPTIONIST")
                
                // Receptionist URLs - Receptionist hoặc Admin
                .requestMatchers("/receptionist/**", "/reservations/**","/room/")
                    .hasAnyRole("RECEPTIONIST", "ADMIN")
                
                // Manager URLs
                .requestMatchers("/manager/**","/rooms").hasRole("MANAGER")

                // Reservations can be accessed by multiple roles
                .requestMatchers("/reservations/**").hasAnyRole("MANAGER", "RECEPTIONIST", "ADMIN")

                // API requests - public (có thể cần điều chỉnh)
                .requestMatchers("/api/**").permitAll()

                // Mặc định - public
                .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/login?error=access_denied");
                })
            );

        return http.build();
    }

    /**
     * Filter để đưa user từ session vào Spring Security context
     * Để có thể sử dụng hasRole() trong authorizeHttpRequests()
     */
    @Bean
    public OncePerRequestFilter sessionAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                    throws ServletException, IOException {
                
                HttpSession session = request.getSession(false);
                
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    String role = (String) session.getAttribute("role");
                    
                    if (user != null && role != null) {

                        String springSecurityRole = convertRoleToSpringSecurityRole(role);
                        
                        // Tạo authentication object và đưa vào SecurityContext
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                user.getUserName(),
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + springSecurityRole))
                            );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                
                chain.doFilter(request, response);
            }
            
            private String convertRoleToSpringSecurityRole(String role) {
                if ("Admin".equals(role)) {
                    return "ADMIN";
                } else if ("HouseKeeping Staff".equals(role)) {
                    return "HOUSEKEEPING_STAFF";
                } else if ("Receptionist".equals(role)) {
                    return "RECEPTIONIST";
                } else if ("Manager".equals(role)) {
                    return "MANAGER";
                }
                return role.toUpperCase().replace(" ", "_");
            }
        };
    }
}

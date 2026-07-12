package com.example.Proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Proyecto.security.AppUserDetails;
import com.example.Proyecto.security.JwtAuthenticationFilter;
import com.example.Proyecto.service.PasswordService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final PasswordService passwordService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/", "/index", "/index.html", "/menu", "/nosotros", "/css/**", "/img/**", "/uploads/**", "/api/registro", "/api/auth/token", "/cart/**").permitAll()
              .requestMatchers("/admin/**").hasRole("ADMIN")
              .anyRequest().authenticated())
          .formLogin(login -> login
              .loginProcessingUrl("/api/login")
              .usernameParameter("correo")
              .passwordParameter("contrasena")
              .successHandler((request, response, authentication) -> {
                  AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
                  request.getSession().setAttribute("usuarioLogueado", user.cliente());
                  response.sendRedirect(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ? "/admin" : "/?login=exitoso");
              })
              .failureHandler((request, response, ex) -> response.sendRedirect("/?login=error"))
              .permitAll())
          .logout(logout -> logout.logoutUrl("/api/logout").logoutSuccessHandler((req, res, auth) -> res.sendRedirect("/?logout=exitoso")))
          .exceptionHandling(errors -> errors.accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN)))
          .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/token"))
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            public String encode(CharSequence raw) { return passwordService.hash(raw.toString()); }
            public boolean matches(CharSequence raw, String encoded) { return passwordService.matches(raw.toString(), encoded); }
            public boolean upgradeEncoding(String encoded) { return passwordService.needsRehash(encoded); }
        };
    }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { return config.getAuthenticationManager(); }
}

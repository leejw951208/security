package demo.security.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import demo.security.global.security.filter.JwtAuthenticationEntryPoint
import demo.security.global.jwt.JwtProperties
import demo.security.global.jwt.JwtProvider
import demo.security.global.security.filter.JwtAuthenticationFilter
import demo.security.global.security.filter.JwtExceptionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtProperties: JwtProperties,
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity> -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .formLogin { formLogin: FormLoginConfigurer<HttpSecurity> -> formLogin.disable() }
            .httpBasic { httpBasic: HttpBasicConfigurer<HttpSecurity> -> httpBasic.disable() }
            .addFilterBefore(JwtAuthenticationFilter(jwtProperties, jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(JwtExceptionFilter(objectMapper), JwtAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("/signup/**", "/signin", "/signin/**", "/jwt/**").permitAll()
                authorize.anyRequest().authenticated()
            }
            .exceptionHandling { exception -> exception.authenticationEntryPoint(JwtAuthenticationEntryPoint()) }
        return http.build();
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
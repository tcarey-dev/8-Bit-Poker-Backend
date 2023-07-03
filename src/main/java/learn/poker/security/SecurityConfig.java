package learn.poker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ConditionalOnWebApplication
//@ComponentScan
public class SecurityConfig {

    private final JwtConverter converter;

    public SecurityConfig(JwtConverter converter) {
        this.converter = converter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {

        http.csrf().disable();

        http.cors();

        http.authorizeRequests()
                .antMatchers("/api/player/authenticate").permitAll()
                .antMatchers("/api/player/refresh-token").authenticated()
                .antMatchers("/api/player/create-account").permitAll()
                .antMatchers(HttpMethod.GET, "/api/player", "/api/player/*").authenticated()
                .antMatchers(HttpMethod.POST, "/api/player").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/player/*").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/player/*").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/room", "/api/room/*").authenticated()
                .antMatchers(HttpMethod.POST, "/api/room").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/room/*").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/room/*").hasAuthority("ADMIN")
//                .antMatchers("/ws").permitAll()
//                .antMatchers("/app").permitAll()
//                .antMatchers("/topic").permitAll()
//                .antMatchers("/topic/**").permitAll()
                .antMatchers("/api/**").denyAll()
                .and()
                .addFilter(new JwtRequestFilter(authenticationManager(authConfig), converter))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
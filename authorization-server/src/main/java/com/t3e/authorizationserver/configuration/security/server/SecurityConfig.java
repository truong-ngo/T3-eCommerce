package com.t3e.authorizationserver.configuration.security.server;

import com.t3e.authorizationserver.configuration.security.authorization.CustomOidcLogoutEndpointFilter;
import com.t3e.authorizationserver.configuration.security.authorization.JdbcOAuth2AuthorizationServiceExtends;
import com.t3e.authorizationserver.configuration.security.authorization.LazyAuthenticationManager;
import com.t3e.authorizationserver.configuration.security.authorization.PostLogoutRedirectUriLogoutSuccessHandler;
import com.t3e.authorizationserver.configuration.security.social.SocialOAuth2AuthenticationSuccessHandler;
import com.t3e.authorizationserver.configuration.security.social.SocialOauth2UserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationContext context;
    private final CorsConfigurationSource configurationSource;

    private final JdbcOAuth2AuthorizationServiceExtends oAuth2AuthorizationServiceExtends;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()))
                .addFilterAfter(customOidcLogoutEndpointFilter(http), CsrfFilter.class);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**")
                .securityMatcher("/register/**")
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/authenticated/**").permitAll()
                        .requestMatchers("/register/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(conf -> conf.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .anyRequest().authenticated())
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll())
                .oauth2Login(conf -> conf
                        .successHandler(successHandler())
                        .loginPage("/login"))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(conf -> conf.configurationSource(configurationSource))
                .build();
    }

    public CustomOidcLogoutEndpointFilter customOidcLogoutEndpointFilter(HttpSecurity http) {
        CustomOidcLogoutEndpointFilter customOidcLogoutEndpointFilter = new CustomOidcLogoutEndpointFilter(new LazyAuthenticationManager(http));
        customOidcLogoutEndpointFilter.setLogoutSuccessHandler(new PostLogoutRedirectUriLogoutSuccessHandler(oAuth2AuthorizationServiceExtends));
        return customOidcLogoutEndpointFilter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true)
                .ignoring()
                .requestMatchers("/webjar/**", "/images/**", "/css/**", "/assets/**", "/favicon.ico");
    }

    @Bean
    @SuppressWarnings("all")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SocialOauth2UserHandler socialOauth2UserHandler = context.getBean(SocialOauth2UserHandler.class);
        return new SocialOAuth2AuthenticationSuccessHandler(socialOauth2UserHandler);
    }
}
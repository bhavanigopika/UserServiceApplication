package com.example.userserviceapplication.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/*
Earlier we do the manual code(randomly taken token value) and we do it in user service.
Now, we are going to do the OAuth implementation where the token gets created automatically.
Actually, in spring security itself will do it without manual but, it needs OAuth2. Spring Authorization server, add dependency then work.
The following code helps to learn about OAUth 2.0 and the code automatically redirected to login page
The following represents to create the token and save the token ourselves. Code from line no: 49 to line no: 169. Such a long code taken from Spring Authorization Server: https://docs.spring.io/spring-authorization-server/reference/getting-started.html
Finally, received authentication token(access token) from authorization server
Since this access token or self validating token (i.e) stateless. So, need to store the token in back end = These are internally handled by spring security
We used postman to show Authentication successful and, we get the access token
*/
/*
Now, Let's implement few API and show how to use spring security and remove all the savings of token and authorize our APIs.
Also, let's see scope level details...
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
                )
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                )
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    //define on extra bean named as apiSecurityFilterChain
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                /*let us define security matcher explicitly now to match our API request and authorize depending on our matches*/
                .securityMatcher("/users/**")//only match our API request
                .authorizeHttpRequests(authorize -> authorize

                        /*let we also authorize depending on our matches and permit all for users/signup means allow signup without token*/
                        .requestMatchers("/users/signup").permitAll()
                        /*but if the request matchers /users/admin/anything else -> it has to have "hasAuthority" which is "SCOPE_ADMIN" -> then allow it to particular request*/
                        .requestMatchers("/users/admin/**").hasAuthority("SCOPE_ADMIN")
                        /*all other request apart from above requestMatchers should authenticate which means all other end points will require JWT*/
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable());
                //Change the above 2 line with lambda method reference
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /*
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
          throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated()
            )
        // Form login handles the redirect to the login page from the
        // authorization server filter chain
        .formLogin(Customizer.withDefaults());

        return http.build();
    }
   */

    //Earlier the code as like above. Now, change it as below
    //Add filter chain for login page and static resources
    //Any other not related API request (i.e) default Security Filter Chain the go to this endpoint.
    //how we say not related API request? Because API request starts with "/users"(Initial end point). In default Security Filter Chain, we don't said as "/users" API
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(authorize -> authorize
                        //if request matchers with login, error or css etc., then permit all
                        .requestMatchers("/login", "/error", "/css/**", "/js/**").permitAll()
                        //then it should be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder()/*withDefaultPasswordEncoder()*/
                .username("user")
                /*.password("password")*/
                //provide bcrypt password for "password"...Get it from bcrypt hash generator website
                .password("$2a$12$9JKvv/R3PXl95.edTdUeGup/9PpWNQ2J5V7EdktzuPTXEqSCwGPP2")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    //The following line store it in-memory db, if you want to actual database. Then refer this docx.
    //https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html -> How-to: Implement core services with JPA

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                /*.redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")*/
                //change redirect uri to postman
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                //let's add one more scope for us. See in postman also
                .scope("ADMIN")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

}

//To allow all unauthorized access when sign up, I implement the following code...
//So, sign up process I implement the following code

/*@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> {
                        try{
                            requests
                                .anyRequest().permitAll()
                                .and().cors().disable()
                                .csrf().disable();
                        }catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                );
        return http.build();
    }
}*/




//We have tried below code but above code only works line no: 174 to 191
/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/signup").permitAll()  // Allow unauthenticated POST to /signup
                .anyRequest().authenticated()  // Secure other endpoints which require authentication
            )
            .httpBasic(Customizer.withDefaults());  // Enable HTTP Basic auth or other mechanisms
        return http.build();
    }*/

/*    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/signup").permitAll()  // Allow unauthenticated POST to /signup
                .anyRequest().authenticated()  // Secure other endpoints which require authentication
            )
             .csrf(csrf -> csrf.disable());  // Disable CSRF for stateless APIs
        return http.build();
    }*/

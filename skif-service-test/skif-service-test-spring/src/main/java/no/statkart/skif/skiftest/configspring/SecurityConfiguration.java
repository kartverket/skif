package no.statkart.skif.skiftest.configspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final SecurityBasicAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfiguration(SecurityBasicAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Note, we do not overwrite the {@link #configure(AuthenticationManagerBuilder)} method, as we want to use the
     * global AuthenticationManagerBuilder and not the local one define for the adapter. Other configurations will also
     * add to the global AuthenticationManagerBuilder making it possible to combine multiple authentication schemes.
     * See https://spring.io/guides/topicals/spring-security-architecture
     */
    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("testUser").password(passwordEncoder().encode("test66User"))
                .authorities("ROLE_Innsyn");
    }

    /**
     * The authentication manager is not exposed as a bean automatically. This is the recommended way to expose it as
     * a bean.
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/favicon.ico").permitAll()
                // Web Service oversiktsside skal ikke autentiseres
                .antMatchers("/skiftest/wsapi/").permitAll()
                // Nedlasting av wsdl skal ikke autentiseres
                .regexMatchers("/skiftest/wsapi/\\w+\\?wsdl").permitAll()
                // Nedlasting av xsd-er skal ikke autentiseres (ikke sikkert vi faktisk har slike)
                .regexMatchers("/skiftest/wsapi/\\w+\\?xsd=*.\\.xsd").permitAll()
                // Alle andre requester skal autentiseres
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
        //return new BCryptPasswordEncoder(); // TODO: Må vel bruke samme som det Weblogic/Matrikkelen bruker. Skal ikke bruke BCrypt da den er for treg.
    }
}

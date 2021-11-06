package no.statkart.skif.storetest.configspring;

import com.zaxxer.hikari.HikariDataSource;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.SkifConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    DataSourceProperties dataSourceProperties(SkifConfiguration configuration) {
        DataSourceProperties properties= new DataSourceProperties();
        String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
        String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
        String service = configuration.getString(SkifConfigConstants.DB_SERVICE);
        String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
        String port = configuration.getString(SkifConfigConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@//%s:%s/%s", hostname, port, service);

        properties.setDriverClassName("oracle.jdbc.OracleDriver");
        properties.setUrl(url);
        properties.setUsername(username);
        properties.setPassword(password);
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Qualifier("Old")
    public DataSource dataSourceOld(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

}

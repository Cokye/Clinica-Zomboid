package cl.clinica.backend_clinica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.ArangoConfiguration;
import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.core.template.ArangoTemplate;

@Configuration
@EnableArangoRepositories(basePackages = "cl.clinica.backend_clinica.repository")
public class ArangoConfig implements ArangoConfiguration {

    @Value("${arangodb.spring.data.hosts:localhost:8529}")
    private String host;

    @Value("${arangodb.spring.data.user:root}")
    private String user;

    @Value("${arangodb.spring.data.password:root}")
    private String password;

    @Value("${arangodb.spring.data.database:_system}")
    private String database;

    @Override
    public ArangoDB.Builder arango() {
        String[] parts = host.split(":");
        String hostname = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 8529;

        return new ArangoDB.Builder()
                .host(hostname, port)
                .user(user)
                .password(password);
    }

    @Override
    public String database() {
        return database;
    }

    @Bean(name = "arangoTemplate")
    public ArangoTemplate arangoTemplateBean() throws Exception {
        return (ArangoTemplate) ArangoConfiguration.super.arangoTemplate();
    }

    @Bean
    public ArangoOperations arangoOperations(ArangoTemplate arangoTemplate) {
        return arangoTemplate;
    }
}
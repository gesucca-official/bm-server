package com.gsc.bm.conf.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "externalDbEntityManagerFactory",
        transactionManagerRef = "externalDbTransactionManager",
        basePackages = {"com.gsc.bm.repo.external"})
public class ExternalDbConfig {

    private final Environment env;

    @Autowired
    public ExternalDbConfig(Environment env) {
        this.env = env;
    }

    @Bean(name = "dataSourceExternalDB")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                Objects.requireNonNull(env.getProperty("spring.datasource.external.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.datasource.external.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.external.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.external.password"));
        return dataSource;
    }

    @Bean(name = "externalDbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean externalDbEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSourceExternalDB") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.gsc.bm.repo.external")
                .persistenceUnit("EXTERNAL_DB")
                .build();
    }

    @Bean(name = "externalDbTransactionManager")
    public PlatformTransactionManager externalDbTransactionManager(
            @Qualifier("externalDbEntityManagerFactory") EntityManagerFactory externalDbEntityManagerFactory) {
        return new JpaTransactionManager(externalDbEntityManagerFactory);
    }
}

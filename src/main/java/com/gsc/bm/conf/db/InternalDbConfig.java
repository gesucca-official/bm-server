package com.gsc.bm.conf.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "internalDbEntityManagerFactory",
        transactionManagerRef = "internalDbTransactionManager",
        basePackages = {"com.gsc.bm.repo.internal"})
public class InternalDbConfig {

    private final Environment env;

    @Autowired
    public InternalDbConfig(Environment env) {
        this.env = env;
    }

    @Primary
    @Bean(name = "dataSourceInternalDB")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                Objects.requireNonNull(env.getProperty("spring.datasource.internal.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.datasource.internal.url"));
        return dataSource;
    }

    @Primary
    @Bean(name = "internalDbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean internalDbEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSourceInternalDB") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.gsc.bm.repo.internal")
                .persistenceUnit("INTERNAL_DB")
                .build();
    }

    @Primary
    @Bean(name = "internalDbTransactionManager")
    public PlatformTransactionManager internalDbTransactionManager(
            @Qualifier("internalDbEntityManagerFactory") EntityManagerFactory internalDbEntityManagerFactory) {
        return new JpaTransactionManager(internalDbEntityManagerFactory);
    }
}

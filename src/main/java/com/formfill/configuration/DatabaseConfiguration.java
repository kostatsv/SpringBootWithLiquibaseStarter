package com.formfill.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ComponentScan(basePackages = { "com.formfill.domain" })
public class DatabaseConfiguration implements TransactionManagementConfigurer {

  @Resource
  private Environment environment;

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

  public static final String BACKEND_DATA_SOURCE = "BACKEND_DATA_SOURCE";

  public static final String BACKEND_SESSION_FACTORY = "BACKEND_SESSION_FACTORY";

  public static final String BACKEND_TRANSACTION_MANAGER = "BACKEND_TRANSACTION_MANAGER";

  public static final String BACKEND_TRANSACTION_TEMPLATE = "BACKEND_TRANSACTION_TEMPLATE";

  @Primary
  @Bean(name = BACKEND_DATA_SOURCE)
  public DataSource dataSource() {
    LOGGER.info("Attempting to create the Data Source");

    HikariDataSource dataSource = new HikariDataSource();

    dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
    dataSource.setJdbcUrl(environment.getRequiredProperty("spring.datasource.url"));
    dataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
    dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
    dataSource.setMaximumPoolSize(Integer.parseInt(environment.getRequiredProperty(
      "spring.datasource.hikari.maximum-pool-size")));
    dataSource.setConnectionTimeout(Long.parseLong(environment.getRequiredProperty(
      "spring.datasource.hikari.connectionTimeout")));

    LOGGER.info("Created the Data Source");

    return dataSource;
  }

  private Properties hibernateProperties() {
    LOGGER.info("Attempting to create the Hibernate Properties Map");

    Properties hibernateProperties = new Properties();

    hibernateProperties.setProperty("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));

    // We always want to use the hibernate 5 spring session context - should never change doesn't need to be a property
    hibernateProperties.setProperty("hibernate.current_session_context_class",
                                    SpringSessionContext.class.getCanonicalName());

    // This should never actually be changed, but we still want it to be a property, so we can configure it to
    // auto-generate the tables for tests to run against. This property should only ever be configured within the
    // application.properties that exists within src/main/resources and is bundled as part of the jar, never externally.
    hibernateProperties.setProperty("hibernate.hbm2ddl.auto",
                                    environment.getRequiredProperty("hibernate.hbm2ddl.auto"));

    // Use the old generator mappings this affects SQL Server 2012+, forcing the database to use IDENTITY for PKs
    // instead of using the newer sequences, consider changing this to true when we can guarantee that we only need to
    // support SQLServer 2012+ (as this is the first time sequences were available in SQLServer)
    // Changing this means BIG database changes for SQLServer - No benefit in changing?
    hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "false");

    if (environment
      .getRequiredProperty("spring.datasource.driver-class-name")
      .equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
      // only valid for sqlserver
      // Use nationalized (nvarchar) over varchar for sting columns, see:
      // http://confluence:8090/display/DEVOPS/varchar+versus+nvarchar
      // hibernateProperties.setProperty("hibernate.use_nationalized_character_data",
      // "true");
      hibernateProperties.setProperty("hibernate.use_nationalized_character_data", "true");
      //hibernateProperties.setProperty("hibernate.dialect", "com.formfill.configuration.SqlServerCustomDialect");
    }
    LOGGER.info("Created the Hibernate Properties Map");

    return hibernateProperties;
  }

  @Primary
  @DependsOn("liquibase")
  @Bean(name = BACKEND_SESSION_FACTORY)
  public SessionFactory sessionFactory() {
    LOGGER.info("Attempting to create the Hibernate Session Factory");

    try {
      LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();

      localSessionFactoryBean.setDataSource(dataSource());
      localSessionFactoryBean.setHibernateProperties(hibernateProperties());
      localSessionFactoryBean.setPackagesToScan("com.formfill.domain");
      localSessionFactoryBean.setAnnotatedPackages("com.formfill.domain");
      localSessionFactoryBean.afterPropertiesSet();

      SessionFactory sessionFactory = localSessionFactoryBean.getObject();

      LOGGER.info("Created the Hibernate Session Factory");

      return sessionFactory;
    }
    catch (IOException ex) {
      LOGGER.error("Unable to create the Hibernate Session Factory: ", ex);
      throw new RuntimeException("Unable to create Hibernate Session Factory", ex);
    }
  }

  @Primary
  @DependsOn(BACKEND_SESSION_FACTORY)
  @Bean(name = BACKEND_TRANSACTION_MANAGER)
  public HibernateTransactionManager transactionManager() {
    LOGGER.info("Attempting to create the Transaction Manager");

    HibernateTransactionManager txManager = new HibernateTransactionManager();
    txManager.setSessionFactory(sessionFactory());
    txManager.setRollbackOnCommitFailure(true);

    LOGGER.info("Created the Transaction Manager");

    return txManager;
  }

  @Primary
  @DependsOn(BACKEND_SESSION_FACTORY)
  @Bean(name = BACKEND_TRANSACTION_TEMPLATE)
  public TransactionTemplate transactionTemplate() {
    LOGGER.info("Attempting to create the Transaction Template");

    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    LOGGER.info("Created the Transaction Template");

    return transactionTemplate;
  }

  @Bean
  @DependsOn("dataSource")
  public SpringLiquibase liquibase() {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
    liquibase.setDataSource(dataSource());
    liquibase.setChangeLogParameters(buildChangeLogParameters());
    return liquibase;
  }

  @Override
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    return transactionManager();
  }

  private Map<String, String> buildChangeLogParameters() {
    LOGGER.debug("Beginning generation of properties for liquibase scripts");
    Map<String, String> changeLogParameters = new HashMap<>();

    String fullJdbcUrl = environment.getRequiredProperty("spring.datasource.url");

    LOGGER.debug("Generating database parameter based on jdbc connection url ({})", fullJdbcUrl);

    LOGGER.debug("Looking for databaseName= within the JDBC URL");

    if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(environment.getRequiredProperty(
      "spring.datasource.driver-class-name"))) {
      //database name only required for sql server

      Pattern databaseNameParameterPattern = Pattern.compile("databaseName=", Pattern.CASE_INSENSITIVE);
      Matcher databaseNameParameterMatcher = databaseNameParameterPattern.matcher(fullJdbcUrl);

      int endIndex = -1;

      if (databaseNameParameterMatcher.find()) {
        endIndex = databaseNameParameterMatcher.end();
        LOGGER.trace("String databaseName= found within the JDBC URL starting " +
                     "at index ({}) and ending at index ({})",
                     databaseNameParameterMatcher.start(), endIndex);
      }

      if (endIndex == -1) {
        String message = String.format("Could not find location of databaseName= parameter within the " +
                                       "Settlements JDBC URL (%s), cannot determine database name required for " +
                                       "running liquibase validation and configuration",
                                       fullJdbcUrl);
        LOGGER.error(message);
        throw new RuntimeException(message);
      }

      String databaseNameOnwards = fullJdbcUrl.substring(endIndex);

      LOGGER.trace("After stripping everything before the databaseName= parameter ({})",
                   databaseNameOnwards);

      // ; should first appear after the actual defined database name in the url this gives us our end point
      int firstIndexOfSemiColon = databaseNameOnwards.indexOf(';');
      LOGGER.trace("First instance of ; in remaining url ({}) found to be ({})",
                   databaseNameOnwards,
                   firstIndexOfSemiColon);

      String databaseName = databaseNameOnwards;

      if (firstIndexOfSemiColon != -1) {
        LOGGER.trace("Found ; after databaseName remove ; and everything following it to get the database name");
        databaseName = databaseNameOnwards.substring(0, firstIndexOfSemiColon);
      }
      else {
        LOGGER
          .trace("No ; found after databaseName, assuming this is the end of the databaseName parameter within the url");
      }

      LOGGER.debug("Stripped database name out of datasource connection url, " +
                   "will be used in liquibase scripts ({})",
                   databaseName);

      changeLogParameters.put("database-name", databaseName);
    }

    changeLogParameters.put("enable-configure-transaction-isolation-level", "true");

    return changeLogParameters;
  }
}

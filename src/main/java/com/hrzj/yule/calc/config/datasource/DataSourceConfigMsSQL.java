package com.hrzj.yule.calc.config.datasource;


import com.hrzj.yule.calc.config.exception.DatabaseConnectionException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/15 星期一 17:14
 */
@Configuration
@Slf4j
@MapperScan(basePackages = "com.hrzj.yule.calc.mapper.mssql", sqlSessionFactoryRef = "yuleSqlSessionFactory")
@EnableJpaRepositories(basePackages = "com.hrzj.yule.calc.repository.mssql",
  transactionManagerRef = "mssqlJpaTransactionManager", entityManagerFactoryRef = "mssqlEntityManagerFactory")
@EnableTransactionManagement
public class DataSourceConfigMsSQL {

  static final String MAPPER_LOCATION = "classpath:mapper/mssql/*.xml";

  @Value("${mssql.spring.datasource.url}")
  private String url;

  @Value("${mssql.spring.datasource.username}")
  private String username;

  @Value("${mssql.spring.datasource.password}")
  private String password;

  @Value("${mssql.spring.datasource.driver-class-name}")
  private String driverClassName;

  @Autowired
  private HibernateProperties hibernateProperties;

  @Autowired
  private JpaProperties jpaProperties;

  @Primary
  @Bean(name = "yuleDataSource")
  public DataSource yuleDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setDriverClassName(driverClassName);
    return dataSource;
  }

  @Primary
  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Primary
  @Qualifier("yuleTransactionManager")
  @Bean(name = "yuleTransactionManager")
  public DataSourceTransactionManager yuleTransactionManager() {
    return new DataSourceTransactionManager(yuleDataSource());
  }

  @Primary
  @Bean(name = "yuleSqlSessionFactory")
  public SqlSessionFactory yuleSqlSessionFactory(@Qualifier("yuleDataSource") DataSource yuleDatasource)
    throws Exception {
    final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(yuleDatasource);
    sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
      .getResources(MAPPER_LOCATION));
    log.info("初始化mssql数据库连接完成");
    return sessionFactory.getObject();
  }

  @Primary
  @Qualifier("mssqlEntityManagerFactory")
  @Bean("mssqlEntityManagerFactory")
  public EntityManagerFactory mssqlEntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(yuleDataSource());
    factory.setPackagesToScan("com.hrzj.yule.calc.pojo.entity.mssql");
    factory.setJpaVendorAdapter(getVendorAdapter());
    factory.setPersistenceUnitName("mssql");
    factory.setJpaPropertyMap(getVendorProperties());
    try {
      factory.afterPropertiesSet();
    } catch (Exception e) {
      log.error("数据库连接失败");
      throw new DatabaseConnectionException("mssql数据库服务未启动,应用无法继续启动。请检查数据库服务再重启。");
    }
    return factory.getObject();
  }

  private Map<String, Object> getVendorProperties() {
    Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(),
      new HibernateSettings());
    return properties;
  }

  private HibernateJpaVendorAdapter getVendorAdapter() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(Boolean.TRUE);
    vendorAdapter.setGenerateDdl(Boolean.TRUE);
    return vendorAdapter;
  }


  @Primary
  @Bean("mssqlJpaTransactionManager")
  public JpaTransactionManager mssqlJpaTransactionManager() {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(mssqlEntityManagerFactory());
    txManager.afterPropertiesSet();
    return txManager;
  }
}

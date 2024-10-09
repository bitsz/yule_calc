package com.hrzj.yule.calc.config.datasource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.hrzj.yule.calc.config.exception.DatabaseConnectionException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
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
@MapperScan(basePackages = "com.hrzj.yule.calc.mapper.mysql", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
@EnableJpaRepositories(basePackages = "com.hrzj.yule.calc.repository.mysql",
  transactionManagerRef = "mysqlJpaTransactionManager", entityManagerFactoryRef = "mysqlEntityManagerFactory")
@EnableTransactionManagement
@ConfigurationProperties(prefix = "mysql.spring.datasource")
public class DataSourceConfigMySQL {

  static final String MAPPER_LOCATION = "classpath:mapper/mysql/*.xml";

  @Value("${mysql.spring.datasource.url}")
  private String url;

  @Value("${mysql.spring.datasource.username}")
  private String username;

  @Value("${mysql.spring.datasource.password}")
  private String password;

  @Value("${mysql.spring.datasource.driver-class-name}")
  private String driverClassName;

  @Autowired
  private HibernateProperties hibernateProperties;

  @Autowired
  private JpaProperties jpaProperties;


  @Bean(name = "mysqlDataSource")
  @Qualifier("mysqlDataSource")
  public DataSource mysqlDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setDriverClassName(driverClassName);
    return dataSource;
  }

  @Bean(name = "mysqlTransactionManager")
  public PlatformTransactionManager mysqlTransactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

//  @Bean(name = "mysqlTransactionManager")
//  public DataSourceTransactionManager mysqlTransactionManager() {
//    return new DataSourceTransactionManager(mysqlDataSource());
//  }

  @Bean(name = "mysqlSqlSessionFactory")
  public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource mysqlDatasource)
    throws Exception {
    final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
    sessionFactory.setDataSource(mysqlDatasource);
    sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
      .getResources(MAPPER_LOCATION));
    sessionFactory.setPlugins(mybatisPlusInterceptor());
    log.info("初始化mysql数据库连接完成");
    return sessionFactory.getObject();
  }


  @Bean
  public SqlSessionTemplate sqlSessionTemplate(@Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean(name = "mysqlEntityManagerFactory")
  public EntityManagerFactory mysqlEntityManagerFactory(@Qualifier("mysqlDataSource") DataSource mysqlDatasource) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(mysqlDatasource);
    factory.setPackagesToScan("com.hrzj.yule.calc.pojo.entity.mysql");
    factory.setJpaVendorAdapter(getVendorAdapter());
    factory.setPersistenceUnitName("mysql");
    factory.setJpaPropertyMap(getVendorProperties());
    try {
      factory.afterPropertiesSet();
    } catch (Exception e) {
      log.error("数据库连接失败");
      throw new DatabaseConnectionException("mySql数据库服务未启动,应用无法继续启动。请检查数据库服务再重启。");
    }
    return factory.getObject();
  }


  private Map<String, Object> getVendorProperties() {
    Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(),
      new HibernateSettings());
    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect"); // 设置MySQL方言
    return properties;
  }


  private HibernateJpaVendorAdapter getVendorAdapter() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(Boolean.TRUE);
    vendorAdapter.setDatabase(Database.MYSQL);
    return vendorAdapter;
  }


  @Bean(name = "mysqlJpaTransactionManager")
  public JpaTransactionManager mysqlJpaTransactionManager(@Qualifier("mysqlDataSource") DataSource mysqlDatasource) {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(mysqlEntityManagerFactory(mysqlDatasource));
    txManager.afterPropertiesSet();
    return txManager;
  }

  //配置分页插件
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 分页
    PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
    // 方言
    paginationInnerInterceptor.setDbType(DbType.MYSQL);
    paginationInnerInterceptor.setMaxLimit(100L);
    interceptor.addInnerInterceptor(paginationInnerInterceptor);
    // 乐观锁 更新时需实体携带 version 版本号参数
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    // SQL 阻断
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return interceptor;
  }
}

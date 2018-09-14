package com.huayi.huayiweb.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceHelper {

  public static DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://192.168.1.117:3306/huayi");
    config.setUsername("root");
    config.setPassword("qbz@123");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setDriverClassName("com.mysql.jdbc.Driver");

    HikariDataSource ds = new HikariDataSource(config);
    return ds;
  }
}

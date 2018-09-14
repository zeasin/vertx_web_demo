package com.huayi.huayiweb.service;

import io.vertx.ext.sql.SQLConnection;

public abstract class AbstractService {

  protected SQLConnection sqlConnection;

  public AbstractService(SQLConnection connection) {
    this.sqlConnection = connection;
  }
}

package com.huayi.huayiweb.handler;

import io.vertx.core.Context;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

public abstract class AbstractHandler {
  protected JDBCClient jdbcClient;
  protected ThymeleafTemplateEngine templateEngine;

  public AbstractHandler(Context context) {
    this.jdbcClient = context.get("JDBCClient");
    this.templateEngine = context.get("TemplateEngine");
  }
}

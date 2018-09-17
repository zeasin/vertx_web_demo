package com.huayi.huayiweb.handler;

import io.vertx.core.Context;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

public abstract class AbstractHandler {
  protected JDBCClient jdbcClient;
  protected ThymeleafTemplateEngine templateEngine;

  public AbstractHandler(Context context) {
    this.jdbcClient = context.get("JDBCClient");
    this.templateEngine = context.get("TemplateEngine");
  }

  protected void render(RoutingContext routingContext, ThymeleafTemplateEngine engine, String templ) {
    engine.render(routingContext, "", templ, res -> {
      if (res.succeeded()) {
        routingContext.response().end(res.result());
      } else {
        routingContext.fail(res.cause());
      }
    });
  }

  protected void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }
}

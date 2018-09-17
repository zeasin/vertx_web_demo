package com.huayi.huayiweb.handler.admin;

import com.huayi.huayiweb.handler.AbstractHandler;
import com.huayi.huayiweb.model.ManageSessionModel;
import io.vertx.core.Context;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import org.thymeleaf.util.StringUtils;

import java.util.List;

public class LoginHandler extends AbstractHandler {

//  private JDBCClient client;
//  private ThymeleafTemplateEngine engine;

  public LoginHandler(Context context) {
    super(context);
  }

  public void handlePostLogin(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    HttpServerRequest request = routingContext.request();
    String userName = request.getParam("username");
    String password = request.getParam("password");
    System.out.println("username:" + userName);
    System.out.println("password:" + password);

    if(StringUtils.isEmpty(userName)){
      //跳转页面
      routingContext.put("error", "请输入用户名和密码");
      routingContext.reroute(HttpMethod.GET, "/admin/login");
    }

    //查询数据库
    jdbcClient.getConnection(res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }

      SQLConnection conn = res.result();
      String sql = "SELECT * FROM manage_user WHERE user_name = ?";

      JsonArray jsonArray = new JsonArray();
      jsonArray.add(userName);


      conn.queryWithParams(sql, jsonArray, query -> {
        if (query.failed()) {
          sendError(500, response);
        } else {
          List<JsonObject> list = query.result().getRows();
          if (list == null || list.size() == 0) {
            //跳转页面
            routingContext.put("error", "用户名不存在");
            routingContext.put("username", userName);
            routingContext.reroute(HttpMethod.GET, "/admin/login");
          } else {

            Session session = routingContext.session();

            ManageSessionModel sessionModel = new ManageSessionModel();
            sessionModel.setId(list.get(0).getInteger("id"));
            sessionModel.setUserName(list.get(0).getString("user_name"));
            sessionModel.setTrueName(list.get(0).getString("true_name"));
            sessionModel.setMobile(list.get(0).getString("mobile"));

            session.put("manage", sessionModel);

            //跳转页面
            response.setStatusCode(302);
            response.headers().add("location", "/admin/index");
            response.end();
          }
        }
      });
      conn.close();

    });


//    Session session = routingContext.session();
//
////    client = routingContext.get("jdbcclient");
////    engine = routingContext.get("engine");
//
//    ManageSessionModel sessionModel = new ManageSessionModel();
//    sessionModel.setId(1);
//    sessionModel.setUserName("admin");
//    sessionModel.setTrueName("andy");
//    sessionModel.setMobile("15818590119");
//
//    session.put("manage", sessionModel);
//
//    //跳转页面
//    response.setStatusCode(302);
//    response.headers().add("location", "/admin/index");
//    response.end();
  }

  public void handleGetLogin(RoutingContext routingContext) {
//    routingContext.put("error", "错误提示");
    render(routingContext, templateEngine, "/admin/login");
  }


}

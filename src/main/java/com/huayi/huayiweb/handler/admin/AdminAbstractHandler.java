package com.huayi.huayiweb.handler.admin;

import com.huayi.huayiweb.handler.AbstractHandler;
import com.huayi.huayiweb.model.AdminMenu;
import com.huayi.huayiweb.model.AdminMenuList;
import com.huayi.huayiweb.model.ManageSessionModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminAbstractHandler extends AbstractHandler {
  protected Integer userId;

  public AdminAbstractHandler(Context context) {
    super(context);
  }

  protected void init(RoutingContext routingContext,Handler<AsyncResult<ResultSet>> resultHandler) {
    setUserInfo(routingContext);
    getMenu(routingContext,resultHandler);
  }

  /**
   * 设置登录用户信息
   *
   * @param routingContext
   */
  private void setUserInfo(RoutingContext routingContext) {
    //获取session
    Session session = routingContext.session();
    ManageSessionModel sessionModel = session.get("manage");
    if (sessionModel != null) {
      routingContext.put("username", sessionModel.getTrueName());
      userId = sessionModel.getId();
    }
  }

  /**
   * 获取菜单信息
   *
   * @param routingContext
   */
  private void getMenu(RoutingContext routingContext,Handler<AsyncResult<ResultSet>> resultHandler) {
    //查询数据库
    jdbcClient.getConnection(res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }
      SQLConnection conn = res.result();
      //取出所有菜单
      String sql = "SELECT p.* FROM manage_user_permission up left join manage_permission p on p.id = up.`permission_id` where p.is_menu='Y' and up.user_id= ?";

      JsonArray jsonArray = new JsonArray();
      jsonArray.add(userId);

      conn.queryWithParams(sql,jsonArray,resultHandler);
//      conn.queryWithParams(sql, jsonArray, query -> {
//        if (query.failed()) {
//
//        } else {
//          List<JsonObject> list = query.result().getRows();
//          List<JsonObject> topMenuList = list.stream().filter(s -> s.getInteger("parent_id") == 0).collect(Collectors.toList());
//          List<AdminMenuList> menuLists = new ArrayList<>();
//
//          //重新组合菜单
//          for (JsonObject js : topMenuList) {
//            AdminMenuList menu = new AdminMenuList();
//            menu.setName(js.getString("name"));
//            menu.setUrl(js.getString("url"));
//
//            //组合子菜单
//            List<JsonObject> childList = list.stream().filter(s -> s.getInteger("parent_id") == js.getInteger("id")).collect(Collectors.toList());
//            List<AdminMenu> childMenu = new ArrayList<>();
//            for (JsonObject child : childList) {
//              AdminMenu adminMenu = new AdminMenu();
//              adminMenu.setName(child.getString("name"));
//              adminMenu.setUrl(child.getString("url"));
//              childMenu.add(adminMenu);
//            }
//
//            menu.setMenus(childMenu);
//            menuLists.add(menu);
//
//          }
//          if (menuLists != null) {
//            routingContext.put("menuList", menuLists);
//          }
//          render(routingContext, templateEngine, "/admin/index");
//          routingContext.response().end();
//        }
//      });

    });
  }

}

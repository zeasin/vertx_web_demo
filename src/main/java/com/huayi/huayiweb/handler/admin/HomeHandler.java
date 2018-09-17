package com.huayi.huayiweb.handler.admin;

import com.huayi.huayiweb.model.AdminMenu;
import com.huayi.huayiweb.model.AdminMenuList;
import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeHandler extends AdminAbstractHandler {
  public HomeHandler(Context context) {
    super(context);
  }


  /**
   * 管理后台首页页面
   *
   * @param routingContext
   */
  public void handleGetHome(RoutingContext routingContext) {
//    init(routingContext);

    init(routingContext, query -> {
      if (query.failed()) {

      } else {
        {
          List<JsonObject> list = query.result().getRows();
          List<JsonObject> topMenuList = list.stream().filter(s -> s.getInteger("parent_id") == 0).collect(Collectors.toList());
          List<AdminMenuList> menuLists = new ArrayList<>();

          //重新组合菜单
          for (JsonObject js : topMenuList) {
            AdminMenuList menu = new AdminMenuList();
            menu.setName(js.getString("name"));
            menu.setUrl(js.getString("url"));

            //组合子菜单
            List<JsonObject> childList = list.stream().filter(s -> s.getInteger("parent_id") == js.getInteger("id")).collect(Collectors.toList());
            List<AdminMenu> childMenu = new ArrayList<>();
            for (JsonObject child : childList) {
              AdminMenu adminMenu = new AdminMenu();
              adminMenu.setName(child.getString("name"));
              adminMenu.setUrl(child.getString("url"));
              childMenu.add(adminMenu);
            }

            menu.setMenus(childMenu);
            menuLists.add(menu);

          }
          if (menuLists != null) {
            routingContext.put("menuList", menuLists);
          }
          render(routingContext, templateEngine, "/admin/index");
        }
      }
    });


//    List<AdminMenuList> menuLists = new ArrayList<>();
//    AdminMenuList aml = new AdminMenuList();
//    aml.setName("网站设置");
//    aml.setUrl("#");
//    menuLists.add(aml);
//
//    routingContext.put("menuList", menuLists);


  }
}

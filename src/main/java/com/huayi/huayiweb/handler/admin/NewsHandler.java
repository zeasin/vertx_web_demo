package com.huayi.huayiweb.handler.admin;

import com.huayi.huayiweb.handler.AbstractHandler;
import io.vertx.core.Context;
import io.vertx.ext.web.RoutingContext;

public class NewsHandler extends AdminAbstractHandler {
  public NewsHandler(Context context) {
    super(context);
  }

  /**
   * 新闻分类页面
   * @param routingContext
   */
  public void handleGetNewsCategoryList(RoutingContext routingContext) {
//    routingContext.put("error", "错误提示");
    render(routingContext, templateEngine, "/admin/news_category_list");
  }

  /**
   * 新闻页面
   * @param routingContext
   */
  public void handleGetNewsList(RoutingContext routingContext) {
    routingContext.put("menu", "ABCD");
    routingContext.put("username","闲心");
    render(routingContext, templateEngine, "/admin/news_list");
  }
}

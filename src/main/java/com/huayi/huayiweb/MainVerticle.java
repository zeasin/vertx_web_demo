package com.huayi.huayiweb;

import com.huayi.huayiweb.handler.admin.HomeHandler;
import com.huayi.huayiweb.handler.admin.LoginHandler;
import com.huayi.huayiweb.handler.admin.NewsHandler;
import com.huayi.huayiweb.model.ManageSessionModel;
import com.huayi.huayiweb.utils.DataSourceHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    System.out.println("开始运行");
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    logger.info("开始运行Vertx");
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new MainVerticle());

  }

  //JDBCClient
  private JDBCClient client;
  //Template Engine
  private ThymeleafTemplateEngine engine;

  //登录Handler
  private LoginHandler loginHandler;
  //管理后台首页
  private HomeHandler homeHandler;
  //新闻管理Handler
  private NewsHandler newsHandler;


  @Override
  public void init(Vertx vertx, Context context) {
    //加载JDBCClient
    client = JDBCClient.create(vertx, DataSourceHelper.dataSource());
    context.put("JDBCClient", client);

    //加载模版引擎
    engine = ThymeleafTemplateEngine.create();
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML");
    engine.getThymeleafTemplateEngine().setTemplateResolver(resolver);
    context.put("TemplateEngine", engine);

    //加载Handler
    loginHandler = new LoginHandler(context);
    newsHandler = new NewsHandler(context);
    homeHandler = new HomeHandler(context);

    this.vertx = vertx;
    this.context = context;
  }


  @Override
  public void start() {
    MainVerticle that = this;
    //登录Handler
//    LoginHandler loginHandler = new LoginHandler();


    Router router = Router.router(vertx);
    // 配置静态文件
    router.route("/static/*").handler(StaticHandler.create("static"));

    router.route().handler(routingContext -> {
      routingContext.put("jdbcclient", client);
      routingContext.put("engine", engine);
      routingContext.next();
    });

    //为所有route创建session handler
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));


    router.get("/index.html").handler(routingContext -> {
      logger.info("访问index.html");
      routingContext.put("welcome", "hello world,page好人医生");
      engine.render(routingContext, "", "/index", res -> {
        if (res.succeeded()) {
          routingContext.response().end(res.result());
        } else {
          routingContext.fail(res.cause());
        }
      });
    });


    router.route().handler(BodyHandler.create());
    router.get("/products/:productID").handler(that::handleGetProduct);
    router.post("/products").handler(that::handleAddProduct);
    router.get("/products").handler(that::handleListProducts);


    //后台页面路由
    router.get("/admin/login").handler(loginHandler::handleGetLogin);
    //登录POST
    router.post("/admin/login").handler(loginHandler::handlePostLogin);

    //后台页面拦截器
    router.route("/admin/*").handler(ctx -> {
      System.out.println("访问后台");
      //获取session
      Session session = ctx.session();
      ManageSessionModel sessionModel = session.get("manage");
      if (sessionModel == null) {
        //没有session跳转到登录页面
        HttpServerResponse response = ctx.response();
        response.setStatusCode(302);
        response.headers().add("location", "/admin/login");
        response.end();
      }
      //session存在，继续
      ctx.next();
    });

    //后台页面
    router.get("/admin/index").handler(homeHandler::handleGetHome);
    router.get("/admin/news/category_list").handler(newsHandler::handleGetNewsCategoryList);
    router.get("/admin/news/list").handler(newsHandler::handleGetNewsList);


    // vert.x使用log4j

//    System.setProperty("vertx.logger-delegate-factory-class-name","io.vertx.core.logging.SLF4JLogDelegateFactory");
//    System.setProperty(SLF4JLogDelegateFactory, "io.vertx.core.logging.SLF4JLogDelegateFactory");
    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    System.out.println("服务器启动成功");

  }

//  private void render(RoutingContext routingContext, ThymeleafTemplateEngine engine, String templ) {
//    engine.render(routingContext, "", templ, res -> {
//      if (res.succeeded()) {
//        routingContext.response().end(res.result());
//      } else {
//        routingContext.fail(res.cause());
//      }
//    });
//  }

  private void handleGetProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("productID");
    HttpServerResponse response = routingContext.response();
    if (productID == null) {
      sendError(400, response);
    } else {
      SQLConnection conn = routingContext.get("conn");

      conn.queryWithParams("SELECT id, name, price, weight FROM products where id = ?", new JsonArray().add(Integer.parseInt(productID)), query -> {
        if (query.failed()) {
          sendError(500, response);
        } else {
          if (query.result().getNumRows() == 0) {
            sendError(404, response);
          } else {
            response.putHeader("content-type", "application/json").end(query.result().getRows().get(0).encode());
          }
        }
      });
    }
  }

  private void handleAddProduct(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();

    SQLConnection conn = routingContext.get("conn");
    JsonObject product = routingContext.getBodyAsJson();

    conn.updateWithParams("INSERT INTO products (name, price, weight) VALUES (?, ?, ?)",
      new JsonArray().add(product.getString("name")).add(product.getFloat("price")).add(product.getInteger("weight")), query -> {
        if (query.failed()) {
          sendError(500, response);
        } else {
          response.end();
        }
      });
  }

  private void handleListProducts(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();


    client.getConnection(res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }
      SQLConnection conn = res.result();
      conn.query("SELECT id, name, price, weight FROM products", query -> {
        if (query.failed()) {
          sendError(500, response);
        } else {
          JsonArray arr = new JsonArray();
          query.result().getRows().forEach(arr::add);
          routingContext.response().putHeader("content-type", "application/json").end(arr.encode());
        }
      });
      conn.close();
    });


//    SQLConnection conn = routingContext.get("conn");


  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }

}

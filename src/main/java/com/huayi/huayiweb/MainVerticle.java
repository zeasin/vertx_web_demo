package com.huayi.huayiweb;

import com.huayi.huayiweb.handler.admin.LoginHandler;
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

  private JDBCClient client;
  //登录Handler
  private LoginHandler loginHandler;
  private ThymeleafTemplateEngine engine;

  @Override
  public void init(Vertx vertx, Context context) {
    //加载JDBCClient
    client = JDBCClient.create(vertx, DataSourceHelper.dataSource());
    context.put("JDBCClient", client);

    //加载模版引擎
    engine = ThymeleafTemplateEngine.create();
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML");
    engine.getThymeleafTemplateEngine().setTemplateResolver(resolver);
    context.put("TemplateEngine", engine);

    loginHandler = new LoginHandler(context);
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
//    router.get("/admin/login").handler(ctx -> {
//      logger.info("访问登录页面");
////      render(ctx, engine, "/admin/login");
//      engine.render(ctx, "", "/admin/login", res -> {
//        if (res.succeeded()) {
//          ctx.response().end(res.result());
//        } else {
//          ctx.fail(res.cause());
//        }
//      });
//    });

    //登录POST
    router.post("/admin/login").handler(loginHandler::handlePostLogin);
//    router.post("/admin/login").handler(routingContext->{
//      HttpServerResponse response = routingContext.response();
//      HttpServerRequest request = routingContext.request();
//      String userName = request.getParam("username");
//      String password = request.getParam("password");
//      System.out.println("username:" + userName);
//      System.out.println("password:" + password);
//
//      Session session = routingContext.session();
//      //登录session实体
//      ManageSessionModel sessionModel = new ManageSessionModel();
//      sessionModel.setId(1);
//      sessionModel.setUserName("admin");
//      sessionModel.setTrueName("andy");
//      sessionModel.setMobile("15818590119");
//      //设置session
//      session.put("manage", sessionModel);
//      //跳转页面
//      response.setStatusCode(302);
//      response.headers().add("location","/admin/index");
//      response.end();
//    });

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

    router.get("/admin/index").handler(routingContext -> {
      logger.info("访问后台首页页面");
//      render(ctx, engine, "/admin/index");
      engine.render(routingContext, "", "/admin/index", res -> {
        if (res.succeeded()) {
          routingContext.response().end(res.result());
        } else {
          routingContext.fail(res.cause());
        }
      });
    });


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

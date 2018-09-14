package com.huayi.huayiweb.service;

import com.huayi.huayiweb.entity.ManageUser;
import com.huayi.huayiweb.model.ServiceResultModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

public class ManageUserService extends AbstractService {

  public ManageUserService(SQLConnection connection) {
    super(connection);
  }


  public ServiceResultModel<ManageUser> getManage(String userName) {

    String sql = "SELECT * FROM manage_user WHERE user_name = '" + userName + "'";

    sqlConnection.query(sql, query -> {
      if (query.failed()) {
        sqlConnection.close();
//        return new ServiceResultModel(500);
      } else {
        JsonArray arr = new JsonArray();
        query.result().getRows().forEach(arr::add);
        new ServiceResultModel(0);
      }
    });
    sqlConnection.close();

    return new ServiceResultModel(500);
  }


}

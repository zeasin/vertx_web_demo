package com.huayi.huayiweb.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.shareddata.impl.ClusterSerializable;

public class ManageSessionModel implements ClusterSerializable {
  private Integer id;
  private String userName;
  private String trueName;
  private String mobile;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getTrueName() {
    return trueName;
  }

  public void setTrueName(String trueName) {
    this.trueName = trueName;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Override
  public void writeToBuffer(Buffer buffer) {

  }

  @Override
  public int readFromBuffer(int i, Buffer buffer) {
    return 0;
  }
}

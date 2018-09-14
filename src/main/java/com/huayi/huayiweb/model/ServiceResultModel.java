package com.huayi.huayiweb.model;

public class ServiceResultModel<T> {
  private Integer code;
  private String message;
  private T t;

  public ServiceResultModel() {

  }

  public ServiceResultModel(Integer code) {
    this.code = code;
  }

  public ServiceResultModel(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public ServiceResultModel(Integer code, String message, T t) {
    this.code = code;
    this.message = message;
    this.t = t;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getT() {
    return t;
  }

  public void setT(T t) {
    this.t = t;
  }
}

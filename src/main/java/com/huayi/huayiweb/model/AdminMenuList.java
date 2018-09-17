package com.huayi.huayiweb.model;

import java.util.List;

public class AdminMenuList {
  private String name;
  private String url;
  private List<AdminMenu> menus;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<AdminMenu> getMenus() {
    return menus;
  }

  public void setMenus(List<AdminMenu> menus) {
    this.menus = menus;
  }
}

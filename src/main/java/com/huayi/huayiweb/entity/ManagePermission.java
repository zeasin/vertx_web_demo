package com.huayi.huayiweb.entity;

public class ManagePermission {
  private Integer id;
  private String name;
  private String url;
  private String tag;
  private String isMenu;
  private Integer sort;
  private Integer parentId;
  private String status;
  private Long addTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

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

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getIsMenu() {
    return isMenu;
  }

  public void setIsMenu(String isMenu) {
    this.isMenu = isMenu;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getAddTime() {
    return addTime;
  }

  public void setAddTime(Long addTime) {
    this.addTime = addTime;
  }
}

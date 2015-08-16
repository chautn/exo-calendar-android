package org.exoplatform.calendar.client.model;

/**
 * Created by chautn on 8/17/15.
 */
public class ExoCalendar implements Identifiable {
  String editPermission;
  String viewPermission;
  String privateURL;
  String publicURL;
  String icsURL;
  String color;
  String name;
  String type;
  String owner;
  String timeZone;
  String description;
  String[] groups;
  String href;
  String id;

  public String getEditPermission() {
    return editPermission;
  }
  public void setEditPermission(String editPermission) {
    this.editPermission = editPermission;
  }
  public String getViewPermission() {
    return viewPermission;
  }
  public void setViewPermission(String viewPermission) {
    this.viewPermission = viewPermission;
  }
  public String getPrivateURL() {
    return privateURL;
  }
  public void setPrivateURL(String privateURL) {
    this.privateURL = privateURL;
  }
  public String getPublicURL() {
    return publicURL;
  }
  public void setPublicURL(String publicURL) {
    this.publicURL = publicURL;
  }
  public String getIcsURL() {
    return icsURL;
  }
  public void setIcsURL(String icsURL) {
    this.icsURL = icsURL;
  }
  public String getColor() {
    return color;
  }
  public void setColor(String color) {
    this.color = color;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getOwner() {
    return owner;
  }
  public void setOwner(String owner) {
    this.owner = owner;
  }
  public String getTimeZone() {
    return timeZone;
  }
  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String[] getGroups() {
    return groups;
  }
  public void setGroups(String[] groups) {
    this.groups = groups;
  }
  public String getHref() {
    return href;
  }
  public void setHref(String href) {
    this.href = href;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
}

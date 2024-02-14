package com.formfill.domain.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.formfill.domain.BaseDomain;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = Role.TABLE_NAME,
       indexes = {
         @Index(name = Role.TABLE_NAME + "_IDX_1",
                columnList = BaseDomain.PK + "," + BaseDomain.VERSION)
       })
public class Role extends BaseDomain implements GrantedAuthority {

  public static final String TABLE_NAME = "ROLE";

  private String name;

  private String description;

  // private List<Url> urls = new ArrayList<>();

  private List<User> users = new ArrayList<>();

  private Boolean deleted = false;

  private List<RoleUrl> roleUrls;
  
  private String defaultLandingPage;

  public Role() {

  }

  /*public Role(RoleController.RoleCommand roleCommand, List<Url> urlList) {
    this.setName(roleCommand.getRoleName());
    this.setDescription(roleCommand.getRoleDescription());
    // this.setUrls(urlList);

    Hibernate.initialize(getRoleUrls());
    List<RoleUrl> roleUrls = urlList.stream().map(temp -> {
      return new RoleUrl(this, temp);
    }).collect(Collectors.toList());
    this.setRoleUrls(roleUrls);
  }*/

  @Nationalized
  @Column(name = "ROLE_NAME", nullable = false, unique = true, length = 50)
  public String getName() {
    return name;
  }

  public void setName(String roleName) {
    this.name = roleName;
  }

  @Nationalized
  @Column(name = "ROLE_DESCRIPTION", nullable = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String roleDescription) {
    this.description = roleDescription;
  }

  // @ManyToMany
  // @JoinTable(name = "ROLE_URL",
  // joinColumns = @JoinColumn(name = "ROLE_PK"),
  // inverseJoinColumns = @JoinColumn(name = "URL_PK"))
  // @AuditJoinTable(name = "ROLE_URL_AUDIT")
  // public List<Url> getUrls() {
  // return urls;
  // }
  //
  // public void setUrls(List<Url> urls) {
  // this.urls = urls;
  // }

  // @AuditJoinTable - why
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "USER_ROLE",
             joinColumns = @JoinColumn(name = "ROLE_FK"),
             inverseJoinColumns = @JoinColumn(name = "USER_FK"))
  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  @Column(name = "DELETED", nullable = false)
  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  // Init all of the role collections
  @Transient
  public void init() {
    // Hibernate.initialize(this.getUrls());
    Hibernate.initialize(this.getUsers());
  }

  // Spring Security interface methods
  @Override
  @Transient
  public String getAuthority() {
    return name;
  }

  @NotAudited
  @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
  public List<RoleUrl> getRoleUrls() {
    return roleUrls;
  }

  public void setRoleUrls(List<RoleUrl> roleUrls) {
    this.roleUrls = roleUrls;
  }

  // @Column(name = "VIEW_BPMN", nullable = false)
  // public Boolean getViewBpmn() {
  // return viewBpmn;
  // }
  //
  // public void setViewBpmn(Boolean viewBpmn) {
  // this.viewBpmn = viewBpmn;
  // }

  @Nationalized
  @Column(name = "LANDING_PAGE", nullable = true)
  public String getDefaultLandingPage() {
    return defaultLandingPage;
  }

  public void setDefaultLandingPage(String defaultLandingPage) {
    this.defaultLandingPage = defaultLandingPage;
  }
}

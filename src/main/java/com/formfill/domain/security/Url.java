package com.formfill.domain.security;

import com.formfill.domain.BaseDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "URL")
public class Url extends BaseDomain {

  private String url;

  private String urlDescription;

  private Long parentFk;

  private UrlType type;

  public Url() {
    // default constructor
  }

  @Nationalized
  @Column(name = "URL", nullable = false, unique = true)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Nationalized
  @Column(name = "URL_DESCRIPTION", nullable = false)
  public String getUrlDescription() {
    return urlDescription;
  }

  public void setUrlDescription(String urlDescription) {
    this.urlDescription = urlDescription;
  }

  // @JsonIgnore
  // @ManyToMany(mappedBy = "urls")
  // public List<Role> getRoles() {
  // return roles;
  // }
  //
  // public void setRoles(List<Role> roles) {
  // this.roles = roles;
  // }

  // Init all of the url collections
/*  @Transient
  @PostConstruct
  public void init() {
    Hibernate.initialize(this.getRoles());
  }*/

  @Enumerated(EnumType.STRING)
  @Column(name = "URL_TYPE", length = 10, columnDefinition = "nvarchar(10)")
  public UrlType getType() {
    return type;
  }

  public void setType(UrlType type) {
    this.type = type;
  }

  @Column(name = "PARENT_FK", nullable = true)
  public Long getParentFk() {
    return parentFk;
  }

  public void setParentFk(Long parentFk) {
    this.parentFk = parentFk;
  }
}

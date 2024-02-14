package com.formfill.domain.security;

import com.formfill.domain.BaseDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = RoleUrl.TABLE_NAME,
       indexes = { @Index(name = RoleUrl.TABLE_NAME + "_IDX_1",
                          columnList = BaseDomain.PK + "," + BaseDomain.VERSION),
                   @Index(name = RoleUrl.TABLE_NAME + "_IDX_2", columnList = RoleUrl.ROLE_FK),
                   @Index(name = RoleUrl.TABLE_NAME + "_IDX_3", columnList = RoleUrl.URL_FK) })
public class RoleUrl extends BaseDomain {

  public static final String TABLE_NAME = "ROLE_URL";

  protected static final String ROLE_FK = "ROLE_FK";

  protected static final String URL_FK = "URL_FK";

  private Role role;

  private Url url;

  private Boolean readOnly = Boolean.FALSE;

  public RoleUrl() {
    super();
  }

  public RoleUrl(Role role, Url url) {
    super();
    setRole(role);
    setUrl(url);
  }

  @Column(name = "READ_ONLY", nullable = false, length = 1)
  public Boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  @ManyToOne
  @JoinColumn(name = ROLE_FK, foreignKey = @ForeignKey(name = "FK_ROLEURL_TO_ROLE"))
  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @ManyToOne
  @JoinColumn(name = URL_FK, foreignKey = @ForeignKey(name = "FK_ROLEURL_TO_URL"))
  public Url getUrl() {
    return url;
  }

  public void setUrl(Url url) {
    this.url = url;
  }
}

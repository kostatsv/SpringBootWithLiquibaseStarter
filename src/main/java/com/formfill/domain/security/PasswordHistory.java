package com.formfill.domain.security;

import com.formfill.domain.BaseDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "PASSWORD_HISTORY", indexes = { @Index(name = "PASSWORD_HISTORY_IDX_1", columnList = "USER_FK") })
public class PasswordHistory extends BaseDomain {

  private String password;

  public PasswordHistory() {
    // default constructor
  }

  public PasswordHistory(String password) {
    this.password = password;
  }

  @Nationalized
  @Column(name = "PASSWORD", nullable = false)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

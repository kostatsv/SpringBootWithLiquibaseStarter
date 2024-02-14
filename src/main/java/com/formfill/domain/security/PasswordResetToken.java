package com.formfill.domain.security;

import java.time.LocalDateTime;

import com.formfill.domain.BaseDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "PASSWORD_RESET_TOKEN")
public class PasswordResetToken extends BaseDomain {

  private String token;

  private String username;

  private boolean isPwExpiredRequest;

  @Nationalized
  @Column(name = "RESET_TOKEN", nullable = false, unique = true)
  public String getToken() {
    return token;
  }

  public void setToken(String resetToken) {
    this.token = resetToken;
  }

  @Nationalized
  @Column(name = "USERNAME")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Column(name = "IS_PW_EXPIRED_REQUEST", length = 1, nullable = false)
  public boolean isPwExpiredRequest() {
    return isPwExpiredRequest;
  }

  public void setPwExpiredRequest(boolean pwExpiredRequest) {
    isPwExpiredRequest = pwExpiredRequest;
  }
}

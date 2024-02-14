package com.formfill.domain.security;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.formfill.domain.BaseDomain;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Nationalized;
import org.hibernate.envers.AuditJoinTable;

@Entity
@Table(name = User.TABLE_NAME,
       indexes = { @Index(name = User.TABLE_NAME + "_IDX_1",
                          columnList = BaseDomain.PK + "," + BaseDomain.VERSION),
                   @Index(name = User.TABLE_NAME + "_IDX_2", columnList = User.ROLE_FK) })
public class User extends BaseDomain implements UserDetails {

  protected static final String TABLE_NAME = "USER";

  protected static final String ROLE_FK = "ROLE_FK";

  private String username;

  private String password;

  private String email;

  private LocalDate passwordExpires;

  private LocalDate passwordExpiresEmailDate;

  private LocalDate passwordNearlyExpiresEmailDate;

  private Integer consecutiveFailedLogin;

  private Boolean locked = Boolean.FALSE;

  private LocalDateTime unlockTime;

  private Boolean newPasswordRequired;

  private LocalDate accountStartDate;

  private LocalDate accountEndDate;

  private List<PasswordHistory> passwordHistory = new ArrayList<>();

  // private List<Role> roles;

  private Role role;

  private Boolean deleted;

  private String quickNavHotkey;

  private String defaultLandingPage;

  private boolean colourContrast;

  public User() {

  }

  /*public User(UserDTO userDTO,
              String encodedPassword,
              LocalDate passwordExpiryDate) {
    this.setUsername(userDTO.getUsername());
    this.setEmail(userDTO.getEmail());
    this.setPassword(encodedPassword);
    this.setPasswordExpires(passwordExpiryDate);
    this.setConsecutiveFailedLogin(0);
    this.setLocked(userDTO.getLocked());
    this.setAccountStartDate(userDTO.getAccountStartDate());
    this.setAccountEndDate(userDTO.getAccountEndDate());
    this.setPasswordHistory(new ArrayList<>());
    this.setNewPasswordRequired(false);
    this.setDeleted(false);
  }*/

  @Nationalized
  @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Nationalized
  @Column(name = "PASSWORD", nullable = false)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Nationalized
  @Column(name = "EMAIL", nullable = false, unique = true)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "PASSWORD_EXPIRES", nullable = false)
  public LocalDate getPasswordExpires() {
    return passwordExpires;
  }

  public void setPasswordExpires(LocalDate passwordExpires) {
    this.passwordExpires = passwordExpires;
  }

  @Column(name = "PASSWORD_EXPIRES_EMAIL_DATE")
  public LocalDate getPasswordExpiresEmailDate() {
    return passwordExpiresEmailDate;
  }

  public void setPasswordExpiresEmailDate(LocalDate passwordExpiresEmailDate) {
    this.passwordExpiresEmailDate = passwordExpiresEmailDate;
  }

  @Column(name = "PASSWORD_NEARLY_EXPIRES_EMAIL_DATE")
  public LocalDate getPasswordNearlyExpiresEmailDate() {
    return passwordNearlyExpiresEmailDate;
  }

  public void setPasswordNearlyExpiresEmailDate(LocalDate passwordNearlyExpiresEmailDate) {
    this.passwordNearlyExpiresEmailDate = passwordNearlyExpiresEmailDate;
  }

  @Column(name = "CONSECUTIVE_FAILED_LOGIN", nullable = false)
  public Integer getConsecutiveFailedLogin() {
    return consecutiveFailedLogin;
  }

  public void setConsecutiveFailedLogin(Integer consecutiveFailedLogin) {
    this.consecutiveFailedLogin = consecutiveFailedLogin;
  }

  @Column(name = "LOCKED", nullable = false)
  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  @Column(name = "UNLOCK_TIME", nullable = true)
  public LocalDateTime getUnlockTime() {
    return unlockTime;
  }

  public void setUnlockTime(LocalDateTime unlockTime) {
    this.unlockTime = unlockTime;
  }

  @Column(name = "NEW_PASSWORD_REQUIRED", nullable = false)
  public Boolean getNewPasswordRequired() {
    return newPasswordRequired;
  }

  public void setNewPasswordRequired(Boolean newPasswordRequired) {
    this.newPasswordRequired = newPasswordRequired;
  }

  @Column(name = "ACCOUNT_START_DATE", nullable = false)
  public LocalDate getAccountStartDate() {
    return accountStartDate;
  }

  public void setAccountStartDate(LocalDate accountStartDate) {
    this.accountStartDate = accountStartDate;
  }

  @Column(name = "ACCOUNT_END_DATE")
  public LocalDate getAccountEndDate() {
    return accountEndDate;
  }

  public void setAccountEndDate(LocalDate accountEndDate) {
    this.accountEndDate = accountEndDate;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "USER_FK")
  @AuditJoinTable(name = "USER_PASSWORD_HISTORY_AUDIT")
  public List<PasswordHistory> getPasswordHistory() {
    return passwordHistory;
  }

  public void setPasswordHistory(List<PasswordHistory> passwordHistory) {
    this.passwordHistory = passwordHistory;
  }

  // @ManyToMany(mappedBy = "users")
  // public List<Role> getRoles() {
  // return roles;
  // }
  //
  // public void setRoles(List<Role> roles) {
  // this.roles = roles;
  // }

  @ManyToOne
  @JoinColumn(name = ROLE_FK, foreignKey = @ForeignKey(name = "FK_USER_TO_ROLE"), nullable = false)
  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Column(name = "DELETED", nullable = false)
  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  // Init all of the users collections
  @Transient
  public void init() {
    Hibernate.initialize(this.getPasswordHistory());

    // Hibernate.initialize(this.getRoles());
    // for (Role role : this.getRoles()) {
    // Hibernate.initialize(role.getUsers());
    // Hibernate.initialize(role.getUrls());
    // }

    Hibernate.initialize(getRole().getUsers());
    // Hibernate.initialize(getRole().getUrls());
    Hibernate.initialize(getRole().getRoleUrls());
  }

  // Spring Security interface methods
  @Override
  @Transient
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // return this.roles;
    return Arrays.asList(getRole());
  }

  @Override
  @Transient
  public boolean isAccountNonExpired() {
    return this.accountEndDate.isAfter(LocalDate.now());
  }

  @Override
  @Transient
  public boolean isAccountNonLocked() {
    return !this.locked && !this.deleted;
  }

  @Override
  @Transient
  public boolean isCredentialsNonExpired() {
    return this.accountEndDate.isAfter(LocalDate.now());
  }

  @Override
  @Transient
  public boolean isEnabled() {
    return !this.locked && !this.deleted;
  }

  @Nationalized
  @Column(name = "QUICK_NAV_HOTKEY", nullable = true)
  public String getQuickNavHotkey() {
    return quickNavHotkey;
  }

  public void setQuickNavHotkey(String quickNavHotkey) {
    this.quickNavHotkey = quickNavHotkey;
  }

  @Nationalized
  @Column(name = "LANDING_PAGE", nullable = true)
  public String getDefaultLandingPage() {
    return defaultLandingPage;
  }

  public void setDefaultLandingPage(String defaultLandingPage) {
    this.defaultLandingPage = defaultLandingPage;
  }

  @Nationalized
  @Column(name = "COLOUR_CONTRAST", nullable = false)

  public boolean getColourContrast() {
    return colourContrast;
  }

  public void setColourContrast(boolean colourContrast) {
    this.colourContrast = colourContrast;
  }
}

package com.formfill.domain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@MappedSuperclass
public abstract class BaseDomain implements Serializable {

  public static final String PK = "PK";

  public static final String VERSION = "VERSION";

  private Long pk;

  private Integer version;

  private LocalDateTime created = LocalDateTime.now();

  private LocalDateTime lastUpdated = LocalDateTime.now();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = PK, nullable = false)
  public Long getPk() {
    return pk;
  }

  public void setPk(Long pk) {
    this.pk = pk;
  }

  @Version
  @Column(name = VERSION, nullable = false)
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Column(name = "CREATED", nullable = false)
  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  @Column(name = "LAST_UPDATED", nullable = false)
  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @PrePersist
  protected void onCreate() {
    lastUpdated = LocalDateTime.now();
    created = lastUpdated;
  }

  @PreUpdate
  protected void onUpdate() {
    lastUpdated = LocalDateTime.now();
  }

  @Transient
  public String byteToString(byte[] content) {
    return new String(content, StandardCharsets.UTF_8);
  }

  @Transient
  public byte[] stringToByte(String content) {
    return content.getBytes(StandardCharsets.UTF_8);
  }
}

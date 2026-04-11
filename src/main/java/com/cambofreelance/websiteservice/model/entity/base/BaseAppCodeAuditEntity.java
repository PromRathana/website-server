package com.cambofreelance.websiteservice.model.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseAppCodeAuditEntity extends BaseAuditEntity {

    @Column(name = "application_code", length = 100)
    private String applicationCode;
}
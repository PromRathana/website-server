package com.cambofreelance.websiteservice.model.entity;

import com.cambofreelance.websiteservice.model.entity.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "article_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class ArticleCategory extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_kh")
    private String nameKh;

    @Column(name = "status", length = 5)
    private String status = "ACT";
}
CREATE TABLE article_categories
(
    id         VARCHAR(36)  NOT NULL,
    code       VARCHAR(255) UNIQUE,
    name_en    VARCHAR(255) NOT NULL,
    name_kh    VARCHAR(255),
    status     VARCHAR(5) DEFAULT 'ACT',
    created_at TIMESTAMP NULL,
    created_by VARCHAR(150) NULL,
    updated_at TIMESTAMP NULL,
    updated_by VARCHAR(100) NULL,
    deleted_at TIMESTAMP NULL,
    deleted_by VARCHAR(100) NULL,
    CONSTRAINT pk_article_categories PRIMARY KEY (id)
);

CREATE INDEX idx_article_category_code ON article_categories (code);
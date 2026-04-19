CREATE TABLE articles
(
    id             VARCHAR(36)  NOT NULL,
    title_en       VARCHAR(255) NOT NULL,
    title_kh       VARCHAR(255),
    description_en TEXT,
    description_kh TEXT,
    content_en     TEXT,
    content_kh     TEXT,
    status         VARCHAR(5) DEFAULT 'DRF',
    category_id    VARCHAR(36),
    created_at     TIMESTAMP NULL,
    created_by     VARCHAR(150) NULL,
    updated_at     TIMESTAMP NULL,
    updated_by     VARCHAR(100) NULL,
    deleted_at     TIMESTAMP NULL,
    deleted_by     VARCHAR(100) NULL,
    CONSTRAINT pk_articles PRIMARY KEY (id),
    CONSTRAINT fk_articles_category FOREIGN KEY (category_id) REFERENCES article_categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_articles_status ON articles (status);
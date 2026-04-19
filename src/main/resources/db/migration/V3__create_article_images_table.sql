CREATE TABLE article_images
(
    id         VARCHAR(36) NOT NULL,
    article_id VARCHAR(36),

    file_name  VARCHAR(255),
    image_path TEXT        NOT NULL,
    image_type VARCHAR(100),
    is_primary BOOLEAN    DEFAULT FALSE,
    status     VARCHAR(3) DEFAULT 'ACT',

    created_at TIMESTAMP NULL,
    created_by VARCHAR(150) NULL,
    updated_at TIMESTAMP NULL,
    updated_by VARCHAR(100) NULL,
    deleted_at TIMESTAMP NULL,
    deleted_by VARCHAR(100) NULL,

    CONSTRAINT pk_article_images PRIMARY KEY (id),
    CONSTRAINT fk_article_images_article FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);

CREATE INDEX idx_article_images_status ON article_images (status);
-- catalog-service: tabla principal.
-- Hibernate NO crea esta tabla (ddl-auto=validate); la crea Flyway con este script.
-- Una migracion ya aplicada NUNCA se edita: para cambios se crea V2__..., V3__...

CREATE TABLE productos (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    distribuidor_id BIGINT          NOT NULL,              -- id del distribuidor en access-control-service (sin FK: otra base de datos)
    nombre          VARCHAR(120)    NOT NULL,
    precio          DECIMAL(12,2)   NOT NULL,
    existencias     INT             NOT NULL DEFAULT 0,    -- unidades disponibles en inventario
    estado          VARCHAR(20)     NOT NULL,              -- ACTIVO, INACTIVO
    PRIMARY KEY (id),
    INDEX idx_productos_distribuidor (distribuidor_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

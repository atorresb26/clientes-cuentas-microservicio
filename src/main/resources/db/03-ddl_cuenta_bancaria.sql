CREATE TABLE cuenta_bancaria
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY,
    api_id         VARCHAR(36)             DEFAULT RANDOM_UUID() NOT NULL,
    cliente_id     BIGINT         NOT NULL,
    tipo_cuenta_id BIGINT         NOT NULL,
    total          DECIMAL(19, 2) NOT NULL DEFAULT 0
);

ALTER TABLE cuenta_bancaria
    ADD CONSTRAINT pk_cuenta_bancaria PRIMARY KEY (id);

ALTER TABLE cuenta_bancaria
    ADD CONSTRAINT fk_cuenta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id);
ALTER TABLE cuenta_bancaria
    ADD CONSTRAINT fk_cuenta_tipo FOREIGN KEY (tipo_cuenta_id) REFERENCES tipo_cuenta (id);

CREATE INDEX idx_cuenta_cliente ON cuenta_bancaria (cliente_id);
CREATE UNIQUE INDEX idx_cuenta_bancaria_api_id ON cuenta_bancaria (api_id);
CREATE INDEX idx_cuenta_tipo ON cuenta_bancaria (tipo_cuenta_id);

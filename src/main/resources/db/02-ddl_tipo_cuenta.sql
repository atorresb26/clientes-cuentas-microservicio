-- Creamos una tabla maestra para los datos de cuenta_bancaria.tipoCuenta
CREATE TABLE tipo_cuenta
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY,
    codigo VARCHAR(50)  NOT NULL,
    nombre   VARCHAR(100) NOT NULL
);

ALTER TABLE tipo_cuenta ADD CONSTRAINT pk_tipo_cuenta PRIMARY KEY (id);
CREATE UNIQUE INDEX idx_tipo_cuenta_codigo ON tipo_cuenta(codigo);

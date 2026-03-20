CREATE TABLE cliente
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY,
    dni              VARCHAR(20)  NOT NULL,
    nombre           VARCHAR(100),
    apellido1        VARCHAR(100),
    apellido2        VARCHAR(100),
    fecha_nacimiento DATE
);

ALTER TABLE cliente ADD CONSTRAINT pk_cliente PRIMARY KEY (id);

CREATE UNIQUE INDEX idx_cliente_dni ON cliente(dni);

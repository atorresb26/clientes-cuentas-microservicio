INSERT INTO cuenta_bancaria (cliente_id, tipo_cuenta_id, total)
VALUES (SELECT id FROM cliente WHERE dni = '11111111A', SELECT id FROM tipo_cuenta WHERE codigo = 'PREM', 150000),
       (SELECT id FROM cliente WHERE dni = '11111111A', SELECT id FROM tipo_cuenta WHERE codigo = 'NRML', 20000),
       (SELECT id FROM cliente WHERE dni = '22222222B', SELECT id FROM tipo_cuenta WHERE codigo = 'NRML', 50000),
       (SELECT id FROM cliente WHERE dni = '22222222B', SELECT id FROM tipo_cuenta WHERE codigo = 'JR', 300),
       (SELECT id FROM cliente WHERE dni = '33333333C', SELECT id FROM tipo_cuenta WHERE codigo = 'JR', 300),
       (SELECT id FROM cliente WHERE dni = '44444444D', SELECT id FROM tipo_cuenta WHERE codigo = 'NRML', 75000),
       (SELECT id FROM cliente WHERE dni = '55555555E', SELECT id FROM tipo_cuenta WHERE codigo = 'PREM', 120000);

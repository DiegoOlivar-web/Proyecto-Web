SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO categoria (id, nombre, descripcion) VALUES (1, 'Pollos a la Brasa', 'Platos de pollo rostizado al carbón');
INSERT INTO categoria (id, nombre, descripcion) VALUES (2, 'Parrillas', 'Platos de carnes y embutidos a la parrilla');
INSERT INTO categoria (id, nombre, descripcion) VALUES (3, 'Combos Especiales', 'Promociones que combinan pollo, parrilla y bebidas');
INSERT INTO categoria (id, nombre, descripcion) VALUES (4, 'Acompañamientos', 'Platos adicionales para complementar el menú');
INSERT INTO categoria (id, nombre, descripcion) VALUES (5, 'Bebidas', 'Opciones refrescantes para acompañar los platos');

INSERT INTO cliente (id, nombre, telefono, correo, contrasena, direccion) VALUES (1, 'Andres', '942448915', 'admin@gmail.com', '1234', 'Av. Alfredo Mendiola 6377, Los Olivos');
INSERT INTO cliente (id, nombre, telefono, correo, contrasena, direccion) VALUES (2, 'ricardo', '123456789', 'ricardo@gmail.com', '1234', 'Av. Mendiola 6377, Los Olivos');
INSERT INTO cliente (id, nombre, telefono, correo, contrasena, direccion) VALUES (3, 'Diego Olivar Santiago', '900458749', 'diego@gmail.com', 'admin', 'Los olivos');
INSERT INTO cliente (id, nombre, telefono, correo, contrasena, direccion) VALUES (4, 'Marco Sanchez', '952365748', 'marcosanchez@gmail.com', 'admin', 'Av. Prócedes, Lima, Lima');

INSERT INTO direccion (id, cliente_id, alias, direccion, referencia, ciudad, distrito) VALUES (1, 1, 'Principal', 'Av. Alfredo Mendiola 6377, Los Olivos', NULL, 'Lima', 'Los Olivos');
INSERT INTO direccion (id, cliente_id, alias, direccion, referencia, ciudad, distrito) VALUES (2, 2, 'Principal', 'Av. Mendiola 6377, Los Olivos', NULL, 'Lima', 'Los Olivos');
INSERT INTO direccion (id, cliente_id, alias, direccion, referencia, ciudad, distrito) VALUES (3, 3, 'Principal', 'Los olivos', NULL, 'Lima', 'Los Olivos');
INSERT INTO direccion (id, cliente_id, alias, direccion, referencia, ciudad, distrito) VALUES (4, 4, 'Principal', 'Av. Prócedes, Lima, Lima', NULL, 'Lima', NULL);

INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (1, '1/4 Pollo a la Brasa', 'Cuarto de pollo rostizado al carbón con papas fritas y ensalada.', 18.0, 1, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (2, 'Salchipollo', 'Trozos de pollo a la brasa y salchicha sobre una cama de papas fritas.', 20.0, 1, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (3, 'Pocho (Pollo + Anticucho)', 'Pollo a la brasa con brochetas de corazón de res maceradas en ají panca.', 20.0, 1, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (4, 'Pechuga a la Parrilla', 'Filete de pechuga marinado en especias peruanas, cocinado a las brasas.', 19.0, 1, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (5, '1/2 Pollo a la Brasa', 'Medio pollo rostizado al carbón con papas fritas y ensalada fresca.', 32.0, 1, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (6, 'Pollo Entero a la Brasa', 'Pollo entero dorado al carbón, perfecto para toda la familia.', 58.0, 1, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (7, 'Chuleta de Cerdo', 'Jugosa chuleta de cerdo sellada a la parrilla, con papas fritas y guarnición de vegetales.', 18.0, 2, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (8, 'Churrasco', 'Corte de res tierno cocinado al término ideal en la parrilla para resaltar su jugosidad.', 18.0, 2, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (9, 'Marucha', 'Corte parrillero tradicional, apreciado por su suavidad. Ideal para amantes de la carne.', 18.0, 2, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (10, '1/2 Parrilla Especial', 'Selección de carnes y embutidos al carbón. Se sirve con papas fritas y ensalada.', 46.0, 2, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (11, 'Parrilla TORI', 'Nuestra parrilla completa: carnes premium al carbón con papas y ensalada. Para 3-4 personas.', 70.0, 2, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (12, 'Anticuchos', 'Brochetas de corazón de res maceradas en ají panca, servidas con papas y choclo.', 15.0, 2, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (13, 'Combo Familiar', '1 pollo entero + 2 porciones de papas + 2 ensaladas + 1 gaseosa. Ideal para compartir en familia.', 72.0, 3, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (14, 'Combo Pareja', '1/2 pollo + 1 chuleta a la parrilla + 2 porciones de papas + 2 bebidas. ¡La cita perfecta!', 52.0, 3, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (15, 'Combo TORI', '1/4 pollo + anticuchos + salchipapa + 1 bebida. El favorito de nuestros clientes frecuentes.', 38.0, 3, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (16, 'Salchipapa', 'El street food peruano por excelencia. Papas fritas crujientes con salchichas. Perfecta para un antojo rápido.', 13.0, 4, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (17, 'Papas Fritas', 'Porción generosa de papas fritas doradas y crujientes, sazonadas al punto justo. Acompañan cualquier plato.', 8.0, 4, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (18, 'Ensalada Fresca', 'Mix de lechugas, tomate, cebolla y palta con aderezo de la casa. La opción más saludable del menú.', 9.0, 4, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (19, 'Chaufa de Pollo', 'Arroz chaufa con trozos de pollo, vegetales salteados, huevo y salsa de soya. El sabor chifa en su máxima expresión.', 14.0, 4, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (20, 'Gaseosa (350ml)', 'Inca Kola, Coca-Cola o Sprite bien fría. La compañera ideal de cualquier plato.', 4.0, 5, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (21, 'Chicha Morada', 'Preparada en casa con maíz morado, clavo y canela. Refrescante y 100% natural.', 5.0, 5, 1);
INSERT INTO producto (id, nombre, descripcion, precio, categoria_id, disponible) VALUES (22, 'Limonada Frozen', 'Limonada helada con menta y hielo triturado. La bebida del verano peruano.', 7.0, 5, 1);

INSERT INTO pedido (id, cliente_id, direccion_id, comprobante_id, fecha_pedido, estado, total, fecha, usuario) VALUES (1, 1, 1, NULL, '2026-06-11 05:53:53', 'PENDIENTE', 20.0, '2026-06-11 05:53:53', 'Andres');
INSERT INTO pedido (id, cliente_id, direccion_id, comprobante_id, fecha_pedido, estado, total, fecha, usuario) VALUES (2, 1, 1, NULL, '2026-06-11 05:59:11', 'PENDIENTE', 52.0, '2026-06-11 05:59:11', 'Andres');
INSERT INTO pedido (id, cliente_id, direccion_id, comprobante_id, fecha_pedido, estado, total, fecha, usuario) VALUES (3, 1, 1, NULL, '2026-06-11 06:09:17', 'PENDIENTE', 20.0, '2026-06-11 06:09:17', 'Andres');

INSERT INTO detalle_pedido (id, pedido_id, producto_id, cantidad, precio_unitario) VALUES (1, 3, 3, 1, 20.0);

ALTER TABLE categoria AUTO_INCREMENT = 6;
ALTER TABLE cliente AUTO_INCREMENT = 5;
ALTER TABLE direccion AUTO_INCREMENT = 5;
ALTER TABLE producto AUTO_INCREMENT = 23;
ALTER TABLE pedido AUTO_INCREMENT = 4;
ALTER TABLE detalle_pedido AUTO_INCREMENT = 2;

SET FOREIGN_KEY_CHECKS = 1;

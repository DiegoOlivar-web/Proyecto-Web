INSERT INTO direccion (cliente_id, alias, direccion, referencia, ciudad, distrito)
SELECT
    c.id,
    'Principal',
    c.direccion,
    NULL,
    'Lima',
    CASE
        WHEN LOWER(c.direccion) LIKE '%olivos%' THEN 'Los Olivos'
        ELSE NULL
    END
FROM cliente c
WHERE c.direccion IS NOT NULL
  AND c.direccion <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM direccion d
      WHERE d.cliente_id = c.id
        AND d.direccion = c.direccion
  );

UPDATE pedido p
JOIN cliente c ON c.id = p.cliente_id
LEFT JOIN (
    SELECT cliente_id, MIN(id) AS direccion_id
    FROM direccion
    GROUP BY cliente_id
) d ON d.cliente_id = p.cliente_id
SET
    p.direccion_id = COALESCE(p.direccion_id, d.direccion_id),
    p.usuario = COALESCE(p.usuario, c.nombre),
    p.fecha = COALESCE(p.fecha, p.fecha_pedido, NOW())
WHERE p.cliente_id IS NOT NULL;

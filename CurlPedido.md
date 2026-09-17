# Pruebas cURL - API REST Pedidos

Documentación de comandos cURL para probar las operaciones de la entidad `Pedido` expuestas en `http://localhost:8080/api/pedidos`.

---

## 1. Registrar Nuevo Pedido (POST)

### Registro 
Crea un nuevo pedido registrado en el sistema.

`/api/pedidos`
```bash
{
    "numeroPedido": "PED-001",
    "total": 250.75,
    "estado": "PENDIENTE"
}
```

### Registro Duplicado 
Intento de registrar un pedido con el mismo numeroPedido previamente ingresado.

`/api/pedidos`
```bash
{
    "numeroPedido": "PED-001",
    "total": 100.00,
    "estado": "PENDIENTE"
}
```

## 2. Consultar Pedido por ID (GET)
Obtiene el detalle de un pedido a partir de su identificador único.

`/api/pedidos/1`

## 3. Consultar Pedidos por Estado (GET)
Filtra y retorna la lista de pedidos de acuerdo con su campo estado

`/api/pedidos/estado/PENDIENTE`
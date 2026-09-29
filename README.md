# SpeedFast

Proyecto desarrollado en Java para simular la gestión de pedidos y entregas de la empresa ficticia SpeedFast.

## Funcionalidades

La aplicación permite:

- Registrar pedidos de tipo COMIDA, ENCOMIENDA y EXPRESS.
- Validar los datos ingresados antes de registrar un pedido.
- Visualizar los pedidos almacenados mediante JTable.
- Guardar y consultar pedidos desde una base de datos MySQL.
- Consultar repartidores registrados en la base de datos.
- Seleccionar la cantidad de repartidores que participarán.
- Ejecutar entregas concurrentes mediante múltiples hilos.
- Actualizar los estados de los pedidos:
  - PENDIENTE
  - EN_REPARTO
  - ENTREGADO
- Registrar las entregas realizadas, asociando cada pedido con un repartidor, fecha y hora.
- Recuperar pedidos almacenados de forma persistente después de reiniciar la aplicación.

## Estructura del proyecto

```text
src/
├── UI/
│   ├── PanelEntrega.java
│   ├── PanelInicio.java
│   ├── PanelListaPedidos.java
│   ├── PanelRegistroPedido.java
│   └── VentanaPrincipal.java
│
├── app/
│   └── Main.java
│
├── dao/
│   ├── ConexionBD.java
│   ├── EntregaDAO.java
│   ├── PedidoDAO.java
│   └── RepartidorDAO.java
│
└── model/
    ├── Cancelable.java
    ├── ControladorDeEnvios.java
    ├── Despachable.java
    ├── Entrega.java
    ├── EstadoPedido.java
    ├── Pedido.java
    ├── PedidoComida.java
    ├── PedidoEncomienda.java
    ├── PedidoExpress.java
    ├── PrioridadPedido.java
    ├── Rastreable.java
    ├── Repartidor.java
    └── ZonaDeCarga.java
```

## Base de datos

El proyecto utiliza una base de datos MySQL llamada:

```text
speedfast_db
```

El archivo:

```text
speedfast_db.sql
```

incluido en la raíz del proyecto permite crear las tablas necesarias:

- repartidor
- pedido
- entrega

También incorpora los repartidores iniciales utilizados por la aplicación.

### Crear la base de datos

1. Abrir MySQL Workbench.
2. Abrir el archivo `speedfast_db.sql`.
3. Ejecutar el script completo.
4. Verificar que se haya creado el esquema `speedfast_db`.

## Configuración de la conexión

La conexión se configura en:

```text
src/dao/ConexionBD.java
```

Los valores utilizados son:

```java
private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
private static final String USER = "root";
private static final String PASSWORD = "CAMBIAR_AQUI";
```

Antes de ejecutar el proyecto, reemplazar:

```text
CAMBIAR_AQUI
```

por la contraseña local del usuario `root` de MySQL.

## MySQL Connector/J

El controlador JDBC utilizado se encuentra dentro de:

```text
lib/mysql-connector-j-26.7.0.jar
```

El proyecto NetBeans está configurado para utilizar este archivo desde el Classpath.

## Ejecución

1. Iniciar MySQL Server.
2. Verificar que exista la base de datos `speedfast_db`.
3. Configurar la contraseña correspondiente en `ConexionBD.java`.
4. Abrir el proyecto en NetBeans.
5. Ejecutar:

```text
app/Main.java
```

La aplicación abrirá la interfaz gráfica de SpeedFast.

## Uso de la aplicación

### Registrar pedido

Desde la opción **Registrar pedido** se solicitan:

- ID
- Dirección
- Distancia en kilómetros
- Tipo de pedido

El pedido se registra en MySQL con estado inicial:

```text
PENDIENTE
```

### Listar pedidos

La opción **Listar pedidos** consulta directamente la base de datos y muestra los pedidos mediante JTable.

### Iniciar entregas

La opción **Iniciar entregas** permite seleccionar la cantidad de repartidores que participarán en la simulación.

Los repartidores son recuperados desde MySQL y ejecutan las entregas concurrentemente.

Durante el proceso, un pedido puede pasar por:

```text
PENDIENTE → EN_REPARTO → ENTREGADO
```

Al finalizar una entrega se registra en la tabla `entrega`:

- Pedido
- Repartidor
- Fecha
- Hora

## Manejo de concurrencia

La zona de carga es un recurso compartido utilizado por múltiples repartidores.

El proyecto utiliza mecanismos de concurrencia para impedir que un mismo pedido sea retirado simultáneamente por más de un repartidor.

También se contempla el caso de interrupción de un repartidor. Si una entrega se interrumpe antes de finalizar, el pedido vuelve al estado:

```text
PENDIENTE
```

y se reincorpora a la zona de carga.

## Persistencia

Los pedidos permanecen almacenados en MySQL después de cerrar la aplicación.

Al volver a iniciar SpeedFast y acceder al listado de pedidos, la información es recuperada nuevamente desde la base de datos.


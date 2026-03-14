# Proyecto: Procesamiento de Transacciones Bancarias con RabbitMQ y Java

## Video del proyecto

Enlace del video de demostración:

```text
https://drive.google.com/file/d/1Szp4SDZGKBoTdUFQE34qFE50U7oVO--t/view?usp=drive\_link
```

\---

## Descripción

En este proyecto desarrollé un sistema distribuido utilizando Java, Maven y RabbitMQ, aplicando el patrón Producer-Consumer.

La idea principal del proyecto es consumir un lote de transacciones bancarias desde una API GET, separar cada transacción según el banco destino y enviarla a una cola de RabbitMQ. Luego, otro componente llamado Consumer escucha esas colas, toma cada transacción y la envía a una API POST para almacenarla.

También se implementó manejo de errores para evitar pérdida de mensajes, usando ACK manual, reintento básico y reencolado cuando el POST falla.

\---

## Arquitectura del proyecto

El flujo del sistema funciona así:

```text
API GET -> Producer -> RabbitMQ -> Consumer -> API POST
```

### Explicación general

* El Producer obtiene las transacciones desde la API.
* Luego revisa el campo `bancoDestino` de cada transacción.
* Según ese valor, crea o usa una cola en RabbitMQ.
* Después publica la transacción en la cola correspondiente.
* El Consumer escucha varias colas al mismo tiempo.
* Cuando recibe una transacción, la deserializa, le agrega `nombre` y `carnet`, y la envía a la API POST.
* Si el POST responde correctamente, se hace ACK manual.
* Si falla, el mensaje se reencola para que no se pierda.

\---

## Tecnologías utilizadas

* Java 17
* Maven
* RabbitMQ
* HttpClient
* Jackson

\---

## Estructura del proyecto

Este proyecto está dividido en dos partes:

### 1\. Proyecto Producer

Se encarga de:

* consumir el endpoint GET
* parsear el JSON
* recorrer las transacciones
* identificar el banco destino
* crear colas por banco
* publicar cada transacción en RabbitMQ

### 2\. Proyecto Consumer

Se encarga de:

* escuchar múltiples colas
* consumir mensajes JSON
* deserializar a objetos Java
* agregar nombre y carnet
* enviar cada transacción al endpoint POST
* confirmar mensajes con ACK manual

\---

## APIs utilizadas

### API GET

Endpoint para obtener transacciones:

```text
https://hly784ig9d.execute-api.us-east-1.amazonaws.com/default/transacciones
```

### API POST

Endpoint para guardar transacciones:

```text
https://7e0d9ogwzd.execute-api.us-east-1.amazonaws.com/default/guardarTransacciones
```

\---

## Modelos utilizados

Para modelar el JSON utilicé las siguientes clases:

* `LoteTransacciones`
* `Transaccion`
* `Detalle`
* `Referencias`
* `TransaccionPost`

Estas clases ayudan a convertir el JSON de la API a objetos Java y viceversa.

\---

## Funcionamiento del Producer

El Producer hace lo siguiente:

1. Llama al endpoint GET de transacciones.
2. Recibe un lote con varias transacciones.
3. Convierte el JSON a objetos Java.
4. Recorre cada transacción.
5. Lee el campo `bancoDestino`.
6. Crea una cola con el nombre del banco si no existe.
7. Envía la transacción como mensaje JSON a RabbitMQ.

Ejemplo de colas creadas:

* BAC
* BANRURAL
* BI
* GYT

\---

## Funcionamiento del Consumer

El Consumer hace lo siguiente:

1. Escucha varias colas de RabbitMQ.
2. Recibe cada mensaje como JSON.
3. Lo convierte a objeto Java.
4. Construye un nuevo objeto `TransaccionPost`.
5. Agrega los campos `nombre` y `carnet`.
6. Envía la transacción al endpoint POST.
7. Si la respuesta es exitosa, hace ACK manual.
8. Si falla, reintenta una vez y luego reencola el mensaje.

\---

## Manejo de errores

Para evitar pérdida de transacciones, se implementó:

### ACK manual

El mensaje solo se confirma cuando el POST responde correctamente.

### Reintento básico

Si el POST falla, se intenta una vez más.

### Reencolado

Si después del reintento sigue fallando, el mensaje se vuelve a colocar en la cola con:

```java
channel.basicNack(deliveryTag, false, true);
```

### Error controlado si RabbitMQ no está disponible

Si RabbitMQ se apaga o no está corriendo, el sistema muestra el error en consola sin romperse de forma silenciosa.

\---

## Pruebas realizadas

### 1\. Flujo normal

* Se ejecutó el Producer.
* Se publicaron transacciones en RabbitMQ.
* Se crearon colas por banco.
* Se ejecutó el Consumer.
* Se enviaron transacciones al POST correctamente.
* Se hizo ACK manual.

### 2\. Falla del POST

* Se forzó un error en el endpoint POST.
* El Consumer no hizo ACK.
* El mensaje se reencoló.
* Se comprobó que la transacción no se perdió.

### 3\. RabbitMQ apagado

* Se apagó RabbitMQ.
* Al ejecutar el Producer, se mostró error controlado en consola.
* Esto comprobó el manejo básico de errores de conexión.

\---

## Ejecución del proyecto

### Paso 1: levantar RabbitMQ

Si se usa Docker:

```bash
docker run -d --hostname rabbit-local --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

Panel de administración:

```text
http://localhost:15672
```

Credenciales:

```text
guest / guest
```

### Paso 2: ejecutar el Producer

Correr la clase principal:

```text
ProducerApp.java
```

### Paso 3: verificar RabbitMQ

Entrar a `Queues and Streams` y comprobar que se crearon las colas por banco.

### Paso 4: ejecutar el Consumer

Correr la clase principal:

```text
ConsumerApp.java
```

### Paso 5: verificar procesamiento

Revisar en consola:

* mensajes recibidos
* JSON enviado al POST
* status response
* ACK enviado

\---

## Datos del estudiante

* Nombre: RONALD ANTONIO AROCHE SANTOS
* Carnet: 0905-24-17495

\---

## Conclusión

Con este proyecto se logró implementar correctamente el flujo solicitado en el enunciado:

* consumo del GET
* publicación en colas por banco
* consumo desde RabbitMQ
* envío al POST
* ACK manual
* reintento
* reencolado
* manejo de errores

En general, el sistema permite procesar transacciones de forma desacoplada y segura, evitando pérdida de mensajes cuando ocurre un fallo.


# Recuperatorio Práctico (Simulacro 2): Artículos en Promoción y Descuentos

**Tiempo orientativo: 45 minutos.** Partir del proyecto base de recuperatorio. Se entregan esta consigna, `datos-parcial.csv` y el archivo de pruebas `EvolucionTest.java`. Mantener los tests existentes.

El CSV del parcial contiene 140 filas de datos más encabezado (141 líneas); es un conjunto nuevo y distinto al inicial de 60 filas.

El punto de ventas incorpora una campaña comercial de **artículos en promoción** para liquidar excedentes de temporada. Se necesita procesar artículos comunes y promocionales en una misma colección, calcular correctamente sus ganancias y obtener estadísticas comerciales.

---

## Reglas Nuevas

### 1. Nuevo encabezado
El nuevo encabezado del archivo es:
```text
id,descripcion,categoria,proveedor,precio,stock,estado,costoUnitario,descuentoPromocional
```
Conservar también el formato original de ocho columnas: todos los artículos del formato anterior se consideran artículos comunes sin descuento (`descuentoPromocional = 0`). Cada fila debe tener el ancho indicado por su encabezado; no aceptar filas de ocho campos bajo un encabezado de nueve, ni a la inversa.

### 2. Descuentos y comportamiento
Para las filas con estado `ACTIVO` o `SUSPENDIDO` del nuevo formato, el noveno campo es un número entero (tras quitar espacios externos):

- **`0`**: Conserva todas las reglas anteriores y su ganancia. Es un artículo común sin descuento:
  $$\text{importe} = \text{stock} \times (\text{precio} - \text{costoUnitario})$$
- **`10`, `20` o `30`**: Son los únicos porcentajes de descuento promocional admitidos. El artículo queda en promoción y se modela como una especialización `ArticuloPromocional` que hereda de `Articulo`.
  - **Precio promocional**: El precio unitario de venta efectivo se reduce según el descuento:
    $$\text{precioConDescuento} = \text{precio} \times \left(1 - \frac{\text{descuento}}{100.0}\right)$$
  - **Invariante de no pérdida**: El punto de ventas nunca vende por debajo de su costo. Por lo tanto, el precio con descuento debe ser **mayor o igual al costo unitario** ($\text{precioConDescuento} \ge \text{costoUnitario}$). Si el descuento hace que el precio de venta sea menor al costo de adquisición, la fila se considera inválida (`IllegalArgumentException`).
  - **Ganancia**: El importe del artículo promocional se calcula en base a su precio con descuento:
    $$\text{importe} = \text{stock} \times (\text{precioConDescuento} - \text{costoUnitario})$$
- Un valor vacío, no numérico o un porcentaje distinto de `0`, `10`, `20` o `30` hace inválida la fila.

### 3. Precedencia
- Verificar primero que la cantidad de columnas coincida con el encabezado.
- Luego descartar `CANCELADO` sin validar números, costos ni descuentos.
- Luego validar estados permitidos (`ACTIVO` o `SUSPENDIDO`), porcentajes de descuento y reglas de dominio. Las filas inválidas se registran con el número de línea física y la carga continúa.

### Ejemplos (precio, costo, descuento, stock, ganancia):
- Artículo con precio 200, costo 100, descuento 0, stock 10:
  $$\text{importe} = 10 \times (200 - 100) = 1000.0$$
- Artículo con precio 200, costo 100, descuento 20, stock 10:
  - $\text{precioConDescuento} = 200 \times 0.80 = 160.0 \ge 100.0$ (válido).
  - $\text{importe} = 10 \times (160 - 100) = 600.0$.
- Artículo con precio 100, costo 85, descuento 30:
  - $\text{precioConDescuento} = 100 \times 0.70 = 70.0 < 85.0$ $\rightarrow$ **inválido** (vendería a pérdida, lanza `IllegalArgumentException`).

---

## Resultado Requerido

1. **Integración**: Integrar la lectura de ambas versiones del CSV (8 y 9 columnas) en `ParserArticulos` sin duplicar el proceso de carga.
2. **Polimorfismo**: Modelar `ArticuloPromocional extends Articulo` redefiniendo `importe()`.
3. **Colección**: Incorporar en `Inventario` un conteo por modalidad o tipo de venta (`porPromocion()` o `porModalidad()`). Puede elegir el nombre y representación; por ejemplo, agrupar en `"SIN_DESCUENTO"` y `"PROMOCION"`, o por porcentaje de descuento.
4. **Ejecutable `Main`**:
   - Hacer que el `Main` use por defecto `datos/datos-parcial.csv`.
   - Mostrar el resumen de procesamiento (leídas, procesadas, descartadas, inválidas), `ValorTotal`, `Por Categoria` y el nuevo agrupamiento por promoción.
5. **Tests**: Conservar el comportamiento anterior y pasar tanto los tests originales (`ArticuloTest`, `ParserArticulosTest`) como los nuevos (`EvolucionTest`).

---

## Comandos de Ejecución

```sh
mvn test
java -cp target/classes utnfc.backend.parcial.Main datos/datos-parcial.csv
```

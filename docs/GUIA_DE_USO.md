# Guía de uso

HidroFES Flow calcula en unidades del Sistema Internacional. Lo que escribes puede estar en otra unidad: la app convierte el valor antes de operar y muestra la equivalencia debajo del campo.

Se acepta punto o coma como separador decimal. El botón de calcular se activa cuando todos los campos obligatorios contienen un número.

## Pantalla de inicio

Dos entradas:

- **Conductos a presión.** Tubería llena, trabajando a presión.
- **Canales.** Flujo a superficie libre.

El icono de información abre la documentación dentro de la app: el resumen de cada módulo, las unidades y las constantes.

## Conductos a presión

### Caudal

Responde a la pregunta: con este tubo y esta carga disponible, ¿cuánta agua pasa?

| Campo | Qué es | Obligatorio |
|---|---|---|
| Diámetro interior | Diámetro interno de la tubería | Sí |
| Longitud | Longitud de la conducción | Sí |
| Material | Fija la rugosidad absoluta ε | Sí, PVC por defecto |
| Carga total disponible H | Desnivel útil entre la superficie libre de aguas arriba y el punto de descarga, en metros | Sí |
| ΣK | Suma de los coeficientes de pérdidas locales. Si no hay accesorios, déjalo en 0 | Sí |

El resultado principal es el caudal en L/s, con su equivalente en m³/s. Debajo aparecen la velocidad, el área, el Reynolds, el factor de fricción, el régimen, y el reparto de la carga H entre fricción, pérdidas locales y carga de velocidad. Esas tres últimas deben sumar H.

### Diámetro

El problema inverso: ¿qué diámetro hace falta para llevar este gasto con la carga disponible?

Pide el gasto (por defecto en L/s), la longitud, el material, H y ΣK. Devuelve el diámetro en metros, milímetros y pulgadas, además de las mismas magnitudes hidráulicas que el módulo de caudal.

Los dos módulos usan la misma ecuación. Si tomas el caudal que dio el primero y lo introduces en el segundo, con el resto de datos iguales, el diámetro que regresa es el de partida.

### Pérdidas por fricción

Pérdida de carga a lo largo de un tramo recto. Tres métodos, elegibles con los chips de la parte superior:

| Método | Coeficiente que usa | Cuándo conviene |
|---|---|---|
| Darcy-Weisbach | Rugosidad ε del material | Caso general. Vale para cualquier régimen |
| Hazen-Williams | Coeficiente C | Agua a temperatura ambiente, tubos de más de 50 mm |
| Chézy-Manning | Coeficiente n | Cuando el resto del proyecto ya está en Manning |

Los datos son longitud, diámetro, gasto y material. La app resuelve los tres métodos con los mismos datos y muestra la diferencia porcentual de Hazen-Williams y Manning respecto a Darcy-Weisbach.

Si el diámetro baja de 50 mm o la velocidad sale del intervalo 0.6 a 3 m/s, Hazen-Williams sigue calculando y añade una advertencia.

### Pérdidas locales

Pérdidas en piezas especiales. Indica el gasto y el diámetro, y suma las piezas con los botones de cada accesorio. El coeficiente K de cada uno se muestra en la fila; el ΣK total aparece en la barra superior y se actualiza al cambiar la cantidad.

El resultado es la pérdida h_L en metros de columna de agua, la velocidad y la carga de velocidad. Si no seleccionas ninguna pieza, ΣK vale 0 y la pérdida es nula: la app lo advierte.

Ese ΣK es el valor que puedes copiar a los módulos de caudal y diámetro.

### Celeridad

Velocidad con la que viaja una onda de presión al cerrar una válvula.

| Campo | Qué es | Obligatorio |
|---|---|---|
| Diámetro interior | Diámetro interno | Sí |
| Espesor de pared | Espesor del tubo | Sí |
| Material | Fija el módulo de Young E | Sí, PVC por defecto |
| Longitud de la conducción | Para el tiempo crítico 2L/a | No |
| Velocidad de régimen | Para la sobrepresión de Joukowsky | No |

Sin los dos últimos datos la app entrega la celeridad, el límite teórico del agua libre y la sobrepresión por cada metro por segundo de cambio de velocidad. Con ellos añade el tiempo que separa una maniobra lenta de una rápida, y la sobrepresión de un cierre instantáneo.

Si la relación diámetro/espesor es muy alta o muy baja, aparece una advertencia sobre la hipótesis de pared delgada.

## Canales

Flujo uniforme: la pendiente de la plantilla, la de la superficie libre y la de la línea de energía son la misma.

1. Elige la geometría. La fila de chips se desplaza si no cabe en pantalla.
2. Completa la geometría que pide esa sección.

| Geometría | Datos de la sección |
|---|---|
| Rectangular | Ancho de plantilla b y tirante y |
| Triangular | Talud z (horizontal entre vertical) y tirante y |
| Trapezoidal | Ancho b, talud z y tirante y |
| Circular | Diámetro D y tirante y, con y ≤ D |

3. Indica la pendiente S en m/m y el coeficiente de Manning n. Bajo el campo hay tres referencias: tierra 0.025, concreto 0.014, PVC 0.009.

El resultado incluye caudal, velocidad, área, perímetro mojado, radio hidráulico, espejo de agua, profundidad hidráulica, energía específica, número de Froude, régimen y tirante crítico.

El régimen se lee así:

- **Subcrítico** (Fr < 1). El control del flujo está aguas abajo.
- **Crítico** (Fr ≈ 1). La energía específica es mínima y la superficie es inestable.
- **Supercrítico** (Fr > 1). El control está aguas arriba.

En sección circular, cuando el tirante llega al diámetro la sección trabaja llena, el espejo de agua se anula y el número de Froude se reporta como no definido. A partir de y/D ≈ 0.94 el gasto deja de crecer al subir el tirante; la app lo advierte.

## Cómo leer el panel de resultados

| Estado | Qué significa |
|---|---|
| Tarjeta gris con un icono de información | Aún no se ha calculado |
| Tarjeta roja | Falta un dato o algún valor no es físicamente válido. El mensaje dice cuál |
| Tarjeta azul | Resultado. La magnitud principal va en tamaño grande |
| Tarjeta verde | Advertencia: el número es válido y además hay una condición que conviene revisar |
| Icono de copiar | Copia todas las líneas del resultado al portapapeles |

Girar el teléfono conserva lo escrito y el último resultado.

## Tema

La app sigue el tema claro u oscuro del sistema. Los colores de acento se mantienen en el azul institucional; no toman el color dinámico del dispositivo.

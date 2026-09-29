# Arquitectura

La app separa el cálculo de la interfaz. El paquete `domain` no importa Android ni Compose: se prueba con JUnit en la JVM, sin emulador. La interfaz solo convierte lo que escribe el usuario, llama al dominio y pinta el resultado.

```
domain  →  Resultado<T>  →  ViewModel  →  Pantalla Compose
```

## Dominio

Cada módulo expone una función pura que recibe números en el SI y devuelve `Resultado<T>`:

- `Exito` trae el valor y una lista de advertencias. El cálculo es válido y hay algo que el usuario debería leer.
- `Error` trae un mensaje. Faltan datos o algún valor no tiene sentido físico (diámetro nulo, tirante mayor que el diámetro, carga negativa).

Así la pantalla no tiene que adivinar si un `NaN` vino de una división entre cero.

Los catálogos (`CatalogoMateriales`, `CatalogoMaterialesElasticos`, `CatalogoAccesorios`) son la única fuente de ε, C, n, E y K. Ninguna pantalla declara los suyos.

`Unidades.kt` convierte longitud y caudal en la frontera. El dominio nunca ve milímetros ni litros por segundo.

## Interfaz

Cada calculadora es un archivo en `ui/screens/` con tres piezas:

1. Un `UiState` inmutable con los textos de entrada y el último resultado.
2. Un `ViewModel` que valida, llama al dominio y traduce el resultado a renglones de texto.
3. Un `@Composable` que dibuja el estado y le manda los cambios al ViewModel.

El estado sobrevive a la rotación porque lo retiene el ViewModel.

Los controles compartidos están en `ui/components/`:

| Componente | Papel |
|---|---|
| `CampoNumerico`, `CampoLongitud`, `CampoCaudal` | Entrada con saneo decimal y selector de unidad |
| `SelectorOpcion` | Catálogos (material, geometría) |
| `PantallaCalculo`, `SeccionTarjeta`, `BarraAccion` | Estructura de una pantalla de cálculo |
| `PanelResultados` | Estados vacío, error y éxito, más copiar al portapapeles |

`EntradaLongitud` y `EntradaCaudal` guardan juntos el texto y la unidad, y exponen `valorMetros` o `valorM3s`. El ViewModel solo lee esas propiedades.

## Navegación y tema

Las rutas son objetos `@Serializable` en `navigation/Rutas.kt`. `GrafoNavegacion` las registra con Navigation Compose. No hay cadenas de ruta sueltas.

`MainActivity` envuelve el grafo en `HidroTheme`. El esquema de color es el de `ui/theme/`, claro u oscuro según el sistema, con el azul institucional como primario. El tema XML de `res/values` solo cubre el instante previo a que Compose arranque, para que la ventana no parpadee en blanco.

## Añadir un cálculo

1. Crea la función en `domain/`, devolviendo `Resultado<T>`, y rechaza ahí las entradas que no son físicas.
2. Añade pruebas en `app/src/test/` contra un caso resuelto a mano o contra una fórmula cerrada.
3. Crea el `UiState`, el `ViewModel` y la pantalla siguiendo `CaudalScreen.kt`.
4. Declara una ruta en `Rutas.kt` y regístrala en `GrafoNavegacion.kt`.
5. Enlázala desde el menú que corresponda en `Menus.kt`.

Si el cálculo necesita un coeficiente de material, léelo del catálogo existente en lugar de crear otra tabla.

## Compilación

| Dato | Valor |
|---|---|
| `compileSdk` / `targetSdk` | 35 |
| `minSdk` | 24 |
| JDK | 17 |
| `applicationId` | `com.example.calculotuberias.v2` |
| `versionName` | 2.0 |

El identificador distinto del de la versión 1.0 permite tener las dos instalaciones en el mismo dispositivo.

```bash
./gradlew assembleDebug          # APK de depuración
./gradlew testDebugUnitTest      # pruebas del dominio
```

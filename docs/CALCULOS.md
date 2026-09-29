# Cálculos

Todas las magnitudes internas están en el Sistema Internacional: metros, segundos, m³/s y pascales. La conversión de lo que escribe el usuario ocurre antes de entrar al dominio.

El fluido es agua a 20 °C.

| Símbolo | Nombre | Valor |
|---|---|---|
| g | Gravedad | 9.81 m/s² |
| ρ | Densidad | 998 kg/m³ |
| ν | Viscosidad cinemática | 1.004×10⁻⁶ m²/s |
| K | Módulo de compresibilidad | 2.19×10⁹ Pa |

Están declaradas una sola vez en `domain/Constantes.kt`.

## Factor de fricción

Número de Reynolds:

```
Re = V · D / ν
```

| Régimen | Condición | Factor f |
|---|---|---|
| Laminar | Re < 2300 | 64 / Re |
| Transición | 2300 ≤ Re < 4000 | Interpolación lineal entre f(2300) y Swamee-Jain en Re = 4000 |
| Turbulento | Re ≥ 4000 | Swamee-Jain |

Swamee-Jain, con ε y D en metros:

```
f = 0.25 / [ log10( ε/(3.7·D) + 5.74/Re^0.9 ) ]²
```

En las pruebas, esta aproximación se compara con Colebrook-White resuelta por iteración. La diferencia relativa se mantiene por debajo del 1 % en los casos de tubería lisa, rugosa y límite hidráulicamente liso.

## Darcy-Weisbach

```
A  = π · D² / 4
V  = Q / A
hf = f · (L/D) · V² / (2·g)
```

`f` sale del apartado anterior, así que el método cubre régimen laminar y turbulento. El gradiente hidráulico es `S = hf / L`.

Implementación: `PerdidasFriccion.darcyWeisbach`.

## Hazen-Williams

Forma del SI, con Q en m³/s, D y L en metros:

```
hf = 10.674 · L · (Q/C)^1.852 / D^4.871
```

La app avisa, sin interrumpir el cálculo, cuando D < 0.05 m o cuando la velocidad queda fuera de 0.6 a 3 m/s.

Implementación: `PerdidasFriccion.hazenWilliams`.

## Chézy-Manning en tubo lleno

Con el radio hidráulico `R = D/4`, la pendiente de Manning despejada y multiplicada por L da:

```
hf = 10.2937 · n² · L · Q² / D^(16/3)
```

La constante es `4^(10/3) / π²`. Una prueba comprueba que coincide con evaluar Manning directamente sobre `R = D/4`.

Implementación: `PerdidasFriccion.chezyManning`.

## Flujo por gravedad

Entre dos depósitos, o hacia una descarga libre, la carga disponible se reparte así:

```
H = ( 1 + f·L/D + ΣK ) · V² / (2·g)
```

El `1` es la carga de velocidad en la salida. Despejando:

```
V = sqrt( 2·g·H / (1 + f·L/D + ΣK) )
```

`f` depende de V a través del Reynolds, así que los dos problemas se cierran por iteración.

**Caudal conocido el diámetro.** Se itera sobre `f` hasta que el cambio baja de 1×10⁻¹⁰. El caudal es `Q = A·V`.

**Diámetro conocido el caudal.** Se propone un diámetro, se calcula la velocidad real `Q/A`, de ahí `f`, y de la ecuación de energía la velocidad que esa carga puede sostener. El diámetro se actualiza con

```
D = sqrt( 4·Q / (π·V) )
```

y se detiene cuando el cambio de D baja de 1×10⁻¹⁰. El tope es de 200 iteraciones.

Los dos sentidos usan la misma ecuación. Encadenarlos devuelve el diámetro de partida; hay una prueba que lo exige con tolerancia de 1×10⁻⁶ m.

Implementación: `FlujoGravedad`.

## Pérdidas locales

```
hL = ΣK · V² / (2·g)
```

`ΣK` es la suma de (cantidad × K) de cada accesorio. La longitud equivalente, cuando se conoce `f`, sería `Leq = ΣK · D / f`; el dominio la calcula si se le pasa `f`, y la pantalla de pérdidas locales reporta h_L directamente.

### Coeficientes K

| Accesorio | K |
|---|---|
| Válvula de globo abierta | 10.0 |
| Válvula check totalmente abierta | 2.5 |
| Válvula en ángulo completamente abierta | 5.0 |
| Válvula de compuerta totalmente abierta | 0.2 |
| Válvula de compuerta, ¾ de apertura | 1.5 |
| Válvula de compuerta, ½ de apertura | 5.6 |
| Válvula de compuerta, ¼ de apertura | 24.0 |
| Codo de 90° | 0.9 |
| Codo de 45° | 0.42 |
| Retorno (curva en U) | 2.2 |
| Tee en sentido recto | 0.3 |
| Tee por la salida lateral | 1.8 |
| Ye de 45°, sentido recto | 0.3 |
| Ye de 45°, salida lateral | 0.8 |
| Unión | 0.3 |
| Entrada brusca | 0.8 |
| Entrada gradual | 0.2 |
| Pichancha | 5.5 |
| Salida | 1.0 |

## Celeridad

Allievi-Korteweg, con D y e en metros y E en pascales:

```
a = 1 / sqrt( ρ · ( 1/K + D/(E·e) ) )
```

Es la misma expresión que

```
a = sqrt(K/ρ) / sqrt( 1 + K·D/(E·e) )
```

`sqrt(K/ρ)` es la celeridad en agua libre, el límite cuando la pared no se deforma. Una prueba lo verifica con un módulo de Young muy grande.

Tiempo crítico de la conducción y sobrepresión de Joukowsky para un cierre instantáneo:

```
Tc = 2·L / a
Δh = a · ΔV / g
```

### Módulo de Young

| Material | E (GPa) |
|---|---|
| PVC | 3.3 |
| PEAD | 0.8 |
| PRFV | 15 |
| Asbesto cemento | 24 |
| Concreto | 25 |
| Concreto reforzado | 45 |
| Hierro fundido | 90 |
| Hierro dúctil | 172 |
| Acero | 207 |

## Canales

Manning para flujo uniforme:

```
Q = (1/n) · A · R^(2/3) · sqrt(S)
R = A / P
V = Q / A
E = y + V² / (2·g)
Fr = V / sqrt( g · A/T )
```

`A/T` es la profundidad hidráulica. Si el espejo `T` se anula, Fr se deja sin definir en lugar de forzar un cero.

### Geometría de la sección mojada

Rectangular, de ancho `b`:

```
A = b·y          P = b + 2·y          T = b
```

Triangular, de talud `z` (horizontal:vertical):

```
A = z·y²         P = 2·y·sqrt(1+z²)   T = 2·z·y
```

En esta sección la profundidad hidráulica vale exactamente `y/2`.

Trapezoidal:

```
A = y·(b + z·y)  P = b + 2·y·sqrt(1+z²)   T = b + 2·z·y
```

Circular de diámetro `D`, con el ángulo central en radianes:

```
θ = 2 · arccos( 1 − 2·y/D )
A = (D²/8) · (θ − sin θ)
P = (D/2) · θ
T = D · sin(θ/2)
```

Se exige `0 < y ≤ D`. El gasto de una sección circular alcanza su máximo cerca de `y/D = 0.938` y después disminuye; una prueba lo comprueba.

### Tirante crítico

Se resuelve por bisección la condición de energía mínima, válida para cualquier sección:

```
Q² · T(y) / ( g · A(y)³ ) − 1 = 0
```

En sección rectangular el resultado se contrasta con la fórmula cerrada `yc = (q²/g)^(1/3)`, donde `q = Q/b`.

### Régimen

| Froude | Régimen |
|---|---|
| Fr < 0.995 | Subcrítico |
| 0.995 ≤ Fr ≤ 1.005 | Crítico |
| Fr > 1.005 | Supercrítico |

## Catálogo de tuberías

Una sola tabla alimenta los tres métodos de fricción. La rugosidad se guarda en milímetros y se pasa a metros al calcular.

| Material | ε (mm) | C | n |
|---|---|---|---|
| PVC | 0.0015 | 150 | 0.009 |
| Polietileno de alta densidad | 0.001524 | 150 | 0.009 |
| Policloruro de vinilo clorado | 0.0015 | 150 | 0.009 |
| Cobre | 0.0015 | 135 | 0.010 |
| Latón | 0.0015 | 135 | 0.011 |
| Plomo | 0.0015 | 135 | 0.011 |
| Vidrio | 0.0015 | 140 | 0.011 |
| Acero esmaltado | 0.0048 | 148 | 0.010 |
| Asbesto cemento | 0.03 | 147 | 0.011 |
| Acero nuevo | 0.045 | 145 | 0.011 |
| Hierro fundido asfaltado | 0.12 | 148 | 0.012 |
| Acero galvanizado | 0.15 | 120 | 0.016 |
| Hierro fundido | 0.15 | 130 | 0.012 |
| Hierro galvanizado | 0.15 | 133 | 0.016 |
| Madera (duelas) | 0.18 | 120 | 0.012 |
| Concreto (cimbra metálica) | 0.18 | 140 | 0.013 |
| Hierro dúctil | 0.25 | 135 | 0.022 |
| Concreto simple | 0.36 | 135 | 0.013 |
| Concreto (cimbra madera) | 0.6 | 120 | 0.015 |
| Acero remachado | 0.9 | 110 | 0.010 |

Los coeficientes son los de las tablas del proyecto académico. Dos valores de n se apartan de los que citan la mayoría de los manuales: **0.022 para hierro dúctil** y **0.010 para acero remachado**. Se conservan para que un cálculo hecho con estas tablas se pueda reproducir; conviene tenerlo presente al comparar con otra fuente.

## Unidades de entrada

| Magnitud | Unidades | Factor hacia el SI |
|---|---|---|
| Longitud | mm, cm, m, in, ft | 0.001, 0.01, 1, 0.0254, 0.3048 |
| Caudal | L/s, m³/s, m³/h, gal/min | 0.001, 1, 1/3600, 6.30902×10⁻⁵ |

## Ejemplo de comprobación

Datos: PVC (ε = 0.0015 mm), L = 1000 m, D = 300 mm, H = 20 m, ΣK = 2.

| Magnitud | Valor |
|---|---|
| Caudal | 214.185 L/s |
| Velocidad | 3.0301 m/s |
| Reynolds | 9.054×10⁵ |
| f | 0.01192 |
| Pérdida por fricción | 18.5961 m |
| Pérdidas locales | 0.9359 m |
| Carga de velocidad | 0.4680 m |

La suma de las tres últimas es 20.000 m, que es H. Alimentar el módulo de diámetro con 214.185 L/s y el resto de datos iguales devuelve D = 0.3000 m.

Segundo caso, pérdidas por fricción: L = 100 m, D = 200 mm, Q = 50 L/s, PVC.

| Método | hf |
|---|---|
| Darcy-Weisbach | 0.9242 m |
| Hazen-Williams | 0.9849 m (+6.6 %) |
| Chézy-Manning | 1.1139 m (+20.5 %) |

## Dónde está cada cosa

| Cálculo | Archivo |
|---|---|
| Constantes | `domain/Constantes.kt` |
| Fricción y régimen | `domain/Friccion.kt` |
| Los tres métodos de hf | `domain/PerdidasFriccion.kt` |
| Caudal y diámetro | `domain/FlujoGravedad.kt` |
| Pérdidas locales y accesorios | `domain/PerdidasLocales.kt` |
| Celeridad | `domain/Celeridad.kt` |
| Canales | `domain/Canales.kt` |
| Catálogos | `domain/MaterialTuberia.kt` |
| Pruebas | `app/src/test/java/.../domain/` |

Para ejecutarlas:

```bash
./gradlew testDebugUnitTest
```

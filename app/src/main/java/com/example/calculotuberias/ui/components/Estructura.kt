package com.example.calculotuberias.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Armazón común de todas las pantallas de cálculo.
 *
 * Sustituye al patrón anterior de `Box` + imagen de fondo a pantalla completa
 * repetido en cada archivo, que además impedía la navegación hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCalculo(
    titulo: String,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    accionInferior: (@Composable () -> Unit)? = null,
    contenido: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        subtitulo?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                navigationIcon = {
                    onVolver?.let {
                        IconButton(onClick = it) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            // Con enableEdgeToEdge el contenido se dibuja detrás de los botones
            // o la barra de gestos del sistema. El padding se aplica sólo cuando
            // hay botón de calcular; si no, el Scaffold ya reserva ese espacio
            // en innerPadding y no conviene duplicarlo.
            if (accionInferior != null) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    accionInferior()
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        content = contenido,
    )
}

/** Columna con scroll y separación homogénea para el cuerpo de una pantalla. */
@Composable
fun CuerpoDesplazable(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    contenido: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            contenido()
            Spacer(Modifier.height(8.dp))
        },
    )
}

/** Tarjeta de sección con encabezado e icono. */
@Composable
fun SeccionTarjeta(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    contenido: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.size(8.dp))
                Text(titulo, style = MaterialTheme.typography.titleMedium)
            }
            descripcion?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            contenido()
        }
    }
}

/** Botón de acción principal anclado al pie de la pantalla. */
@Composable
fun BarraAccion(
    texto: String,
    icono: ImageVector,
    habilitado: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
    ) {
        Button(
            onClick = onClick,
            enabled = habilitado,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text(texto, style = MaterialTheme.typography.titleMedium)
        }
    }
}

/** Lista de navegación en tarjetas, usada en las pantallas de menú. */
@Composable
fun ListaOpciones(
    opciones: List<OpcionMenu>,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(opciones.size) { indice ->
            TarjetaOpcion(opciones[indice])
        }
    }
}

data class OpcionMenu(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaOpcion(opcion: OpcionMenu) {
    Card(
        onClick = opcion.onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                opcion.icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    opcion.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    opcion.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

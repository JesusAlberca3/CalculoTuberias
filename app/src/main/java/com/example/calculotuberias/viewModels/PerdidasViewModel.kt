package com.example.calculotuberias.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculotuberias.models.PerdidaItem

class PerdidasViewModel : ViewModel() {

    // Lista base de pares (nombre, valor)
    private val accesorios = listOf(
        "Válvula de globo abierta" to 10.0,
        "Válvula check totalmente abierta" to 2.5,
        "Codo de 90°" to 0.9,
        "Codo de 45°" to 0.42,
        "Válvula de compuerta totalmente abierta" to 0.2,
        "Válvula de compuerta con ¾ de apertura" to 1.5,
        "Válvula de compuerta con ½ de apertura" to 5.6,
        "Válvula de compuerta con ¼ de apertura" to 24.0,
        "Pérdida por entrada brusca" to 0.8,
        "Pérdida por entrada gradual" to 0.2,
        "Pérdida por pichancha" to 5.5,
        "Válvula en ángulo completamente abierta" to 5.0,
        "Retorno (curva en U)" to 2.2,
        "Tee en sentido recto" to 0.3,
        "Tee a través de la salida lateral" to 1.8,
        "Unión" to 0.3,
        "Ye de 45°, en sentido recto" to 0.3,
        "Ye de 45°, en salida lateral" to 0.8,
        "Salida" to 1.0
    )


    private val _accesoriosItems = MutableLiveData<List<PerdidaItem>>(
        accesorios.map { (nombre, valor) ->
            PerdidaItem(nombre = nombre, cantidad = 0, caudal = valor)
        }
    )
    val accesoriosItems: LiveData<List<PerdidaItem>> = _accesoriosItems

    fun addCantidad(item: PerdidaItem) {
        val currentList = _accesoriosItems.value ?: return
        _accesoriosItems.value = currentList.map {
            if (it.nombre == item.nombre) it.copy(cantidad = it.cantidad + 1) else it
        }
    }

    fun removeCantidad(item: PerdidaItem) {
        val currentList = _accesoriosItems.value ?: return
        _accesoriosItems.value = currentList.map {
            if (it.nombre == item.nombre && it.cantidad > 0) it.copy(cantidad = it.cantidad - 1) else it
        }
    }

    fun calcularTotal(): Double {
        val list = _accesoriosItems.value ?: emptyList()
        return list.sumOf { it.cantidad * it.caudal.toDouble() }
    }


}
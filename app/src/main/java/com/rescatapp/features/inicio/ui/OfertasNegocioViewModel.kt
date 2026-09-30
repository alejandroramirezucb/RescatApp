package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class OfertasNegocioViewModel @Inject constructor(repositorioOfertas: RepositorioOfertas) :
    ViewModel() {
    val ofertas: StateFlow<List<Oferta>> = repositorioOfertas.ofertas
}

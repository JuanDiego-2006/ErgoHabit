package com.knexus.ergohabit.features.progreso.domain.repositories

import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.Frase
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import kotlinx.coroutines.flow.Flow

interface ProgresoRepository {
    fun getHabitosProgreso(idUsuario: Int): Flow<Result<List<HabitoProgreso>>>
    fun getTendenciaGeneral(idUsuario: Int): Flow<Result<Pair<String, List<ProgresoDia>>>>
    fun getDetalleHabito(idUsuario: Int, idHabito: Int): Flow<Result<DetalleHabito>>
    fun getFraseAleatoria(): Flow<Result<Frase>>
}

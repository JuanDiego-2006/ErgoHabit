package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.abs

class PosturaUseCase @Inject constructor(
    private val repositorio: PostureRepository
) {
    operator fun invoke(): Flow<EntidadPostura> {
        return repositorio.getPostureData().map { entidad ->
            val gradosMalaPostura = abs(90.0 - abs(entidad.anguloPitch))

            // Límite ajustado a 25.0 grados
            val esCorrecta = gradosMalaPostura <= 25.0
            val mensajeAlerta = when {
                gradosMalaPostura <= 25.0 -> "Postura Correcta"
                gradosMalaPostura <= 40.0 -> "¡Eleva tu teléfono!"
                else                      -> "¡Corrige tu postura!"
            }

            entidad.copy(
                esCorrecta = esCorrecta,
                mensaje    = mensajeAlerta
            )
        }
    }
}
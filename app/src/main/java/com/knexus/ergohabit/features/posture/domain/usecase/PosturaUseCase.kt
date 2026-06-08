package com.knexus.ergohabit.features.posture.domain.usecase

import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para analizar la postura. Aquí reside la lógica de negocio.
 */
class PosturaUseCase @Inject constructor(
    private val repositorio: PostureRepository
) {
    /**
     * Ejecuta el análisis de postura validando los ángulos.
     */
    operator fun invoke(): Flow<EntidadPostura> {
        return repositorio.getPostureData().map { entidad ->
            // Lógica de negocio en español: Validar ángulos entre -15 y 15 grados
            val esCorrecta = entidad.anguloPitch in -15.0..15.0 && entidad.anguloRoll in -15.0..15.0
            val mensajeAlerta = if (esCorrecta) "Postura Correcta" else "¡Corrige tu postura!"
            
            entidad.copy(
                esCorrecta = esCorrecta,
                mensaje = mensajeAlerta
            )
        }
    }
}

package com.knexus.ergohabit.features.posture.data.mapper

import com.knexus.ergohabit.core.hardware.domain.model.DatosSensor
import com.knexus.ergohabit.features.posture.data.models.PosturaDto
import com.knexus.ergohabit.features.posture.domain.entities.EntidadPostura

/**
 * Mapeador para convertir datos entre la capa de datos y dominio.
 */
class PostureMapper {
    /**
     * Convierte los datos del sensor a una entidad de postura.
     */
    fun mapearAEntidad(datosSensor: DatosSensor): EntidadPostura {
        return EntidadPostura(
            anguloPitch = datosSensor.inclinacion,
            anguloRoll = datosSensor.balanceo,
            esCorrecta = true,
            mensaje = "",
            conteoVibraciones = 0
        )
    }

    /**
     * Convierte la entidad de postura a un DTO para el servidor.
     */
    fun mapearADto(entidad: EntidadPostura): PosturaDto {
        return PosturaDto(
            inclinacion = entidad.anguloPitch,
            balanceo = entidad.anguloRoll,
            esCorrecta = entidad.esCorrecta,
            fechaRegistro = System.currentTimeMillis(),
            conteoVibraciones = entidad.conteoVibraciones
        )
    }
}

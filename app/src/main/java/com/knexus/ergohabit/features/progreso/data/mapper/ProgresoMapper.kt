package com.knexus.ergohabit.features.progreso.data.mapper

import com.knexus.ergohabit.features.progreso.data.models.DetalleHabitoDto
import com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoDto
import com.knexus.ergohabit.features.progreso.data.models.ProgresoDiaDto
import com.knexus.ergohabit.features.progreso.data.models.RegistroHabitoDto
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito

fun HabitoProgresoDto.toDomain(): HabitoProgreso {
    return HabitoProgreso(
        id = this.id,
        nombre = this.nombre,
        icono = this.icono,
        colorHex = this.colorHex,
        porcentaje = this.porcentaje
    )
}

fun ProgresoDiaDto.toDomain(): ProgresoDia {
    return ProgresoDia(
        dia = this.dia,
        valor = this.valor
    )
}

fun DetalleHabitoDto.toDomain(): DetalleHabito {
    return DetalleHabito(
        idHabito = this.idHabito,
        titulo = this.titulo,
        metaValor = this.metaValor,
        leyendaPositiva = this.leyendaPositiva,
        leyendaNegativa = this.leyendaNegativa,
        registros = this.registros.map { it.toDomain() }
    )
}

fun RegistroHabitoDto.toDomain(): RegistroHabito {
    return RegistroHabito(
        etiqueta = this.etiqueta,
        valor = this.valor,
        esMetaCumplida = this.esMetaCumplida
    )
}

package com.knexus.ergohabit.features.progreso.data.mapper

import com.knexus.ergohabit.core.database.entities.FraseEntity
import com.knexus.ergohabit.core.database.entities.HabitoProgresoEntity
import com.knexus.ergohabit.core.database.entities.RegistroSemanalEntity
import com.knexus.ergohabit.features.progreso.data.models.DetalleHabitoDto
import com.knexus.ergohabit.features.progreso.data.models.HabitoProgresoDto
import com.knexus.ergohabit.features.progreso.data.models.ProgresoDiaDto
import com.knexus.ergohabit.features.progreso.data.models.RegistroHabitoDto
import com.knexus.ergohabit.features.progreso.domain.entities.DetalleHabito
import com.knexus.ergohabit.features.progreso.domain.entities.Frase
import com.knexus.ergohabit.features.progreso.domain.entities.HabitoProgreso
import com.knexus.ergohabit.features.progreso.domain.entities.ProgresoDia
import com.knexus.ergohabit.features.progreso.domain.entities.RegistroHabito

// --- DTO to DOMAIN ---

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

// --- DOMAIN to ENTITY ---

fun HabitoProgreso.toEntity(
    titulo: String? = null,
    leyendaPositiva: String? = null,
    leyendaNegativa: String? = null
): HabitoProgresoEntity {
    return HabitoProgresoEntity(
        id = this.id,
        nombre = this.nombre,
        icono = this.icono,
        colorHex = this.colorHex,
        porcentaje = this.porcentaje,
        tituloSeccion = titulo,
        leyendaPositiva = leyendaPositiva,
        leyendaNegativa = leyendaNegativa
    )
}

fun RegistroHabito.toEntity(idHabito: Int): RegistroSemanalEntity {
    return RegistroSemanalEntity(
        idHabito = idHabito,
        etiqueta = this.etiqueta,
        valor = this.valor,
        esMetaCumplida = this.esMetaCumplida
    )
}

fun Frase.toEntity(): FraseEntity {
    return FraseEntity(
        id = this.id,
        categoria = this.categoria,
        texto = this.texto
    )
}

// --- ENTITY to DOMAIN ---

fun HabitoProgresoEntity.toDomain(): HabitoProgreso {
    return HabitoProgreso(
        id = this.id,
        nombre = this.nombre,
        icono = this.icono,
        colorHex = this.colorHex,
        porcentaje = this.porcentaje
    )
}

fun RegistroSemanalEntity.toDomain(): RegistroHabito {
    return RegistroHabito(
        etiqueta = this.etiqueta,
        valor = this.valor,
        esMetaCumplida = this.esMetaCumplida
    )
}

fun FraseEntity.toDomain(): Frase {
    return Frase(
        id = this.id,
        categoria = this.categoria,
        texto = this.texto
    )
}

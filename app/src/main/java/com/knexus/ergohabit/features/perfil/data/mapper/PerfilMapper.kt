package com.knexus.ergohabit.features.perfil.data.mapper

import com.knexus.ergohabit.core.database.entities.UsuarioPerfilEntity
import com.knexus.ergohabit.features.perfil.data.models.PerfilDto
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil

fun PerfilDto.toDomain(): UsuarioPerfil {
    return UsuarioPerfil(
        id = this.id,
        nombre = this.nombre,
        primerApellido = this.primerApellido,
        segundoApellido = this.segundoApellido,
        correo = this.email,
        idRol = this.idRol ?: 0,
        peso = this.peso,
        estatura = this.estatura,
        fotoUrl = this.fotoUrl
    )
}

fun UsuarioPerfil.toEntity(): UsuarioPerfilEntity {
    return UsuarioPerfilEntity(
        id = this.id,
        nombre = this.nombre,
        primerApellido = this.primerApellido,
        segundoApellido = this.segundoApellido,
        correo = this.correo,
        idRol = this.idRol,
        peso = this.peso,
        estatura = this.estatura,
        fotoUrl = this.fotoUrl
    )
}

fun UsuarioPerfilEntity.toDomain(): UsuarioPerfil {
    return UsuarioPerfil(
        id = this.id,
        nombre = this.nombre,
        primerApellido = this.primerApellido,
        segundoApellido = this.segundoApellido,
        correo = this.correo,
        idRol = this.idRol,
        peso = this.peso,
        estatura = this.estatura,
        fotoUrl = this.fotoUrl
    )
}

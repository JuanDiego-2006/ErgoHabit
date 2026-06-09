package com.knexus.ergohabit.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRuta {

    @Serializable
    data object Login : NavRuta()

    @Serializable
    data object Inicio : NavRuta()

    @Serializable
    data object Tareas : NavRuta()

    @Serializable
    data object Sensores : NavRuta()

    @Serializable
    data object Progreso : NavRuta()

    @Serializable
    data object Perfil : NavRuta()
}
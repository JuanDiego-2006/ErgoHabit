package com.knexus.ergohabit.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRuta {
    @Serializable data object Login : NavRuta()
    @Serializable data object Register : NavRuta()
    @Serializable data object Inicio : NavRuta()
    @Serializable data object Hidratacion : NavRuta()
    @Serializable data object Sueno : NavRuta()
    @Serializable data object ConfigHorario : NavRuta()
    @Serializable data object RetrasoSueno : NavRuta()
    @Serializable data object Actividad : NavRuta()
    @Serializable data object ConfigMeta : NavRuta()
    @Serializable data object Nutricion : NavRuta()
    @Serializable data object ConfigNutricion : NavRuta()
    @Serializable data class Tareas(val mostrarCompletado: Boolean = false) : NavRuta()
    @Serializable data object Progreso : NavRuta()
    @Serializable data object Perfil : NavRuta()
}
package com.knexus.ergohabit.core.di

import com.knexus.ergohabit.core.hardware.data.AndroidGestorSonido
import com.knexus.ergohabit.core.hardware.data.SensorPasosAndroid
import com.knexus.ergohabit.core.hardware.data.SensorPosturaAndroid
import com.knexus.ergohabit.core.hardware.domain.GestorSonido
import com.knexus.ergohabit.core.hardware.domain.SensorEjercicio
import com.knexus.ergohabit.core.hardware.domain.SensorPostura
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindGestorSonido(
        androidGestorSonido: AndroidGestorSonido
    ): GestorSonido

    @Binds
    @Singleton
    abstract fun bindSensorPostura(
        sensorAndroid: SensorPosturaAndroid   // ← REAL en lugar de Simulado
    ): SensorPostura

    @Binds
    @Singleton
    abstract fun bindSensorEjercicio(
        sensorPasosAndroid: SensorPasosAndroid
    ): SensorEjercicio
}
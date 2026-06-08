package com.knexus.ergohabit.core.di

import com.knexus.ergohabit.core.hardware.data.AndroidGestorSonido
import com.knexus.ergohabit.core.hardware.data.SensorPosturaAndroid
import com.knexus.ergohabit.core.hardware.data.SensorPosturaSimulado
import com.knexus.ergohabit.core.hardware.domain.GestorSonido
import com.knexus.ergohabit.core.hardware.domain.SensorPostura
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer las implementaciones de hardware.
 * Puedes alternar entre SensorPosturaAndroid (Real) y SensorPosturaSimulado (Pruebas).
 */
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
        sensorSimulado: SensorPosturaSimulado // Cambia esto a SensorPosturaAndroid para usar el real
    ): SensorPostura
}

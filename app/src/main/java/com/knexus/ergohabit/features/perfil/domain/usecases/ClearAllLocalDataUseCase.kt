package com.knexus.ergohabit.features.perfil.domain.usecases

import com.knexus.ergohabit.core.database.dao.AguaDao
import com.knexus.ergohabit.core.database.dao.ProgresoDao
import com.knexus.ergohabit.core.database.dao.TareaDao
import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import javax.inject.Inject

class ClearAllLocalDataUseCase @Inject constructor(
    private val perfilRepository: PerfilRepository,
    private val tareaDao: TareaDao,
    private val tareaProgresoDao: TareaProgresoDao,
    private val progresoDao: ProgresoDao,
    private val aguaDao: AguaDao
) {
    suspend operator fun invoke() {
        perfilRepository.clearLocalProfile()
        tareaDao.clearTareas()
        tareaProgresoDao.clearAll()
        progresoDao.clearFrase()
        aguaDao.clearDashboard()
    }
}

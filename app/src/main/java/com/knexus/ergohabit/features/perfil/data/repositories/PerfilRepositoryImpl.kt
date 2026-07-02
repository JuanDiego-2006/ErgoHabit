package com.knexus.ergohabit.features.perfil.data.repositories

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.knexus.ergohabit.core.network.ApiErrorParser
import com.knexus.ergohabit.features.perfil.data.datasource.api.PerfilApi
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PerfilRepositoryImpl @Inject constructor(
    private val api: PerfilApi,
    @ApplicationContext private val context: Context
) : PerfilRepository {

    override fun getPerfil(idUsuario: Int): Flow<Result<UsuarioPerfil>> = flow {
        try {
            val perfil = api.getPerfil(idUsuario)
            emit(Result.success(perfil))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun updateFotoPerfil(idUsuario: Int, fotoUri: String): Result<String> {
        return try {
            val uri = Uri.parse(fotoUri)
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return Result.failure(Exception("No se pudo abrir la imagen seleccionada."))

            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                "file",
                "foto_perfil.$extension",
                requestBody
            )

            val response = api.updateFotoPerfil(part)
            Result.success(response.url)
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.mensaje(e)))
        }
    }

    override suspend fun updatePerfil(perfil: UsuarioPerfil): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Actualización de perfil pendiente de implementar"))
    }
}

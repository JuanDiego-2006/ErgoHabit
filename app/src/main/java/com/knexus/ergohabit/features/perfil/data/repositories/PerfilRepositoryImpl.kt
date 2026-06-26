package com.knexus.ergohabit.features.perfil.data.repositories

import android.content.Context
import android.net.Uri
import com.knexus.ergohabit.core.database.dao.UsuarioPerfilDao
import com.knexus.ergohabit.features.perfil.data.datasource.api.PerfilApi
import com.knexus.ergohabit.features.perfil.data.mapper.toDomain
import com.knexus.ergohabit.features.perfil.data.mapper.toEntity
import com.knexus.ergohabit.features.perfil.data.models.UpdatePerfilRequestDto
import com.knexus.ergohabit.features.perfil.domain.entities.UsuarioPerfil
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PerfilRepositoryImpl @Inject constructor(
    private val api: PerfilApi,
    private val dao: UsuarioPerfilDao,
    @ApplicationContext private val context: Context
) : PerfilRepository {

    override fun getPerfil(idUsuario: Int): Flow<Result<UsuarioPerfil>> = flow {

        val local = dao.getPerfil(idUsuario).first()
        if (local != null) {
            emit(Result.success(local.toDomain()))
        }

        try {

            val response = api.getPerfil(idUsuario)
            val perfil = response.toDomain()
            

            dao.insertPerfil(perfil.toEntity())
            
            emit(Result.success(perfil))
        } catch (e: Exception) {
            if (local == null) emit(Result.failure(e))
        }
    }

    override suspend fun updateFotoPerfil(idUsuario: Int, fotoUri: String): Result<String> {
        return try {
            val uri = Uri.parse(fotoUri)
            val file = uriToFile(uri, context) ?: throw Exception("No se pudo procesar la imagen")
            
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", file.name, requestFile)
            
            val response = api.uploadFoto(body)
            
            val local = dao.getPerfil(idUsuario).first()
            if (local != null) {
                dao.insertPerfil(local.copy(fotoUrl = response.url))
            }
            
            Result.success(response.url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePerfil(idUsuario: Int, perfil: UsuarioPerfil): Result<String> {
        return try {
            val request = UpdatePerfilRequestDto(
                nombre = perfil.nombre,
                primerApellido = perfil.primerApellido,
                segundoApellido = perfil.segundoApellido,
                email = perfil.correo,
                peso = perfil.peso,
                estatura = perfil.estatura
            )
            
            val response = api.updatePerfil(idUsuario, request)
            val updatedDomain = response.toDomain()
            
            dao.insertPerfil(updatedDomain.toEntity())
            
            Result.success("Información actualizada con éxito")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_profile_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }
}

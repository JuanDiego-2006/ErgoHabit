package com.knexus.ergohabit.features.posture.presentation.components

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.knexus.ergohabit.core.hardware.domain.EvaluadorPosturaRostro
import com.knexus.ergohabit.core.hardware.domain.model.ResultadoPosturaCamara
import java.util.concurrent.Executors

/**
 * Analiza postura con la cámara frontal (solo rostro, sin vista previa).
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CamaraPosturaMonitor(
    activo: Boolean,
    tienePermisoCamara: Boolean,
    lifecycleOwner: LifecycleOwner,
    onResultado: (ResultadoPosturaCamara) -> Unit,
    onCamaraInactiva: () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(activo, tienePermisoCamara, lifecycleOwner) {
        if (!activo || !tienePermisoCamara) {
            onCamaraInactiva()
            onDispose { }
            return@DisposableEffect onDispose { }
        }

        val executor = Executors.newSingleThreadExecutor()
        val faceDetector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setMinFaceSize(0.12f)
                .enableTracking()
                .build()
        )
        var cameraProvider: ProcessCameraProvider? = null
        var ultimoAnalisis = 0L

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    val ahora = System.currentTimeMillis()
                    if (ahora - ultimoAnalisis < 400) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    ultimoAnalisis = ahora

                    @OptIn(ExperimentalGetImage::class)
                    val mediaImage = imageProxy.image
                    if (mediaImage == null) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    faceDetector.process(inputImage)
                        .addOnSuccessListener { faces ->
                            val resultado = if (faces.isEmpty()) {
                                EvaluadorPosturaRostro.sinRostro()
                            } else {
                                val principal = faces.maxBy { it.boundingBox.width() * it.boundingBox.height() }
                                EvaluadorPosturaRostro.evaluar(principal)
                            }
                            onResultado(resultado)
                        }
                        .addOnFailureListener {
                            onResultado(EvaluadorPosturaRostro.sinRostro())
                        }
                        .addOnCompleteListener { imageProxy.close() }
                }

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    imageAnalysis
                )
            } catch (_: Exception) {
                onResultado(
                    ResultadoPosturaCamara(
                        personaDetectada = false,
                        esCorrecta = true,
                        motivo = "Cámara no disponible"
                    )
                )
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProvider?.unbindAll()
            executor.shutdown()
            faceDetector.close()
            onCamaraInactiva()
        }
    }
}

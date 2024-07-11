package com.github.comismoy.scanx

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.github.comismoy.scanx.interfaces.IScanner
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.delay
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.*
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

actual class ScannerFactory {
    actual fun createScanner(): IScanner = IOSScanner()
}
@OptIn(ExperimentalForeignApi::class)
class IOSScanner : NSObject(), IScanner, AVCaptureMetadataOutputObjectsDelegateProtocol {
    private val captureSession = AVCaptureSession()
    private val metadataOutput = AVCaptureMetadataOutput()
    private var resultCallback: ((String) -> Unit)? = null

    init {
        val videoCaptureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        val videoInput = videoCaptureDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, error = null) } as AVCaptureDeviceInput
        if (captureSession.canAddInput(videoInput)) {
            captureSession.addInput(videoInput)
        }

        if (captureSession.canAddOutput(metadataOutput)) {
            captureSession.addOutput(metadataOutput)
            metadataOutput.setMetadataObjectsDelegate(this, dispatch_get_main_queue())
            metadataOutput.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)
        }
    }

    override fun startScanning() {
        captureSession.startRunning()
    }

    override fun stopScanning() {
        captureSession.stopRunning()
    }

    override fun onResult(callback: (String) -> Unit) {
        resultCallback = callback
    }

    @ObjCAction
    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection
    ) {
        if (didOutputMetadataObjects.isNotEmpty()) {
            val metadataObject = didOutputMetadataObjects.first() as? AVMetadataMachineReadableCodeObject
            val result = metadataObject?.stringValue
            result?.let { resultCallback?.invoke(it) }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
fun IOSScannerView(scanner: IScanner) {
    var isScanning by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(30000)  // Pausar el escaneo después de 30 segundos
        isScanning = false
        scanner.stopScanning()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isScanning,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScannerFrameView(scanner)
        }

        if (!isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        isScanning = true
                        scanner.startScanning()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Toca para escanear",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
fun ScannerFrameView(scanner: IScanner) {
    UIKitView(
        factory = {
            val view = UIView()
            // Configura la vista para el escáner aquí
            view
        },
        update = {
            scanner.startScanning()
        },
        modifier = Modifier.fillMaxSize()
    )
}


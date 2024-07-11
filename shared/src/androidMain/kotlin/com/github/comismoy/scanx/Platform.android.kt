package com.github.comismoy.scanx

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.comismoy.scanx.interfaces.IScanner
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.delay


actual class ScannerFactory {
    actual fun createScanner(): IScanner = AndroidScanner()
}
class AndroidScanner : IScanner {
    private var barcodeView: CompoundBarcodeView? = null
    private var resultCallback: ((String) -> Unit)? = null

    override fun startScanning() {
        barcodeView?.resume()
    }

    override fun stopScanning() {
        barcodeView?.pause()
    }

    override fun onResult(callback: (String) -> Unit) {
        resultCallback = callback
        barcodeView?.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let { resultCallback?.invoke(it) }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
    }

    fun initialize(barcodeView: CompoundBarcodeView) {
        this.barcodeView = barcodeView
    }
}

@Composable
fun AndroidScannerView(scanner: IScanner) {
    val activity = LocalContext.current as Activity
    val barcodeView = remember { CompoundBarcodeView(activity) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
            }
            else -> {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
    }

    DisposableEffect(Unit) {
        if (hasCameraPermission) {
            scanner.startScanning()
        }

        val listener = { _: Int, permissions: Array<String>, grantResults: IntArray ->
            if (permissions.contains(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasCameraPermission = true
            }
        }

        onDispose {
            scanner.stopScanning()
        }
    }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            delay(30000)  // Pausar el escaneo después de 30 segundos
            isScanning = false
            scanner.stopScanning()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isScanning,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AndroidView(
                factory = { barcodeView }) {
                (scanner as AndroidScanner).initialize(it)
                scanner.startScanning()
            }
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
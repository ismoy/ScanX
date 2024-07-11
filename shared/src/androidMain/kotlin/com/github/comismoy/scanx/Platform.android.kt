package com.github.comismoy.scanx
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.comismoy.scanx.interfaces.IScanner
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.delay


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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

/*@Composable
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
}*/
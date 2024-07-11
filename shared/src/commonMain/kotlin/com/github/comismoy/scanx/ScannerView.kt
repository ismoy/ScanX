package com.github.comismoy.scanx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.comismoy.scanx.interfaces.IScanner

@Composable
fun ScannerView(scanner: IScanner) {
    // Aquí puedes definir la UI común utilizando Jetpack Compose
    // La lógica de la UI puede ser común para Android e iOS

    // Llama a startScanning cuando la UI se inicializa
    DisposableEffect(Unit) {
        scanner.startScanning()
        onDispose { scanner.stopScanning() }
    }

    // Aquí puedes agregar componentes de Compose como botones, textos, etc.
    // que sean comunes para ambas plataformas
}

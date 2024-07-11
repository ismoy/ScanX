package com.github.comismoy.scanx.interfaces

interface IScanner {
    fun startScanning()
    fun stopScanning()
    fun onResult(callback:(String) -> Unit)
}
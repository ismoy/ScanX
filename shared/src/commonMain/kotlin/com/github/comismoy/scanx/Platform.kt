package com.github.comismoy.scanx

import com.github.comismoy.scanx.interfaces.IScanner


expect class ScannerFactory{
    fun createScanner():IScanner
}
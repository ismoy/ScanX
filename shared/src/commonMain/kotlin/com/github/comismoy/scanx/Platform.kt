package com.github.comismoy.scanx

import com.github.comismoy.scanx.interfaces.IScanner


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ScannerFactory{
    fun createScanner():IScanner
}
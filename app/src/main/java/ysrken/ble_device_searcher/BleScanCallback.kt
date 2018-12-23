package ysrken.ble_device_searcher

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

import java.util.Observable

import io.reactivex.ObservableEmitter

class BleScanCallback : ScanCallback() {

    /**
     * requestLocationFeatureに対するEmitter
     */
    private lateinit var mScanResultEmitter: ObservableEmitter<ScanResult>

    fun setScanResultEmitter(emitter: ObservableEmitter<ScanResult>){
        mScanResultEmitter = emitter
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        mScanResultEmitter.onNext(result)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        mScanResultEmitter.onError(RuntimeException("" + errorCode))
    }
}

package ysrken.ble_device_searcher

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import io.reactivex.ObservableEmitter
import android.bluetooth.BluetoothProfile


class BleGattCallback : BluetoothGattCallback() {
    /**
     * onConnectionStateChangeなどに対するEmitter
     */
    private lateinit var mBluetoothGattEmitter: ObservableEmitter<BluetoothGatt>

    /**
     * onReadRemoteRssiに対するEmitter
     */
    private lateinit var mIntEmitter: ObservableEmitter<Int>


    fun setBluetoothGatttEmitter(emitter: ObservableEmitter<BluetoothGatt>) {
        mBluetoothGattEmitter = emitter
    }

    fun setIntEmitter(emitter: ObservableEmitter<Int>) {
        mIntEmitter = emitter
    }

    /**
     * GATTクライアントから接続/切断された際の処理
     *
     * @param gatt GATTクライアント
     * @param status 処理が成功すると [BluetoothGatt.GATT_SUCCESS] になる
     * @param newState 新しい接続の状態。 [BluetoothProfile.STATE_DISCONNECTED] または [BluetoothProfile.STATE_CONNECTED]
     */
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        // 成功時じゃなければ無視する
        if (status != BluetoothGatt.GATT_SUCCESS)
            return

        /**
         * 条件分岐
         */
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            mBluetoothGattEmitter.onNext(gatt)
            mBluetoothGattEmitter.onComplete()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            gatt.connect()
            mBluetoothGattEmitter.onNext(gatt)
            mBluetoothGattEmitter.onComplete()
        }
    }

    /**
     * RSSI値を取得する
     * @param gatt GATTクライアント
     * @param rssi RSSI値
     * @param status 処理が成功すると [BluetoothGatt.GATT_SUCCESS] になる
     */
    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)

        // 成功時じゃなければ無視する
        if (status != BluetoothGatt.GATT_SUCCESS)
            return

        // 流し込み
        mIntEmitter.onNext(rssi)
        mIntEmitter.onComplete()
    }
}

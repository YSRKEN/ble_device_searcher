package ysrken.ble_device_searcher

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.PrintWriter
import java.io.StringWriter


class MainActivity : AppCompatActivity() {
    private val TAG: String = this.javaClass.simpleName

    /**
     * Bluetooth機能を有効化する際のリクエストコード
     */
    private val BLUETOOTH_REQUEST_CODE = 1

    /**
     * 位置情報機能を有効化する際のリクエストコード
     */
    private val LOCATION_REQUEST_CODE = 2

    /**
     * Bluetooth通信を行うためのアダプタ-
     */
    private lateinit var mBluetoothAdapter: BluetoothAdapter

    /**
     * requestBluetoothFeatureに対するEmitter
     */
    private lateinit var mBluetoothEmitter: CompletableEmitter

    /**
     * requestLocationFeatureに対するEmitter
     */
    private lateinit var mLocationEmitter: CompletableEmitter

    /**
     * スキャンボタン
     */
    private val mScanButton: Button by lazy { findViewById<Button>(R.id.scanButton) }

    /**
     * デバイス一覧のリストボックス
     */
    private val mListView: ListView by lazy { findViewById<ListView>(R.id.deviceListView) }

    /**
     * スタックトレースを文字列化する
     */
    private fun Throwable.stackTraceString(): String {
        val sw = StringWriter()
        this.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    /**
     * Bluetooth機能が有効になっていない際は有効にする
     */
    private fun requestBluetoothFeature(): Completable {
        return Completable.create {
            // 既に有効になっていれば飛ばす
            if (mBluetoothAdapter.isEnabled)
                it.onComplete()

            // 有効になっていないので、有効にするように要求する
            mBluetoothEmitter = it
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE)
        }
    }

    /**
     * 位置情報機能が有効になっていない際は有効にする
     */
    private fun requestLocationFeature(): Completable {
        return Completable.create {
            // 既に有効になっていれば飛ばす
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                it.onComplete()
            }

            // 有効になっていないので、有効にするように要求する
            mLocationEmitter = it
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    /**
     * 周囲のデバイスをスキャンする
     * @param scanTime スキャンする時間
     */
    private fun scanBluetoothDevice(scanTime: Long): Single<List<Pair<BluetoothDevice, Int>>> {
        return Observable.create<BluetoothDevice> { emitter ->
            // BLEデバイスのスキャナーを用意する
            val scanner: BluetoothLeScanner? = mBluetoothAdapter.bluetoothLeScanner
            if (scanner != null) {
                // スキャン処理を実施する
                val callback = BleScanCallback()
                callback.setScanResultEmitter(emitter)
                scanner.startScan(callback)
                Thread.sleep(scanTime)
                scanner.stopScan(callback)
                emitter.onComplete()
            } else {
                emitter.onError(RuntimeException(resources.getString(R.string.ble_scan_failed)))
            }
        }
        .filter{
            // BLE対応機器以外は飛ばす
            val deviceType = BluetoothDeviceType.fromInt(it.type)
            (deviceType == BluetoothDeviceType.LE || deviceType == BluetoothDeviceType.Dual)
        }
        .flatMap { device ->
            val callback = BleGattCallback()
            Observable.create<BluetoothGatt> { emitter ->
                callback.setBluetoothGatttEmitter(emitter)
                device.connectGatt(this, false, callback)
            }.flatMap {
                Observable.create<Int>{emitter ->
                    callback.setIntEmitter(emitter)
                    if (!it.readRemoteRssi()) {
                        emitter.onError(RuntimeException(resources.getString(R.string.ble_rssi_failed)))
                    }
                }
            }.map {
                Pair(device, it)
            }
        }
        .toList()
        .map {pair ->
            // アドレスの重複を排除する処理
            val deviceDict: MutableMap<String, Pair<BluetoothDevice, Int>> = HashMap()
            for (device in pair) {
                deviceDict[device.first.address] = device
            }
            deviceDict.values.toList()
        }
    }

    @SuppressLint("CheckResult")
    private fun scanBleDeviceCommand() {
        // ボタンを無効化する
        mScanButton.isEnabled = false
        mScanButton.setTextColor(0xffe0e0e0.toInt())

        // デバイスをスキャンし、結果を表示する
        scanBluetoothDevice(5000)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // ログを表示する
                val deviceList = it.map {pair ->
                    val device = pair.first
                    val rssi = pair.second
                    BluetoothListItem(
                        favoriteFlg = false,
                        name = device.name ?: "(不明)",
                        address = "${device.address}  [${BluetoothDeviceType.fromInt(device.type)}]",
                        deviceType = "${BluetoothDeviceMinorType.fromInt(device.bluetoothClass.deviceClass)}",
                        rssi = rssi
                    )
                }
                val adapter = BluetoothListAdapter(this, R.layout.bluetooth_device_list_item, deviceList)
                mListView.adapter = adapter

                // ボタンを有効化する
                mScanButton.setTextColor(0xff000000.toInt())
                mScanButton.isEnabled = true

                // トーストを表示する
                Toast.makeText(this, "デバイス数：${deviceList.size}個", Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            })
    }

    /**
     * 起動時の処理
     */
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android端末がBLEをサポートしてるかの確認
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // BLEをサポートしてない場合
            Toast.makeText(this, R.string.ble_is_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Bluetoothアダプターを取得
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager.adapter == null) {
            // Bluetoothをサポートしていない場合
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            mBluetoothAdapter = bluetoothManager.adapter
        }

        // ボタンにイベントを設定する
        mScanButton.setOnClickListener {
            scanBleDeviceCommand()
        }
    }

    /**
     * 表示時の処理
     */
    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()

        // Bluetooth機能・位置情報機能が有効になっていない際は有効にする
        requestBluetoothFeature().concatWith(requestLocationFeature()).subscribe({
        }, {
            Log.e(TAG, it.stackTraceString())
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            finish()
        })
    }

    /**
     * Activityからの結果を受け取る
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // requestBluetoothFeatureメソッドを叩いた際に有効になる
            BLUETOOTH_REQUEST_CODE ->
                if (resultCode != Activity.RESULT_CANCELED) {
                    mBluetoothEmitter.onComplete()
                } else {
                    mBluetoothEmitter.onError(RuntimeException(resources.getString(R.string.bluetooth_is_not_working)))
                }
        }
    }

    /**
     * 機能を有効にするかの確認ダイアログの操作結果
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // requestLocationFeatureメソッドを叩いた際に有効になる
            LOCATION_REQUEST_CODE ->
                // ここで配列の要素数について調べないと、要素数0でOutOfRangeすることがあった
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationEmitter.onComplete()
                    } else {
                        mLocationEmitter.onError(RuntimeException(resources.getString(R.string.location_is_not_working)))
                    }
                }
        }
    }
}

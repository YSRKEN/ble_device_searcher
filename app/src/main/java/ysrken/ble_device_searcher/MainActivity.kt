package ysrken.ble_device_searcher

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {
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
    private var mBluetoothAdapter: BluetoothAdapter? = null

    /**
     * requestBluetooth処理におけるコンテキスト
     */
    private var requestBluetoothCont: Continuation<Boolean>? = null

    /**
     * requestLocation処理におけるコンテキスト
     */
    private var requestLocationCont: Continuation<Boolean>? = null

    /**
     * Bluetooth機能を有効にするように要求する
     */
    private suspend fun requestBluetoothAsync(): Boolean  = suspendCoroutine { cont ->
        requestBluetoothCont = cont

        // 既に有効になっていれば飛ばす
        if (mBluetoothAdapter!!.isEnabled()) {
            requestBluetoothCont?.resume(true)
        }

        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE)
    }

    /**
     * 位置情報機能が有効になっていない際は有効にする
     */
    private suspend fun requestLocationCoroutine(): Boolean  = suspendCoroutine { cont ->
        requestLocationCont = cont

        // Android 5.0以下なら確認しなくていい
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            requestLocationCont?.resume(true)
        }

        // 既に有効になっていれば飛ばす
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            requestLocationCont?.resume(true)
        }

        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }

    /**
     * 起動時の処理
     */
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
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            // Bluetoothをサポートしていない場合
            Toast.makeText(this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    /**
     * 表示時の処理
     */
    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            // Bluetooth機能が有効になっていない際は有効にする
            if (!requestBluetoothAsync()){
                Toast.makeText(this@MainActivity, R.string.bluetooth_is_not_working, Toast.LENGTH_SHORT).show()
                finish()
            }

            // 位置情報機能が有効になっていない際は有効にする
            if (!requestLocationCoroutine()){
                Toast.makeText(this@MainActivity, R.string.location_is_not_working, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Activityからの結果を受け取る
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
        // requestBluetoothFeatureメソッドを叩いた際に有効になる
        BLUETOOTH_REQUEST_CODE ->
            requestBluetoothCont?.resume(resultCode != Activity.RESULT_CANCELED)
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
            requestLocationCont?.resume(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        }
    }
}

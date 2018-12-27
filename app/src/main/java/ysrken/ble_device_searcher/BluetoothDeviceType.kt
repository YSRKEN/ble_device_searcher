package ysrken.ble_device_searcher

import android.bluetooth.BluetoothDevice.*

/**
 * Bluetoothデバイスの種類を示すenum
 */
enum class BluetoothDeviceType(val value: Int) {
    Classic(DEVICE_TYPE_CLASSIC),
    Dual(DEVICE_TYPE_DUAL),
    LE(DEVICE_TYPE_LE),
    Unknown(DEVICE_TYPE_UNKNOWN);

    companion object {
        val enumString = mapOf(
            Classic to "BR/DER",
            Dual to "BR/DER/LE",
            LE to "LE",
            Unknown to "不明"
        )

        fun fromInt(value: Int): BluetoothDeviceType {
            return BluetoothDeviceType.values().find { it.value == value } ?: Unknown
        }
    }

    override fun toString(): String {
        return enumString[this] ?: enumString[Unknown]!!
    }
}
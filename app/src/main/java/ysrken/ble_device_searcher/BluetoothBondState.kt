package ysrken.ble_device_searcher

import android.bluetooth.BluetoothDevice.*

/**
 * Bluetoothデバイスの接続状態を示すenum
 */
enum class BluetoothBondState(val value: Int) {
    Bonded(BOND_BONDED),
    Bonding(BOND_BONDING),
    None(BOND_NONE);

    companion object {
        val enumString = mapOf(
            Bonded to "ペアリング済",
            Bonding to "ペアリング作業中",
            None to "未ペアリング"
        )

        fun fromInt(value: Int): BluetoothBondState {
            return BluetoothBondState.values().find { it.value == value } ?: None
        }
    }

    override fun toString(): String {
        return enumString[this] ?: enumString[None]!!
    }
}
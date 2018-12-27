package ysrken.ble_device_searcher

import android.bluetooth.BluetoothClass.Device.Major.*

/**
 * Bluetoothデバイスの大分類を示すenum
 */
enum class BluetoothDeviceMajorType(val value: Int) {
    AudioVideo(AUDIO_VIDEO),
    Computer(COMPUTER),
    Health(HEALTH),
    Imaging(IMAGING),
    Misc(MISC),
    Networking(NETWORKING),
    Peripheral(PERIPHERAL),
    Phone(PHONE),
    Toy(TOY),
    Uncategorized(UNCATEGORIZED),
    Wearable(WEARABLE);

    companion object {
        fun fromInt(value: Int): BluetoothDeviceMajorType {
            return BluetoothDeviceMajorType.values().find { it.value == value } ?: Uncategorized
        }
    }

    override fun toString(): String {
        return this.name
    }
}
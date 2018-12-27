package ysrken.ble_device_searcher

import android.bluetooth.BluetoothClass.Device.*

/**
 * Bluetoothデバイスの詳細な分類を示すenum
 */
enum class BluetoothDeviceMinorType(val value: Int) {
    AudioVideoCamcorder(AUDIO_VIDEO_CAMCORDER),
    AudioVideoCarAudio(AUDIO_VIDEO_CAR_AUDIO),
    AudioVideoHandsfree(AUDIO_VIDEO_HANDSFREE),
    AudioVideoHeadphones(AUDIO_VIDEO_HEADPHONES),
    AudioVideoHifiAudio(AUDIO_VIDEO_HIFI_AUDIO),
    AudioVideoLoudspeaker(AUDIO_VIDEO_LOUDSPEAKER),
    AudioVideoMicrophone(AUDIO_VIDEO_MICROPHONE),
    AudioVideoPortableAudio(AUDIO_VIDEO_PORTABLE_AUDIO),
    AudioVideoSetTopBox(AUDIO_VIDEO_SET_TOP_BOX),
    AudioVideoUncategorized(AUDIO_VIDEO_UNCATEGORIZED),
    AudioVideoVcr(AUDIO_VIDEO_VCR),
    AudioVideoVideoCamera(AUDIO_VIDEO_VIDEO_CAMERA),
    AudioVideoVideoConferencing(AUDIO_VIDEO_VIDEO_CONFERENCING),
    AudioVideoVideoDisplayAndLoudspeaker(AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER),
    AudioVideoVideoGamingToy(AUDIO_VIDEO_VIDEO_GAMING_TOY),
    AudioVideoVideoMonitor(AUDIO_VIDEO_VIDEO_MONITOR),
    AudioVideoWearableHeadset(AUDIO_VIDEO_WEARABLE_HEADSET),
    ComputerDesktop(COMPUTER_DESKTOP),
    ComputerHandheldPcPda(COMPUTER_HANDHELD_PC_PDA),
    ComputerLaptop(COMPUTER_LAPTOP),
    ComputerPalmSizePcPda(COMPUTER_PALM_SIZE_PC_PDA),
    ComputerServer(COMPUTER_SERVER),
    ComputerUncategorized(COMPUTER_UNCATEGORIZED),
    ComputerWearable(COMPUTER_WEARABLE),
    HealthBloodPressure(HEALTH_BLOOD_PRESSURE),
    HealthDataDisplay(HEALTH_DATA_DISPLAY),
    HealthGlucose(HEALTH_GLUCOSE),
    HealthPulseOximeter(HEALTH_PULSE_OXIMETER),
    HealthPulseRate(HEALTH_PULSE_RATE),
    HealthThermometer(HEALTH_THERMOMETER),
    HealthUncategorized(HEALTH_UNCATEGORIZED),
    HealthWeighing(HEALTH_WEIGHING),
    PhoneCellular(PHONE_CELLULAR),
    PhoneCordless(PHONE_CORDLESS),
    PhoneIsdn(PHONE_ISDN),
    PhoneModemOrGateway(PHONE_MODEM_OR_GATEWAY),
    PhoneSmart(PHONE_SMART),
    PhoneUncategorized(PHONE_UNCATEGORIZED),
    ToyController(TOY_CONTROLLER),
    ToyDollActionFigure(TOY_DOLL_ACTION_FIGURE),
    ToyGame(TOY_GAME),
    ToyRobot(TOY_ROBOT),
    ToyUncategorized(TOY_UNCATEGORIZED),
    ToyVehicle(TOY_VEHICLE),
    WearableGlasses(WEARABLE_GLASSES),
    WearableHelmet(WEARABLE_HELMET),
    WearableJacket(WEARABLE_JACKET),
    WearablePager(WEARABLE_PAGER),
    WearableUncategorized(WEARABLE_UNCATEGORIZED),
    WearableWristWatch(WEARABLE_WRIST_WATCH),
    Uncategorized(-1);

    companion object {
        fun fromInt(value: Int): BluetoothDeviceMinorType {
            return BluetoothDeviceMinorType.values().find { it.value == value } ?: Uncategorized
        }
    }

    override fun toString(): String {
        return this.name
    }
}
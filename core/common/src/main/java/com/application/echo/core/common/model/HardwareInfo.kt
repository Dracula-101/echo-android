package com.application.echo.core.common.model

/**
 * CPU and RAM snapshot.
 *
 * @param cpuAbi            Primary ABI (e.g. "arm64-v8a")
 * @param supportedAbis     All ABIs supported by the device
 * @param cpuCores          Number of logical CPU cores
 * @param cpuFrequencyKhz   Max frequency of CPU core 0 in kHz (-1 if unavailable)
 * @param totalRamBytes     Total installed RAM in bytes
 * @param availableRamBytes Available RAM at snapshot time
 * @param lowRamDevice      Whether ActivityManager reports a low-RAM device
 * @param totalStorageBytes Total internal storage in bytes
 * @param availableStorageBytes Free internal storage bytes at snapshot time
 */
data class HardwareInfo(
    val cpuAbi: String,
    val supportedAbis: List<String>,
    val cpuCores: Int,
    val cpuFrequencyKhz: Long,
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val lowRamDevice: Boolean,
    val totalStorageBytes: Long,
    val availableStorageBytes: Long
)
package com.nmdlock.app.data.repository

import com.nmdlock.app.core.security.DeviceIdManager
import com.nmdlock.app.data.remote.api.DeviceApi
import com.nmdlock.app.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling device registration and status.
 */
@Singleton
class DeviceRepository @Inject constructor(
    private val deviceApi: DeviceApi,
    private val deviceIdManager: DeviceIdManager,
) {
    /**
     * Register or sync device with server.
     */
    suspend fun registerDevice(): Result<DeviceStatusResponse> {
        return try {
            val info = deviceIdManager.getDeviceInfo()
            val response = deviceApi.register(
                DeviceRegisterRequest(
                    deviceId = info.deviceId,
                    deviceName = info.deviceName,
                    deviceModel = info.deviceModel,
                    androidVersion = info.androidVersion,
                )
            )

            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: return Result.failure(Exception(body.message ?: "Registration response is empty"))
                Result.success(data)
            } else {
                Result.failure(Exception(body?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get device status from server.
     */
    suspend fun getDeviceStatus(): Result<DeviceStatusResponse> {
        return try {
            val deviceId = deviceIdManager.getDeviceId()
            val response = deviceApi.getStatus(deviceId)

            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: return Result.failure(Exception(body.message ?: "Device status response is empty"))
                Result.success(data)
            } else {
                Result.failure(Exception("Failed to get device status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get device history.
     */
    suspend fun getHistory(page: Int = 1, limit: Int = 20): Result<DeviceHistoryResponse> {
        return try {
            val response = deviceApi.getHistory(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: DeviceHistoryResponse())
            } else {
                Result.success(DeviceHistoryResponse())
            }
        } catch (e: Exception) {
            Result.success(DeviceHistoryResponse())
        }
    }

    /**
     * Get the device's unique ID.
     */
    fun getDeviceId(): String = deviceIdManager.getDeviceId()
}

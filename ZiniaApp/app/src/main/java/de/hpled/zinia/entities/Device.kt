package de.hpled.zinia.entities

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import java.io.Serializable

enum class DeviceType {
    SINGLE_LED,
    LED_CHAIN,
    UNKNOWN
}

class DeviceTypeConverter {
    @TypeConverter
    fun fromInt(ordinal: Int) = DeviceType.values().get(ordinal)

    @TypeConverter
    fun toInt(deviceType: DeviceType) = deviceType.ordinal
}

@Entity(tableName = "devices")
@TypeConverters(DeviceTypeConverter::class)
data class Device (
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "ip_address")
    val ipAddress: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "num_leds")
    val numLeds: Int,

    @ColumnInfo(name = "type")
    val type: DeviceType,

    @ColumnInfo(name = "isRGBW")
    val isRGBW: Boolean = false

) : Serializable {
    companion object {
        @JvmStatic
        fun newInstance(
            ip: String, name: String, numLeds: Int, type: DeviceType, isRGBW: Boolean = false) : Device {
            return Device(0, ip, name, numLeds, type, isRGBW)
        }
    }
}

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices")
    fun findAll() : List<Device>

    @Query("SELECT * FROM devices")
    fun findAllLiveData() : LiveData<List<Device>>

    @Query("SELECT * FROM devices WHERE id = (:deviceId) LIMIT 1")
    fun findById(deviceId: Long) : Device

    @Query("SELECT * FROM devices WHERE name = (:deviceName)")
    fun findAllByName(deviceName: String) : List<Device>

    @Insert
    fun insertAll(vararg device: Device)

    @Insert
    fun insert(device: Device) : Long

    @Delete
    fun deleteAll(vararg device: Device)

}
package pro.darc.cake.module.storage


interface CakeStorage {

    var owner: StorageOwner

    fun get(key: String): Any
    fun set(key: String, value: Any)

    fun getShort(key: String): Short?
    fun setShort(key: String, value: Short)

    fun getInt(key: String): Int?
    fun setInt(key: String, value: Int)

    fun getLong(key: String): Long?
    fun setLong(key: String, value: Long)

    fun getFloat(key: String): Float?
    fun setFloat(key: String, value: Float)

    fun getDouble(key: String): Double?
    fun setDouble(key: String, value: Double)

    fun getString(key: String): String?
    fun setString(key: String, value: String)

    fun getBoolean(key: String): Boolean?
    fun setBoolean(key: String, value: Boolean)

    // TODO Changing parameter clazz to a restricted type would make it work all the time consistently
    //  ,see https://github.com/Kotlin/kotlinx.serialization/issues/329
    fun<T> getObject(key: String): T
    fun<T> setObject(key: String, value: T)

    fun setNull(key: String)

    /**
     * A pure function that migrate this storage with other
     *
     * Returning a new CakeStorage instance
     *
     * @param other other storage
     *
     * @return New instance of migrated storages
     */
    fun migrate(other: CakeStorage): CakeStorage

}
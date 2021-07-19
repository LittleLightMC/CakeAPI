package pro.darc.cake.module.distribute

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.SerializationCodec
import pro.darc.cake.core.inject.LifeCycle
import pro.darc.cake.core.inject.LifeInject
import pro.darc.cake.module.extensions.Config
import pro.darc.cake.utils.Priority

val redisson: RedissonClient = Manager.getClient()

object Manager {
    lateinit var redissonClient: RedissonClient

    @LifeInject([LifeCycle.CakeReload, LifeCycle.CakeEnable], Priority.MID)
    @JvmStatic
    fun init() {
        redissonClient = createFromConfig()
    }

    fun getClient() = redissonClient

    private fun createFromConfig(): RedissonClient {
        val config = org.redisson.config.Config()
        val nodes = Config.db.getStringList("redis.nodes")
        val commandTimeout = Config.db.getInt("redis.command timeout")
        val connectTimeout = Config.db.getInt("redis.connect timeout")
        val retryTime = Config.db.getInt("redis.retry time")
        val retryInterval = Config.db.getInt("redis.retry interval")
        val database = Config.db.getInt("redis.database")
        config.codec = SerializationCodec()
        when(Config.db.getString("redis.mode")) {
            "cluster" -> {
                config.useClusterServers()
                    .setScanInterval(3000)
                    .addNodeAddress(*nodes.toTypedArray())
                    .setConnectTimeout(connectTimeout)
                    .setTimeout(commandTimeout)
                    .setRetryAttempts(retryTime)
                    .setRetryInterval(retryInterval)
            }
            "master_slave" -> {
                config.useMasterSlaveServers()
                    .setMasterAddress(nodes.first())
                    .setDatabase(database)
                    .addSlaveAddress(*nodes.subList(1, nodes.size).toTypedArray())
                    .setConnectTimeout(connectTimeout)
                    .setTimeout(commandTimeout)
                    .setRetryAttempts(retryTime)
                    .setRetryInterval(retryInterval)
            }
            else -> {
                config.useSingleServer()
                    .setAddress(nodes.first())
                    .setDatabase(database)
                    .setConnectTimeout(connectTimeout)
                    .setTimeout(commandTimeout)
                    .setRetryAttempts(retryTime)
                    .setRetryInterval(retryInterval)
            }
        }
        return Redisson.create(config)
    }
}

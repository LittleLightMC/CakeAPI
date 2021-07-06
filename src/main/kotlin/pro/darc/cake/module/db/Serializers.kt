package pro.darc.cake.module.db

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.bukkit.*
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.banner.Pattern
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.util.BlockVector
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.util.*

object OfflinePlayerSerializer: KSerializer<OfflinePlayer> {
    override fun deserialize(decoder: Decoder): OfflinePlayer {
        val uuid = UUID.fromString(decoder.decodeString())
        return Bukkit.getOfflinePlayer(uuid)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OfflinePlayer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OfflinePlayer) {
        encoder.encodeString(value.uniqueId.toString())
    }
}

object WorldSerializer: KSerializer<World> {

    class WorldNotFoundException(uid: UUID?): RuntimeException("World(uid=$uid) not found on this server!")

    override fun deserialize(decoder: Decoder): World {
        val uid = UUID.fromString(decoder.decodeString())
        return Bukkit.getWorld(uid) ?: throw WorldNotFoundException(uid)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("World", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: World) {
        encoder.encodeString(value.uid.toString())
    }
}

object LocationSerializer: KSerializer<Location> {
    override fun deserialize(decoder: Decoder): Location {
        val x = decoder.decodeDouble()
        val y = decoder.decodeDouble()
        val z = decoder.decodeDouble()
        val pitch = decoder.decodeFloat()
        val yaw = decoder.decodeFloat()
        val world = decoder.decodeSerializableValue(WorldSerializer)
        return Location(world, x, y, z, yaw, pitch)
    }

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeDouble(value.x)
        encoder.encodeDouble(value.y)
        encoder.encodeDouble(value.z)
        encoder.encodeFloat(value.pitch)
        encoder.encodeFloat(value.yaw)
        encoder.encodeSerializableValue(WorldSerializer, value.world!!)
    }

    override val descriptor: SerialDescriptor get() = buildClassSerialDescriptor("Location") {
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
        element<Float>("pitch")
        element<Float>("yaw")
        element<World>("world")
    }

}

/**
 * Serializing object using Bukkit's Configuration Serializable.
 * Using (slow) reflection, so keep your eyes on performance.
 */
open class YAMLConfigurationBasedSerializer<T: ConfigurationSerializable>(
    private val clazz: Class<T>
): KSerializer<T> {

    override fun deserialize(decoder: Decoder): T {
        val map = Json.decodeFromString<MapStringAny>(decoder.decodeString()).entries
        val method = clazz.getDeclaredMethod("deserialize", Map::class.java)
        method.trySetAccessible()
        val res = method.invoke(null, map)
        return res as T
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(clazz.name, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(Json.encodeToString(MapStringAny(value.serialize())))
    }
}


object AttributeModifierSerializer: YAMLConfigurationBasedSerializer<AttributeModifier>(AttributeModifier::class.java)

object BlockVectorSerializer: YAMLConfigurationBasedSerializer<BlockVector>(BlockVector::class.java)

object BoundingBoxSerializer: YAMLConfigurationBasedSerializer<BoundingBox>(BoundingBox::class.java)

object ColorSerializer: YAMLConfigurationBasedSerializer<Color>(Color::class.java)

object FireworkEffectSerializer: YAMLConfigurationBasedSerializer<FireworkEffect>(FireworkEffect::class.java)

object ItemStackSerializer: YAMLConfigurationBasedSerializer<ItemStack>(ItemStack::class.java)

object PatternSerializer: YAMLConfigurationBasedSerializer<Pattern>(Pattern::class.java)

object PotionEffectSerializer: YAMLConfigurationBasedSerializer<PotionEffect>(PotionEffect::class.java)

object VectorSerializer: YAMLConfigurationBasedSerializer<Vector>(Vector::class.java)

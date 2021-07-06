package pro.darc.cake.module.db

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = OfflinePlayer::class)
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = World::class)
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Location::class)
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

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = AttributeModifier::class)
object AttributeModifierSerializer: YAMLConfigurationBasedSerializer<AttributeModifier>(AttributeModifier::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = BlockVector::class)
object BlockVectorSerializer: YAMLConfigurationBasedSerializer<BlockVector>(BlockVector::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = BoundingBox::class)
object BoundingBoxSerializer: YAMLConfigurationBasedSerializer<BoundingBox>(BoundingBox::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Color::class)
object ColorSerializer: YAMLConfigurationBasedSerializer<Color>(Color::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = FireworkEffect::class)
object FireworkEffectSerializer: YAMLConfigurationBasedSerializer<FireworkEffect>(FireworkEffect::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ItemStack::class)
object ItemStackSerializer: YAMLConfigurationBasedSerializer<ItemStack>(ItemStack::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Pattern::class)
object PatternSerializer: YAMLConfigurationBasedSerializer<Pattern>(Pattern::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PotionEffect::class)
object PotionEffectSerializer: YAMLConfigurationBasedSerializer<PotionEffect>(PotionEffect::class.java)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Vector::class)
object VectorSerializer: YAMLConfigurationBasedSerializer<Vector>(Vector::class.java)

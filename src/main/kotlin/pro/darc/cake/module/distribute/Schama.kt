package pro.darc.cake.module.distribute

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SendPlayerToServerRequest(
    @Contextual val uuid: UUID,
    val target: String,
): java.io.Serializable

@Serializable
data class PlayerMessageRequest(
    @Contextual val target: UUID,
    val xmlText: String,
): java.io.Serializable

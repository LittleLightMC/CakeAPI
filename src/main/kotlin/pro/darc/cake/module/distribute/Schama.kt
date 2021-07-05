package pro.darc.cake.module.distribute

import java.util.*

data class SendPlayerToServerRequest(
    val uuid: UUID,
    val target: String,
)

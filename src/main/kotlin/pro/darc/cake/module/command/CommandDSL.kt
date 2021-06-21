package pro.darc.cake.module.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class Executor<E: CommandSender>(
    val sender: E,
    val label: String,
    val args: Array<out String>,
    val command: Any, // TODO
    val scope: CoroutineScope,
)

open class CommandDSL (
    val plugin: Plugin,
    name: String,
    vararg aliases: String = arrayOf(),
    executor: Any, // TODO
    var errorHandler: Any, // TODO
    var job: Job = SupervisorJob(),
    private val coroutineScope: CoroutineScope = CoroutineScope(job)
)

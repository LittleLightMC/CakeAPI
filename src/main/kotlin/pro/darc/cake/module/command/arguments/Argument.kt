package pro.darc.cake.module.command.arguments

import pro.darc.cake.module.command.CommandFailException
import pro.darc.cake.module.command.Executor
import pro.darc.cake.module.command.TabCompleter

inline fun TabCompleter.argumentCompleteBuilder(
    index: Int,
    block: (String) -> List<String>
): List<String> {
    if(args.size == index+1) {
        return block(args.getOrNull(index) ?: "")
    }
    return emptyList()
}

inline fun <T> Executor<*>.optional(block: () -> T): T? {
    return try {
        block()
    }catch (exception: CommandFailException) {
        if(exception.argMissing) null
        else throw exception
    }
}

inline fun <reified T> Executor<*>.array(
    startIndex: Int,
    endIndex: Int,
    usageIndexPerArgument: Int = 1,
    block: (index: Int) -> T
): Array<T> {
    if (endIndex <= startIndex)
        throw IllegalArgumentException("endIndex can't be lower or equals a startIndex.")
    if(usageIndexPerArgument <= 0)
        throw IllegalArgumentException("usageIndexPerArgument can't be lower than 1.")

    val arguments = (endIndex - startIndex) / usageIndexPerArgument

    return Array(arguments) {
        block(startIndex + (it * usageIndexPerArgument))
    }
}

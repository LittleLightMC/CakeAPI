package pro.darc.cake.module.extensions
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

fun Block.sendBlockChange(
    blockData: BlockData,
    players: List<Player>
) {
    players.filter { it.world.name == world.name }.forEach {
        it.sendBlockChange(location, blockData)
    }
}

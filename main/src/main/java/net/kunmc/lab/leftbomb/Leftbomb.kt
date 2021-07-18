package net.kunmc.lab.leftbomb

import com.github.bun133.flylib2.commands.Commander
import com.github.bun133.flylib2.commands.CommanderBuilder
import com.github.bun133.flylib2.commands.TabChain
import com.github.bun133.flylib2.commands.TabObject
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class Leftbomb : JavaPlugin(), Listener {
    companion object {
        var Max = 100.0
        var Power = 100.0F
    }

    var players = mutableListOf<Player>()
    var isAll = false

    override fun onEnable() {
        // Plugin startup logic
        val command = Command(this)
        command.register("lb")
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        players.clear()
        isAll = false
    }

    @EventHandler
    fun onLeftClick(e: PlayerInteractEvent) {
        if (!players.contains(e.player) && !isAll) return
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            // On Click
            val b = e.player.rayTraceBlocks(Max)
            if (b != null) {
                val block = b.hitBlock
                block?.location?.createExplosion(Power)
            }
        }
    }
}

fun Command(Leftbomb: Leftbomb) = Commander<Leftbomb>(
    Leftbomb, "Main LeftBomb Plugin", "/lb start|startme|stop|bomb|range <Int>",
    CommanderBuilder<Leftbomb>()
        .addTabChain(TabChain(TabObject("start", "stop","startme")))
        .addFilter(CommanderBuilder.Filters.OP())
        .setInvoker { leftbomb, commandSender, strings ->
            when (strings[0]) {
                "start" -> {
                    Bukkit.broadcastMessage("" + ChatColor.BOLD + "爆発開始!")
                    leftbomb.isAll = true
                }
                "end" -> {
                    Bukkit.broadcastMessage("" + ChatColor.BOLD + "爆発終了!")
                    leftbomb.isAll = false
                    leftbomb.players.clear()
                }
                "startme"->{
                    if(commandSender is Player){
                        commandSender.sendMessage("あなたは神の力を手に入れた...")
                        leftbomb.players.add(commandSender)
                    }
                }
                else -> return@setInvoker false
            }
            return@setInvoker true
        },
    CommanderBuilder<Leftbomb>()
        .addTabChain(TabChain(TabObject("bomb", "range"), IntTabObject()))
        .addFilter(CommanderBuilder.Filters.OP())
        .setInvoker { leftbomb, commandSender, strings ->
            when (strings[0]) {
                "bomb" -> {
                    val i = strings[1].toIntOrNull() ?: return@setInvoker false
                    net.kunmc.lab.leftbomb.Leftbomb.Power = (i * 4).toFloat()
                    commandSender.sendMessage("威力をTNT${i}個分に設定しました")
                }
                "range" -> {
                    val i = strings[1].toIntOrNull() ?: return@setInvoker false
                    net.kunmc.lab.leftbomb.Leftbomb.Max = i.toDouble()
                    commandSender.sendMessage("最大距離を${i}ブロックに設定しました")
                }
                else -> return@setInvoker false
            }

            return@setInvoker true
        },
)

class IntTabObject() : TabObject() {
    override fun isMatch(s: String): Boolean {
        return s.toIntOrNull() != null
    }
}
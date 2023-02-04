package me.nazarxexe.survival.econ

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent

class EconomyRegistrations(plugin: Economy) : Listener {
    private val plugin: Economy?
    private val api: EconomyAPI?

    init {
        this.plugin = plugin
        this.api = this.plugin.api
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        this.api?.databaseToCached(e.player);
    }

    @EventHandler
    fun onLeft(e: PlayerQuitEvent) {
        api?.cachedToDatabase(e.player)
        api?.cache?.remove(e.player.uniqueId)
    }
}
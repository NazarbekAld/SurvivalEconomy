package me.nazarxexe.survival.econ

import cn.nukkit.Player
import cn.nukkit.scheduler.AsyncTask
import cn.nukkit.utils.TextFormat
import me.nazarxexe.survival.core.economy.EconomyManager
import me.nazarxexe.survival.core.economy.Pocket
import me.nazarxexe.survival.core.tools.TerminalComponent
import me.nazarxexe.survival.core.tools.TextComponent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class EconomyAPI(plugin: Economy) {
    private val plugin: Economy?
    private val manager: EconomyManager?
    val cache: MutableMap<UUID?, Pocket?>?

    private val confighook: HashMap<String?, String?>?

    init {
        this.plugin = plugin
        manager = plugin.economyManager
        cache = HashMap()
        confighook = plugin.confighook
    }

    fun isPlayerOnDatabase(player: UUID): CompletableFuture<Boolean?>? {
        return manager!!.loadPocket("econ", player)?.thenApplyAsync { obj: Pocket? -> Objects.nonNull(obj) }
    }

    fun isPlayerOnDatabase(player: Player): CompletableFuture<Boolean?>? {
        return manager!!.loadPocket("econ", player.uniqueId)?.thenApplyAsync { obj: Pocket? -> Objects.nonNull(obj) }
    }

    fun databaseToCached(player: Player) {
        plugin!!.server.scheduler.scheduleAsyncTask(plugin, object : AsyncTask() {
            override fun onRun() {
                manager!!.loadPocket("econ", player.uniqueId)!!.thenAcceptAsync(Consumer { pocket: Pocket? ->
                    if (pocket == null) {
                        cache!![player.uniqueId] = Pocket("econ", player.uniqueId)
                        return@Consumer
                    }
                    cache!![player.uniqueId] = pocket
                }).join()
            }
        })
    }

    fun cachedToDatabase(player: Player) {
        if (cache!![player.uniqueId] == null) {
            TerminalComponent(
                plugin!!.logger, TextComponent()
                    .combine(TextFormat.RED)
                    .combine("Cannot save ")
                    .combine(player.uniqueId.toString())
                    .combine("! Because not exists on cache!")
            ).warn()
            return
        }
        manager!!.savePocket(cache[player.uniqueId]!!)
    }

    fun add(payee: UUID, balance: Long): Boolean {
        val payeePocket = getPocket(payee) ?: return false
        payeePocket.addBalance(balance)
        return true
    }

    fun decrement(payer: UUID, balance: Long): Boolean {
        val payerPocket = getPocket(payer) ?: return false
        payerPocket.decrementBalance(balance)
        return true
    }

    operator fun set(player: UUID, balance: Long): Boolean {
        val playerPocket = getPocket(player) ?: return false
        playerPocket.balance = balance
        return true
    }

    operator fun get(player: UUID): Long {
        val playerPocket = getPocket(player) ?: return 0L
        return playerPocket.balance
    }


    fun getPocket(player: UUID): Pocket? {
        return cache?.get(player)
    }
}
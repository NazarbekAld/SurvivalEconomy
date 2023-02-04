package me.nazarxexe.survival.econ.commands

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import me.nazarxexe.survival.core.command.CommandExecutable
import me.nazarxexe.survival.econ.Economy
import me.nazarxexe.survival.econ.EconomyAPI
import org.apache.commons.text.StringSubstitutor

class EconomyCommand(private val plugin: Economy?) {
    private val confighook: HashMap<String?, String?>? = plugin?.confighook
    private var api: EconomyAPI? = plugin?.api

    private fun execute(sender: CommandSender?, commandLabel: String?, args: Array<String?>?): Boolean {
        if (sender !is Player) return true
        val temp = confighook
        if (args!!.size < 2) {
            return false
        }
        temp!!["payee"] = args[1]
        temp["player"] = sender.asPlayer()!!.name
        if (args[0].equals("get", ignoreCase = true)) {
            temp["amount"] = "0"
        }
        if (args.size > 3) {
            temp["amount"] = args[3]
        }
        when (args[0]) {
            "add" -> { // ADD
                if (!sender.hasPermission("economy.add")) {
                    sender.sendMessage(StringSubstitutor(temp).replace("\${NO_PERMISSIONS}"))
                    return true
                }
                if (plugin!!.server.getPlayer(args[1]) == null) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["TRANSACTION_ERROR"]))
                    return true
                }
                val success = api!!.add(plugin.server.getPlayer(args[1]).uniqueId, args[2]!!.toLong())
                if (!success) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["TRANSACTION_ERROR"]))
                    return true
                }
                sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["BAL_ADD"]))
            }

            "decrement" -> {
                // DECREMENT
                if (!sender.hasPermission("economy.decrement")) {
                    sender.sendMessage(StringSubstitutor(temp).replace("\${NO_PERMISSIONS}"))
                    return true
                }
                if (plugin!!.server.getPlayer(args[1]) == null) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook?.get("TRANSACTION_ERROR")))
                    return true
                }
                val success = api!!.decrement(plugin.server.getPlayer(args[1]).uniqueId, args[2]!!.toLong())
                if (!success) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook?.get("TRANSACTION_ERROR")))
                    return true
                }
                sender.sendMessage(StringSubstitutor(temp).replace(confighook?.get("BAL_DECREMENT")))
            }

            "get" -> {
                // GET
                if (!sender.hasPermission("economy.get")) {
                    sender.sendMessage(StringSubstitutor(confighook).replace("\${NO_PERMISSIONS}"))
                    return true
                }
                if (plugin!!.server.getPlayer(args[1]) == null) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook?.get("TRANSACTION_ERROR")))
                    return true
                }
                val bal = api?.get(plugin.server.getPlayer(args[1]).uniqueId)
                temp.replace("amount", bal.toString())
                sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["BAL_GET"]))
            }

            "set" -> {             // SET
                if (!sender.hasPermission("economy.set")) {
                    sender.sendMessage(StringSubstitutor(temp).replace("\${NO_PERMISSIONS}"))
                    return true
                }
                if (plugin!!.server.getPlayer(args[1]) == null) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["TRANSACTION_ERROR"]))
                    return true
                }
                val success = api!!.set(plugin.server.getPlayer(args[1]).uniqueId, args[2]!!.toLong())
                if (!success) {
                    sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["TRANSACTION_ERROR"]))
                    return true
                }
                sender.sendMessage(StringSubstitutor(temp).replace(confighook!!["BAL_SET"]))
            }
        }
        return true
    }

    var name: String? = "economy"
    var desc: String? = "A economy command!"
    var usage: String? = "/economy (get/set/add/decrement) (playername) (amount)"
    var alts: Array<String?>? = arrayOf("eco")
    var executable: CommandExecutable? =
        CommandExecutable { sender: CommandSender?, commandLabel: String?, args: Array<String?>? ->
            execute(
                sender,
                commandLabel,
                args
            )
        }

}
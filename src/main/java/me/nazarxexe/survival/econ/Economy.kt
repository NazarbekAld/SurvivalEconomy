package me.nazarxexe.survival.econ

import cn.nukkit.plugin.PluginBase
import cn.nukkit.plugin.service.ServicePriority
import cn.nukkit.utils.Config
import cn.nukkit.utils.TextFormat
import me.nazarxexe.survival.core.chat.ChatManager
import me.nazarxexe.survival.core.command.EzCommand
import me.nazarxexe.survival.core.economy.EconomyManager
import me.nazarxexe.survival.core.tools.TerminalComponent
import me.nazarxexe.survival.core.tools.TextComponent
import me.nazarxexe.survival.econ.commands.EconomyCommand
import java.io.File

class Economy : PluginBase() {
    var economyManager: EconomyManager? = null
    var chatManager: ChatManager? = null
    var api: EconomyAPI? = null
    var conf: Config? = null
    var confighook: HashMap<String?, String?>? = null

    override fun onEnable() {
        saveDefaultConfig()
        conf = Config(
            File(dataFolder, "config.yml"),
            Config.YAML
        )
        confighook = HashMap()

        for (i in conf!!.keys) {
            confighook!![i] = TextFormat.colorize('&', conf!!.get(i, "CONFIG ERROR"))
        }

        if (!server.pluginManager.getPlugin("SurvivalCore").isEnabled) {
            logger.error("SurvivalCore dependency not found!")
            logger.error("Disabling the plugin.....")
            pluginLoader.disablePlugin(this)
            return
        }
        TerminalComponent(
            logger,
            TextComponent.coloredText(TextFormat.BLUE, "Grabbing managers...")
        )
            .info()
        economyManager = server.serviceManager.getProvider(EconomyManager::class.java).provider
        chatManager = server.serviceManager.getProvider(ChatManager::class.java).provider
        if (economyManager == null && chatManager == null) {
            TerminalComponent(
                logger, TextComponent(
                    TextComponent.coloredText(TextFormat.RED, "Failed to grab managers.")
                        .add(TextComponent.coloredText(TextFormat.RED, "Disabling the plugin..."))
                )
            )
                .error()
            pluginLoader.disablePlugin(this)
        } else {
            TerminalComponent(
                logger, TextComponent(
                    TextComponent.coloredText(TextFormat.GREEN, "Successfully grabbed economy manager.")
                        .add(
                            TextComponent.coloredText(
                                TextFormat.GREEN, "Successfully grabbed chat manager"
                            )
                        )
                )
            )
                .info()
        }
        api = EconomyAPI(this)
        server.serviceManager.register(
            EconomyAPI::class.java,
            api,
            this,
            ServicePriority.NORMAL
        )
        server.pluginManager.registerEvents(EconomyRegistrations(this), this)
        registerCommands()
    }

    override fun onDisable() {}
    private fun registerCommands() {
        EzCommand(EconomyCommand(this)).register(this)
        // No more command check extensions!
    }
}
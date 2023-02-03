
package me.nazarxexe.survival.econ;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.service.ServicePriority;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import me.nazarxexe.survival.core.chat.ChatManager;
import me.nazarxexe.survival.core.command.CommandExecutable;
import me.nazarxexe.survival.core.command.EzCommand;
import me.nazarxexe.survival.core.economy.EconomyManager;
import me.nazarxexe.survival.core.tools.TerminalComponent;
import me.nazarxexe.survival.core.tools.TextComponent;
import me.nazarxexe.survival.econ.commands.EconomyCommand;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.util.*;

@Getter
@SuppressWarnings({ "unused", "FieldMayBeFinal" })
public class Economy extends PluginBase {

    EconomyManager economyManager;
    ChatManager chatManager;

    EconomyAPI api;

    List<EzCommand> ezcommands = new ArrayList<>();


    private Config config;
    private String ECONOMY_CHAR;

    private HashMap<String, String> confighook;



    @Override
    public void onEnable(){

        saveDefaultConfig();

        config = new Config(
                new File(this.getDataFolder(), "config.yml"),
                Config.YAML
        );

        confighook = new HashMap<>();

        for (String i : config.getKeys()){
            confighook.put(i, TextFormat.colorize('&', config.get(i, "CONFIG ERROR")));
        }

        if (!(getServer().getPluginManager().getPlugin("SurvivalCore").isEnabled())){

            getLogger().error("SurvivalCore dependency not found!");
            getLogger().error("Disabling the plugin.....");
            getPluginLoader().disablePlugin(this);
            return;
        }


        new TerminalComponent(getLogger(),
                TextComponent.coloredText(TextFormat.BLUE, "Grabbing managers..."))
                .info();
        economyManager = getServer().getServiceManager().getProvider(EconomyManager.class).getProvider();
        chatManager = getServer().getServiceManager().getProvider(ChatManager.class).getProvider();

        if (economyManager == null && chatManager == null){
            new TerminalComponent(getLogger(), new TextComponent(
                    TextComponent.coloredText(TextFormat.RED, "Failed to grab managers.")
                            .add(TextComponent.coloredText(TextFormat.RED, "Disabling the plugin..."))))
                    .error();
            getPluginLoader().disablePlugin(this);
        }else {
            new TerminalComponent(getLogger(), new TextComponent(
                    TextComponent.coloredText(TextFormat.GREEN, "Successfully grabbed economy manager.")
                            .add(
                            TextComponent.coloredText(
                                    TextFormat.GREEN, "Successfully grabbed chat manager"))))
                    .info();
        }




        api = new EconomyAPI(this);
        getServer().getServiceManager().register(
                EconomyAPI.class,
                api,
                this,
                ServicePriority.NORMAL
        );

        getServer().getPluginManager().registerEvents(new EconomyRegistrations(this), this);

        registerCommands();


    }

    @Override
    public void onDisable(){

    }



    private void registerCommands() {
        new EzCommand(new EconomyCommand(this)).register(this);
        // No more command check extensions!
    }







}

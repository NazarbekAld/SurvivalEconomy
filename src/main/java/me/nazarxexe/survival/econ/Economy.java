package me.nazarxexe.survival.econ;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.service.ServicePriority;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import me.nazarxexe.survival.core.chat.ChatManager;
import me.nazarxexe.survival.core.economy.EconomyManager;
import me.nazarxexe.survival.core.tools.TerminalComponent;
import me.nazarxexe.survival.core.tools.TextComponent;

@Getter
public class Economy extends PluginBase {

    EconomyManager economyManager;
    ChatManager chatManager;

    EconomyAPI api;

    @Override
    public void onEnable(){
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

    }

    @Override
    public void onDisable(){

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(command.getName().equals("economy"))) return true;

        switch (args[0]) {
            case "add" -> {
                if (!(sender.hasPermission("economy.add"))) {
                    sender.sendMessage(new TextComponent(TextComponent.coloredText(
                            TextFormat.RED,
                            "Not enough permissions."))
                            .getText());
                    return true;
                }
                boolean success = api.add(getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));
                if (!(success)) {

                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.RED + "Player not online or caused error.")
                            .getText());

                    return true;
                }
                sender.sendMessage(new TextComponent()
                        .combine(TextFormat.GREEN + "Added ")
                        .combine(args[2])
                        .combine("$ to ")
                        .combine(args[1])
                        .getText());
            }
            case "decrement" -> {
                if (!(sender.hasPermission("economy.decrement"))) {
                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.RED + "Not enough permissions.")
                            .getText());
                    return true;
                }
                boolean success = api.decrement(getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));

                if (!(success)) {

                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.RED + "Player not online or caused error.")
                            .getText());

                    return true;
                }
                sender.sendMessage(new TextComponent()
                        .combine(TextFormat.RED + "Decreased ")
                        .combine(args[2])
                        .combine("$ to ")
                        .combine(args[1])
                        .getText());
            }
            case "get" -> {
                if (!(sender.hasPermission("economy.get"))) {
                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.RED + "Not enough permissions.")
                            .getText());
                    return true;
                }
                long bal = api.get(getServer().getPlayer(args[1]).getUniqueId());
                sender.sendMessage(new TextComponent()
                        .combine(TextFormat.GREEN)
                        .combine(args[1])
                        .combine(" has ")
                        .combine(String.valueOf(bal))
                        .combine("$")
                        .getText());
            }
            case "cache" -> sender.sendMessage(new TextComponent(String.valueOf(api.getCache().size()))
                    .add(api.getCache().toString())
                    .getText());
        }

        return true;
    }




}

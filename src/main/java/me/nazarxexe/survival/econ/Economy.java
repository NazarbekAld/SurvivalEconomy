
package me.nazarxexe.survival.econ;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.service.ServicePriority;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import me.nazarxexe.survival.core.chat.ChatManager;
import me.nazarxexe.survival.core.command.CommandExecutable;
import me.nazarxexe.survival.core.command.EzCommand;
import me.nazarxexe.survival.core.economy.EconomyManager;
import me.nazarxexe.survival.core.tools.TerminalComponent;
import me.nazarxexe.survival.core.tools.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@SuppressWarnings({ "unused", "FieldMayBeFinal" })
public class Economy extends PluginBase {

    EconomyManager economyManager;
    ChatManager chatManager;

    EconomyAPI api;

    List<EzCommand> ezcommands = new ArrayList<>();

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

        registerCommands();


    }

    @Override
    public void onDisable(){

    }



    private void registerCommands() {

        this.getServer().getCommandMap().getCommands().remove("economy");
        this.getServer().getCommandMap().getCommands().remove("balance");
        this.getServer().getCommandMap().getCommands().remove("pay");

        ezcommands.add( new EzCommand(new Object () {
            private boolean execute(CommandSender sender, String commandLabel, String[] args) {

                if (!(sender instanceof Player)) return true;

                switch (args[0]) {
                    case "add" -> { // ADD
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
                        break;
                    }
                    case "decrement" -> {       // DECREMENT
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
                        break;
                    }
                    case "get" -> {                      // GET
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
                    case "set" -> {             // SET
                        if (!(sender.hasPermission("economy.set"))) {
                            sender.sendMessage(new TextComponent()
                                    .combine(TextFormat.RED + "Not enough permissions.")
                                    .getText());
                            return true;
                        }

                        boolean success = api.set(getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));

                        if (!(success)) {
                            sender.sendMessage(new TextComponent()
                                    .combine(TextFormat.RED + "Player not online or caused error.")
                                    .getText());
                            return true;
                        }

                        sender.sendMessage(new TextComponent()
                                .combine(TextFormat.GREEN)
                                .combine("")
                                .getText());
                        break;
                    }
                }

                return true;
            }

            String name = "economy";
            String desc = "A economy command!";
            String usage = "/economy (get/set/add/decrement) (playername) (amount)";
            String[] alts = { "eco" };

            CommandExecutable executable = this::execute;

        }).register(this) );

        ezcommands.add( new EzCommand(new Object() {

            String name = "balance";
            String desc = "Check your balance.";
            String[] alts = { "bal" };

            CommandExecutable executable = new CommandExecutable() {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    if (!(sender instanceof Player)) return true;

                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.GREEN)
                            .combine("You have " + api.get(sender.asPlayer().getUniqueId()))
                            .combine("$").getText());

                    return true;
                }
            };

        }).register(this) );

        ezcommands.add( new EzCommand(new Object() {
            String name = "pay";
            String desc = "Pay to player!";
            CommandExecutable executable = new CommandExecutable() {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {

                    if (!(sender instanceof Player)) return true;

                    long payer = api.get(sender.asPlayer().getUniqueId());

                    if (!(Long.parseLong(args[1]) > 0L)) {
                        sender.sendMessage(new TextComponent()
                                .combine(TextFormat.RED)
                                .combine("You need to pay more than 0!")
                                .getText());
                        return true;
                    }

                    if (payer > (Long.parseLong(args[1]))){
                        sender.sendMessage(new TextComponent()
                                .combine(TextFormat.RED)
                                .combine("Not enough money!")
                                .getText());
                        return true;
                    }

                    boolean send = api.decrement(sender.asPlayer().getUniqueId(), (Long.parseLong(args[1])));
                    if (!(send)){
                        failed(sender);
                        return true;
                    }
                    boolean receive = api.add(UUID.fromString(args[0]), (Long.parseLong(args[1])));
                    if (!(receive)){
                        failed(sender);
                        api.add(sender.asPlayer().getUniqueId(), (Long.parseLong(args[1])));
                        return true;
                    }

                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.GREEN)
                            .combine("You sent ")
                            .combine(args[1])
                            .combine("$ to ")
                            .combine(args[1])
                            .getText());
                    return true;
                }

                private void failed(CommandSender sender){
                    sender.sendMessage(new TextComponent()
                            .combine(TextFormat.RED)
                            .combine("Failed to send money!")
                            .getText());
                }

            };
        }).register(this) );



    }







}

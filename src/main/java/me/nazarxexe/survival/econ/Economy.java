
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
        ezcommands.add( new EzCommand(new Object () {
            private boolean execute(CommandSender sender, String commandLabel, String[] args) {

                if (!(sender instanceof Player)) return true;

                HashMap<String, String> temp = confighook;
                if (args.length < 2){
                    return false;
                }
                temp.put("payee", args[1]);
                temp.put("player", sender.asPlayer().getName());
                if (args[0].equalsIgnoreCase("get")) {
                    temp.put("amount", "0");
                }
                if (args.length > 3) {
                    temp.put("amount", args[3]);
                }
                switch (args[0]) {
                    case "add" -> { // ADD

                        if (!(sender.hasPermission("economy.add"))) {
                            sender.sendMessage( new StringSubstitutor(temp).replace("${NO_PERMISSIONS}") );
                            return true;
                        }
                        if (getServer().getPlayer(args[1]) == null){
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }
                        boolean success = api.add(getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));
                        if (!(success)) {
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }
                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_ADD")));
                    }
                    case "decrement" -> {
                        // DECREMENT
                        if (!(sender.hasPermission("economy.decrement"))) {
                            sender.sendMessage( new StringSubstitutor(temp).replace("${NO_PERMISSIONS}") );
                            return true;
                        }
                        if (getServer().getPlayer(args[1]) == null){
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }
                        boolean success = api.decrement(getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));

                        if (!(success)) {
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }
                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_DECREMENT")));
                    }
                    case "get" -> {
                        // GET
                        if (!(sender.hasPermission("economy.get"))) {
                            sender.sendMessage( new StringSubstitutor(confighook).replace("${NO_PERMISSIONS}") );
                            return true;
                        }
                        if (getServer().getPlayer(args[1]) == null){
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }
                        long bal = api.get(getServer().getPlayer(args[1]).getUniqueId());
                        temp.replace("amount", String.valueOf(bal));
                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_GET")));
                    }
                    case "set" -> {             // SET

                        if (!(sender.hasPermission("economy.set"))) {
                            sender.sendMessage( new StringSubstitutor(temp).replace("${NO_PERMISSIONS}") );
                            return true;
                        }

                        if (getServer().getPlayer(args[1]) == null){
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }

                        boolean success = api.set(getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));

                        if (!(success)) {
                            sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                            return true;
                        }

                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_SET")));
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

                    HashMap<String, String> temp = confighook;
                    temp.put("payee", sender.asPlayer().getName());
                    temp.put("amount", String.valueOf(api.get(sender.asPlayer().getUniqueId())));

                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_GET")));

                    return true;
                }
            };

        }).register(this) );

        ezcommands.add( new EzCommand(new Object() {
            String name = "pay";
            String desc = "Pay to player!";
            String usage = "/pay (playername) (amount)";
            CommandExecutable executable = new CommandExecutable() {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    if (!(sender instanceof Player)) return true;

                    if (args.length != 2){
                        return false;
                    }

                    HashMap<String, String> temp = confighook;
                    temp.put("payee", args[1]);
                    temp.put("player", sender.asPlayer().getName());
                    temp.put("amount", args[0]);

                    long payer = api.get(sender.asPlayer().getUniqueId());

                    if (!(Long.parseLong(args[1]) > 0L)) {
                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_MORE_THAN_ONE")));
                        return true;
                    }

                    if (payer > (Long.parseLong(args[1]))){
                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_NOT_ENOUGH")));
                        return true;
                    }


                    boolean send = api.decrement(sender.asPlayer().getUniqueId(), (Long.parseLong(args[1])));
                    if (!(send)){
                        failed(sender, temp);
                        return true;
                    }

                    if (getServer().getPlayer(args[0]) == null){
                        sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                        return true;
                    }
                    boolean receive = api.add(getServer().getPlayer(args[0]).getUniqueId() , (Long.parseLong(args[1])));
                    if (!(receive)){
                        failed(sender, temp);
                        api.add(sender.asPlayer().getUniqueId(), (Long.parseLong(args[1])));
                        return true;
                    }

                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_PAY")));
                    return true;
                }

                private void failed(CommandSender sender, HashMap<String, String> temp){
                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                }

            };
        }).register(this) );

    }







}

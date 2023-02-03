package me.nazarxexe.survival.econ.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import me.nazarxexe.survival.core.command.CommandExecutable;
import me.nazarxexe.survival.econ.Economy;
import me.nazarxexe.survival.econ.EconomyAPI;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class EconomyCommand {

    final Economy plugin;
    final HashMap<String, String> confighook;
    EconomyAPI api;


    public EconomyCommand(Economy plugin) {
        this.plugin = plugin;
        this.confighook = plugin.getConfighook();
        this.api = plugin.getApi();
    }

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
                if (plugin.getServer().getPlayer(args[1]) == null){
                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                    return true;
                }
                boolean success = api.add(plugin.getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));
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
                if (plugin.getServer().getPlayer(args[1]) == null){
                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                    return true;
                }
                boolean success = api.decrement(plugin.getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));

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
                if (plugin.getServer().getPlayer(args[1]) == null){
                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                    return true;
                }
                long bal = api.get(plugin.getServer().getPlayer(args[1]).getUniqueId());
                temp.replace("amount", String.valueOf(bal));
                sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("BAL_GET")));
            }
            case "set" -> {             // SET

                if (!(sender.hasPermission("economy.set"))) {
                    sender.sendMessage( new StringSubstitutor(temp).replace("${NO_PERMISSIONS}") );
                    return true;
                }

                if (plugin.getServer().getPlayer(args[1]) == null){
                    sender.sendMessage(new StringSubstitutor(temp).replace(confighook.get("TRANSACTION_ERROR")));
                    return true;
                }

                boolean success = api.set(plugin.getServer().getPlayer(args[1]).getUniqueId(), Long.parseLong(args[2]));

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

}

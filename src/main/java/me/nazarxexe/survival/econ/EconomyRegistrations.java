package me.nazarxexe.survival.econ;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class EconomyRegistrations implements Listener {

    final Economy plugin;
    final EconomyAPI api;


    public EconomyRegistrations(@NotNull Economy plugin) {
        this.plugin = plugin;
        this.api = plugin.getApi();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e){
        api.databaseToCached(e.getPlayer());
    }

    @EventHandler
    public void onLeft(@NotNull PlayerQuitEvent e){
        api.cachedToDatabase(e.getPlayer());
    }

}

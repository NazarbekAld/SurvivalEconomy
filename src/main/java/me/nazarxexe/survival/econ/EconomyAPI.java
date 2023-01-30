package me.nazarxexe.survival.econ;

import cn.nukkit.Player;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import me.nazarxexe.survival.core.economy.EconomyManager;
import me.nazarxexe.survival.core.economy.Pocket;
import me.nazarxexe.survival.core.tools.TerminalComponent;
import me.nazarxexe.survival.core.tools.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class EconomyAPI {

    private final Economy plugin;
    private final EconomyManager manager;



    private final Map<UUID, Pocket> cache;
    public Map<UUID, Pocket> getCache() {
        return cache;
    }

    public EconomyAPI(@NotNull Economy plugin){
        this.plugin = plugin;
        this.manager = plugin.getEconomyManager();
        this.cache = new HashMap<>();
    }


    public CompletableFuture<Boolean> isPlayerOnDatabase(@NotNull UUID player){
        return manager.loadPocket("econ", player).thenApplyAsync(Objects::nonNull);
    }
    public CompletableFuture<Boolean> isPlayerOnDatabase(@NotNull Player player){
        return manager.loadPocket("econ", player.getUniqueId()).thenApplyAsync(Objects::nonNull);
    }


    public void databaseToCached(@NotNull Player player){
        plugin.getServer().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
            @Override
            public void onRun() {
                manager.loadPocket("econ", player.getUniqueId()).thenAcceptAsync((pocket -> {
                    if (pocket == null){
                        cache.put(player.getUniqueId(), new Pocket("econ", player.getUniqueId()));
                        return;
                    }
                    cache.put(player.getUniqueId(), pocket);
                })).join();
            }
        });
    }

    public void cachedToDatabase(@NotNull Player player){
        if (cache.get(player.getUniqueId()) == null){
            new TerminalComponent(plugin.getLogger(), new TextComponent()
                    .combine(TextFormat.RED)
                    .combine("Cannot save ")
                    .combine(player.getUniqueId().toString())
                    .combine("! Because not exists on cache!")).warn();
            return;
        }
       manager.savePocket(cache.get(player.getUniqueId()));
    }

    public boolean add(@NotNull UUID payee, long balance){
        Pocket payeePocket = getPocket(payee);
        if (payeePocket == null){
            return false;
        }
        payeePocket.addBalance(balance);
        return true;

    }

    public boolean decrement(@NotNull UUID payer, long balance){
        Pocket payerPocket = getPocket(payer);
        if (payerPocket == null){
            return false;
        }
        payerPocket.decrementBalance(balance);
        return true;
    }

    public boolean set(@NotNull UUID player, long balance){
        Pocket playerPocket = getPocket(player);
        if (playerPocket == null){
            return false;
        }
        playerPocket.setBalance(balance);
        return true;

    }

    public long get(@NotNull UUID player){
        Pocket playerPocket = getPocket(player);
        if (playerPocket == null){
            return 0L;
        }
        return playerPocket.getBalance();

    }



    public @Nullable Pocket getPocket(@NotNull UUID player) {
        return cache.get(player);
    }





}

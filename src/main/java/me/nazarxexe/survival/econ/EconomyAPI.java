package me.nazarxexe.survival.econ;

import cn.nukkit.Player;
import cn.nukkit.scheduler.AsyncTask;
import me.nazarxexe.survival.core.economy.EconomyManager;
import me.nazarxexe.survival.core.economy.Pocket;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EconomyAPI {

    private final Economy plugin;
    private final EconomyManager manager;


    public Set<Pocket> getCache() {
        return cache;
    }

    private final Set<Pocket> cache;

    public EconomyAPI(@NotNull Economy plugin){
        this.plugin = plugin;
        this.manager = plugin.getEconomyManager();
        this.cache = new HashSet<>();
    }

    public boolean isPlayerOnCache(@NotNull Player player){
        AtomicBoolean res = new AtomicBoolean(false);
        cache.forEach((pocket -> {
            if (!(pocket.getOwner().equals(player.getUniqueId()))) return;
            res.set(true);
        }));
        return res.get();
    }

    public CompletableFuture<Boolean> isPlayerOnCacheAsync(@NotNull Player player){
        return CompletableFuture.supplyAsync(() -> {
            AtomicBoolean res = new AtomicBoolean(false);
            cache.forEach((pocket -> {
                if (!(pocket.getOwner().equals(player.getUniqueId()))) return;
                res.set(true);
            }));
            return res.get();
        });
    }

    public CompletableFuture<Boolean> isPlayerOnDatabase(@NotNull UUID player){
        return manager.loadPocket("econ", player).thenApplyAsync(Objects::nonNull);
    }
    public CompletableFuture<Boolean> isPlayerOnDatabase(@NotNull Player player){
        return manager.loadPocket("econ", player.getUniqueId()).thenApplyAsync(Objects::nonNull);
    }


    /*
        
     */
    public void databaseToCached(@NotNull Player player){
        plugin.getServer().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
            @Override
            public void onRun() {
                manager.loadPocket("econ", player.getUniqueId()).thenAcceptAsync((pocket -> {
                    if (pocket == null){
                        cache.add(new Pocket("econ", player.getUniqueId()));
                        return;
                    }
                    cache.add(pocket);
                })).join();
            }
        });
    }

    public void cachedToDatabase(@NotNull Player player){
        cache.forEach((pocket -> {
            if (pocket.getOwner().equals(player.getUniqueId())){
                manager.savePocket(pocket);
            }
        }));
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



    public Pocket getPocket(@NotNull UUID player) {
        AtomicReference<Pocket> G = new AtomicReference<>();
        cache.forEach((pocket -> {
            if (pocket.getOwner().equals(player)) {
                G.set(pocket);
            }
        }));
        return G.get();
    }





}

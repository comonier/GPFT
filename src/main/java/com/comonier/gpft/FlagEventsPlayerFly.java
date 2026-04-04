package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.UUID;

public class FlagEventsPlayerFly implements Listener {

    private final FlagManager flagManager;
    private final String FLY_META = "gpft_fly_active";
    private final HashMap<UUID, BukkitRunnable> activeCountdowns = new HashMap<>();

    public FlagEventsPlayerFly(FlagManager flagManager) {
        this.flagManager = flagManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (null == to) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        Player player = event.getPlayer();
        
        if (player.hasPermission("essentials.fly") || GameMode.CREATIVE == player.getGameMode() || GameMode.SPECTATOR == player.getGameMode()) {
            return;
        }

        Claim claim = flagManager.getClaimAt(to);

        if (null == claim) {
            if (player.hasMetadata(FLY_META)) {
                startFlyRemovalCountdown(player);
            }
            return;
        }

        boolean canFlyFlag = flagManager.getFlagState(claim, "visitor_fly");
        // Mudança crítica: Usar allowBuild em vez de allowAccess para evitar falso positivo em Container/Access Trust
        boolean hasBuildTrust = (null == claim.allowBuild(player, null));

        if (canFlyFlag && hasBuildTrust) {
            stopFlyRemovalCountdown(player);

            if (false == player.getAllowFlight()) {
                player.setAllowFlight(true);
                player.setMetadata(FLY_META, new FixedMetadataValue(Main.getInstance(), true));
                
                // Delay de 1 segundo (20 ticks) para não colidir com o Title de entrada do terreno
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline() && player.hasMetadata(FLY_META)) {
                            String titleOn = Main.getInstance().getMsgRaw("fly_on_title");
                            String subtitleOn = Main.getInstance().getMsgRaw("fly_on_subtitle");
                            player.sendTitle(titleOn, subtitleOn, 10, 40, 10);
                        }
                    }
                }.runTaskLater(Main.getInstance(), 20L);
            }
        } else {
            if (player.hasMetadata(FLY_META)) {
                if (false == player.getUniqueId().equals(claim.ownerID) && false == player.hasPermission("gpft.admin")) {
                    startFlyRemovalCountdown(player);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        removeFlyInstant(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        stopFlyRemovalCountdown(player);
        removeFlyInstant(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (PlayerTeleportEvent.TeleportCause.UNKNOWN == event.getCause()) return;
        Player player = event.getPlayer();
        if (player.hasPermission("essentials.fly") || player.hasPermission("gpft.admin")) return;
        removeFlyInstant(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        removeFlyInstant(event.getPlayer());
    }

    private void removeFlyInstant(Player player) {
        if (player.hasPermission("essentials.fly") || player.hasPermission("gpft.admin")) return;

        if (player.hasMetadata(FLY_META)) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.removeMetadata(FLY_META, Main.getInstance());
        }
    }

    private void startFlyRemovalCountdown(Player player) {
        if (activeCountdowns.containsKey(player.getUniqueId())) return;
        if (player.hasPermission("essentials.fly") || player.hasPermission("gpft.admin")) return;

        Main plugin = Main.getInstance();
        BukkitRunnable task = new BukkitRunnable() {
            int timer = 10;

            @Override
            public void run() {
                if (false == player.isOnline()) {
                    stopFlyRemovalCountdown(player);
                    return;
                }

                if (timer > 0) {
                    String title = plugin.getMsgRaw("fly_countdown_title").replace("{timer}", String.valueOf(timer));
                    String subtitle = plugin.getMsgRaw("fly_countdown_subtitle");
                    player.sendTitle(title, subtitle, 0, 25, 0);
                    timer--;
                } else {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.removeMetadata(FLY_META, plugin);
                    
                    String titleOff = plugin.getMsgRaw("fly_off_title");
                    String subtitleOff = plugin.getMsgRaw("fly_off_subtitle");
                    player.sendTitle(titleOff, subtitleOff, 0, 60, 20);
                    
                    stopFlyRemovalCountdown(player);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
        activeCountdowns.put(player.getUniqueId(), task);
    }

    private void stopFlyRemovalCountdown(Player player) {
        BukkitRunnable task = activeCountdowns.remove(player.getUniqueId());
        if (null != task) {
            task.cancel();
            player.sendTitle("", "", 0, 1, 0);
        }
    }
}

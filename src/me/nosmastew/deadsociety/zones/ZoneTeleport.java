package me.nosmastew.deadsociety.zones;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.api.events.PlayerTeleportZoneEvent;
import me.nosmastew.deadsociety.api.events.PlayerTeleportZonePrepareEvent;
import me.nosmastew.deadsociety.configuration.ZoneData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ZoneTeleport extends BukkitRunnable {

    private DeadSociety plugin;
    private Player player;
    private String loc;

    public ZoneTeleport(DeadSociety plugin, Player player, String loc) {
        this.plugin = plugin;
        this.player = player;
        this.loc = loc;

        PlayerTeleportZonePrepareEvent event = new PlayerTeleportZonePrepareEvent(player, (Location) ZoneData.getConfig(plugin, loc).get("zone.spawn"));
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void run() {
        PlayerTeleportZoneEvent event = new PlayerTeleportZoneEvent(player, (Location) ZoneData.getConfig(plugin, loc).get("zone.spawn"));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        player.teleport((Location) ZoneData.getConfig(plugin, loc).get("zone.spawn"));
    }
}
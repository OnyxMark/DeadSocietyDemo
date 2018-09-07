package me.nosmastew.deadsociety.zones;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.api.events.PlayerTeleportZoneEvent;
import me.nosmastew.deadsociety.api.events.PlayerTeleportZonePrepareEvent;
import me.nosmastew.deadsociety.utilities.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZoneTeleportHandlers implements Listener {

    private DeadSociety plugin;

    private List<UUID> control = new ArrayList<>();

    public ZoneTeleportHandlers(DeadSociety plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (control.contains(event.getPlayer().getUniqueId())) control.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onZoneTeleport(PlayerTeleportZonePrepareEvent event) {
        if (!plugin.getConfig().getBoolean("enable-zones-cooldown-teleportation")) {
            event.getPlayer().teleport(event.getZone());
            plugin.getTitle().sendTitle(event.getPlayer(), "", Lang.TELEPORTING.get(), 5, 40, 5);
            return;
        }
        if (control.contains(event.getPlayer().getUniqueId())) {
            return;
        }
        control.add(event.getPlayer().getUniqueId());
        plugin.getTitle().sendTitle(event.getPlayer(), "", Lang.TELEPORTING_COOLDOWN.get(), 5, 40, 5);
    }

    @EventHandler
    public void onZoneTeleport(PlayerTeleportZoneEvent event) {
        if (!control.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        plugin.getTitle().sendTitle(event.getPlayer(), "", Lang.TELEPORTING.get(), 5, 40, 5);
        control.remove(event.getPlayer().getUniqueId());
    }
}
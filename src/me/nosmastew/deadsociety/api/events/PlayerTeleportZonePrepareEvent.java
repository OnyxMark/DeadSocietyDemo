package me.nosmastew.deadsociety.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTeleportZonePrepareEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private Location location;

    public PlayerTeleportZonePrepareEvent(Player player, Location location) {
        super(player);
        this.location = location;
    }

    public Location getZone() {
        return this.location;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

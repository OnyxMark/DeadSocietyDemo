package me.nosmastew.deadsociety.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerQuitScreenEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private String diseaseType;
    private double progress;
    private String timeFormatted;

    public PlayerQuitScreenEvent(Player player, String diseaseType, double progress, String timeFormatted) {
        super(player);
        this.diseaseType = diseaseType;
        this.progress = progress;
        this.timeFormatted = timeFormatted;
    }

    public String getDiseaseType() {
        return this.diseaseType;
    }

    public double getProgress() {
        return this.progress;
    }

    public String getTimeFormatted() {
        return this.timeFormatted;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

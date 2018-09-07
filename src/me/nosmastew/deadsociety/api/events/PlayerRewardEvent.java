package me.nosmastew.deadsociety.api.events;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerRewardEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    protected double rewardAmount;
    private Economy economy;

    public PlayerRewardEvent(Economy economy, Player player, double rewardAmount) {
        super(player);
        this.rewardAmount = rewardAmount;
        this.cancel = false;
    }

    public void setRewardAmount(Double amount){
        economy.depositPlayer(player, amount);
    }

    public double getRewardAmount() {
        return this.rewardAmount;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
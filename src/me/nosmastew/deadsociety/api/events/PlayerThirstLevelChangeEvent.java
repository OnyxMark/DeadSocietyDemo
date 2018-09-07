package me.nosmastew.deadsociety.api.events;

import me.nosmastew.deadsociety.api.Message;
import me.nosmastew.deadsociety.api.MessageType;
import me.nosmastew.deadsociety.thirst.Thirst;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerThirstLevelChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private Thirst thirst;
    private BossBar bossBar;

    public PlayerThirstLevelChangeEvent(Player player, Thirst thirst, BossBar bossBar) {
        super(player);
        this.thirst = thirst;
        this.bossBar = bossBar;
        this.cancel = false;
    }

    public void setWarningMessage(MessageType type, String warningMessage) {
        new Message(player, type, warningMessage).send();
    }

    public boolean notValidate() {
        return this.thirst.notValidate(player);
    }

    public boolean isUnderSwimming() {
        return this.thirst.isUnderSwimming(player);
    }

    public int getThirst() {
        return this.thirst.getThirst(player);
    }

    public BossBar getBossBar(){
        return this.bossBar;
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

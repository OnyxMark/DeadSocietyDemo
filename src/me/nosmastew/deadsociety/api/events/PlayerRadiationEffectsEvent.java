package me.nosmastew.deadsociety.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerRadiationEffectsEvent extends PlayerEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private boolean cancel;

        public PlayerRadiationEffectsEvent(Player player) {
            super(player);
            this.cancel = false;
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
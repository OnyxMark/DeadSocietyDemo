package me.nosmastew.deadsociety.survival;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.Message;
import me.nosmastew.deadsociety.api.MessageType;
import me.nosmastew.deadsociety.api.events.PlayerRewardEvent;
import me.nosmastew.deadsociety.api.events.ZombieRewardEvent;
import me.nosmastew.deadsociety.utilities.DSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Rewards implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public Rewards(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onWalkersReward(EntityDeathEvent e) {
        if ((e.getEntity().getKiller() != null) && (e.getEntity().getKiller() instanceof Player) && (e.getEntity() instanceof Zombie)) {
            Player killer = e.getEntity().getKiller();
            if (manager.isDisabledInWorld(killer.getWorld())) return;

            if ((!plugin.setupEconomy()) || (!plugin.getConfig().getBoolean("kill-walker-money-reward.enable"))) {
                return;
            }
            double reward = plugin.getConfig().getDouble("kill-walker-money-reward.set-money-reward");

            ZombieRewardEvent killRewardEvent = new ZombieRewardEvent(plugin.econ(), killer, reward);
            Bukkit.getPluginManager().callEvent(killRewardEvent);
            if (killRewardEvent.isCancelled()) {
                return;
            }
            plugin.econ().depositPlayer(killer, reward);

            if (!plugin.getConfig().getBoolean("kill-walker-money-reward.reward-message.enable")) return;

            MessageType type = MessageType.valueOf(plugin.getConfig().getString("kill-walker-money-reward.reward-message.type"));
            String message = DSUtils.colour(plugin.getConfig().getString("kill-walker-money-reward.reward-message.message").replace("%reward%", Double.toString(reward)));
            new Message(killer, type, message).send();
        }
    }

    @EventHandler
    public void onPlayersReward(PlayerDeathEvent e) {
        if ((e.getEntity().getKiller() != null) && (e.getEntity().getKiller() instanceof Player)) {
            Player killer = e.getEntity().getKiller();
            if (manager.isDisabledInWorld(killer.getWorld())) return;

            if ((!plugin.setupEconomy()) || (!plugin.getConfig().getBoolean("kill-player-money-reward.enable"))) {
                return;
            }
            if (killer.getName().equals(e.getEntity().getName())) return;

            double reward = plugin.getConfig().getDouble("kill-player-money-reward.set-money-reward");

            PlayerRewardEvent killRewardEvent = new PlayerRewardEvent(plugin.econ(), killer, reward);
            Bukkit.getPluginManager().callEvent(killRewardEvent);
            if (killRewardEvent.isCancelled()) {
                return;
            }
            plugin.econ().depositPlayer(killer, reward);

            if (!plugin.getConfig().getBoolean("kill-player-money-reward.reward-message.enable")) return;

            MessageType type = MessageType.valueOf(plugin.getConfig().getString("kill-player-money-reward.reward-message.type"));
            String message = DSUtils.colour(plugin.getConfig().getString("kill-player-money-reward.reward-message.message").replace("%reward%", Double.toString(reward)));
            new Message(killer, type, message).send();
        }
    }
}
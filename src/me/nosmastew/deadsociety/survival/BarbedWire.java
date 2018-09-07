package me.nosmastew.deadsociety.survival;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.utilities.DSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BarbedWire implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    private List<UUID> control = new ArrayList<>();

    public BarbedWire(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onBarbedWire(PlayerMoveEvent event) {
        if (!isInBarbedWire(event.getPlayer())) {
            return;
        }
        Player player = event.getPlayer();
        if (manager.isDisabledInWorld(player.getWorld()) || control.contains(player.getUniqueId())) {
            return;
        }
        control.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> control.remove(player.getUniqueId()), 20);

        player.damage(plugin.getConfig().getDouble("barbed-wire-damage"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if ((!isInBarbedWire(event.getEntity())) || !plugin.getConfig().getBoolean("custom-death-messages")) {
            return;
        }
        if (isLivingEntity(event.getEntity()) || manager.isDisabledInWorld(event.getEntity().getWorld())) return;

        String deathMessage = DSUtils.colour(plugin.getConfig().getString("death-messages.barbed-wire").replace("%player%", event.getEntity().getName()));
        event.setDeathMessage(deathMessage);
    }

    private boolean isInBarbedWire(Player player) {
        return (player.getLocation().getBlock() != null) && (player.getLocation().getBlock().getType() == Material.WEB) || (player.getEyeLocation().getBlock().getType() == Material.WEB);
    }

    private boolean isLivingEntity(Player player) {
        return (player.getLastDamageCause() != null) && (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) && (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof LivingEntity);
    }
}
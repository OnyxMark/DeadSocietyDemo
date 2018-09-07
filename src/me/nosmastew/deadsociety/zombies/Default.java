package me.nosmastew.deadsociety.zombies;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Default implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public Default(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onDoorsBreak(EntityBreakDoorEvent event) {
        if ((event.getEntity().getType() == EntityType.ZOMBIE) && (!plugin.getConfig().getBoolean("walkers-can-break-wooden-doors"))) {
            if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE) {
            if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getDamager();
            if (manager.isDisabledInWorld(zombie.getWorld())) return;
            zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(plugin.getConfig().getDouble("walkers-default-strength"));
        }
    }

    @EventHandler
    public void onSpeed(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE) {
            if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;
            event.getEntity().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(plugin.getConfig().getDouble("walkers-default-movement-speed"));
        }
    }

    @EventHandler
    public void onLeash(PlayerInteractEntityEvent event) {
        if ((event.getHand() != EquipmentSlot.OFF_HAND) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.LEASH)) {
            if ((event.getRightClicked() != null) && (event.getRightClicked().getType() == EntityType.ZOMBIE)) {
                Zombie zombie = (Zombie) event.getRightClicked();
                if (manager.isDisabledInWorld(zombie.getWorld()) || zombie.isLeashed()) return;
                Player player = event.getPlayer();

                Bukkit.getScheduler().runTaskLater(plugin, () -> zombie.setLeashHolder(player), 1L);
                Item.removeItem(player);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if ((!isWalker(event.getEntity())) || !plugin.getConfig().getBoolean("custom-death-messages")) {
            return;
        }
        if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;

        String deathMessage = DSUtils.colour(plugin.getConfig().getString("death-messages.walker").replace("%player%", event.getEntity().getName()));
        event.setDeathMessage(deathMessage);
    }

    private boolean isWalker(Player player) {
        return (player.getLastDamageCause() != null) && (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) && (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof Zombie);
    }
}
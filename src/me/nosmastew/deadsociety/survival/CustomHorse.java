package me.nosmastew.deadsociety.survival;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class CustomHorse implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public CustomHorse(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onHorseSpawn(CreatureSpawnEvent event) {
        if ((event.getEntity().getType() == EntityType.HORSE) && (plugin.getConfig().getBoolean("spawn-custom-horse"))) {
            Horse horse = (Horse) event.getEntity();
            if (manager.isDisabledInWorld(horse.getWorld())) return;
            if (!horse.isAdult()) {
                event.setCancelled(true);
            }
            horse.setTamed(true);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        }
    }

    @EventHandler
    public void onHorseInventoryClick(InventoryClickEvent event) {
        if ((event.getInventory() instanceof HorseInventory) && (plugin.getConfig().getBoolean("spawn-custom-horse"))) {
            Horse horse = (Horse) event.getView().getTopInventory().getHolder();
            if (manager.isDisabledInWorld(horse.getWorld())) return;
            if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType() != Material.SADDLE)) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHorseDeath(EntityDeathEvent event) {
        if ((event.getEntity().getType() == EntityType.HORSE) && (plugin.getConfig().getBoolean("spawn-custom-horse"))) {
            if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;
            event.getDrops().clear();
        }
    }
}

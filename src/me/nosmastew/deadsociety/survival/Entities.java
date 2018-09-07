package me.nosmastew.deadsociety.survival;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class Entities implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public Entities(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onDroppedExp(EntityDeathEvent e) {
        if (manager.isDisabledInWorld(e.getEntity().getWorld()) || plugin.getConfig().getBoolean("entities-can-drop-experience-points")) {
            return;
        }
        e.setDroppedExp(0);
    }

    @EventHandler
    public void onCreatureSpawnCancellation(CreatureSpawnEvent e) {
        List<String> entities = plugin.getConfig().getStringList("block-entities.entities");
        if ((plugin.getConfig().getBoolean("block-entities.enable")) && (entities.contains(e.getEntity().getType().toString()))) {
            if (manager.isDisabledInWorld(e.getEntity().getWorld())) return;
            e.setCancelled(true);
        }
    }
}
package me.nosmastew.deadsociety.survival;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Doors implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    private List<UUID> control = new ArrayList<>();

    public Doors(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIronDoor(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || control.contains(e.getPlayer().getUniqueId())) return;

        Block block = e.getClickedBlock();
        if (block.getType() != Material.IRON_DOOR_BLOCK || manager.isDisabledInWorld(e.getPlayer().getWorld())) return;
        e.setCancelled(true);
        control.add(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTask(plugin, () -> control.remove(e.getPlayer().getUniqueId()));
        if (e.getPlayer().hasPermission(Permission.IRON_DOOR.get()) || e.getPlayer().hasPermission(Permission.OP.get())) {
            if (block.getData() >= 8) {
                block = block.getRelative(BlockFace.DOWN);
            }
            if (block.getData() < 4) {
                block.setData((byte) (block.getData() + 4));
                block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
                return;
            }
            block.setData((byte) (block.getData() - 4));
            block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
        }
    }
}

package me.nosmastew.deadsociety.survival;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Blocks implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public Blocks(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        List<String> blocksList = plugin.getConfig().getStringList("cancel-certain-blocks-from-opening.blocks");
        if ((!blocksList.contains(event.getClickedBlock().getType().toString())) || !plugin.getConfig().getBoolean("cancel-certain-blocks-from-opening.enable")) {
            return;
        }
        Player player = event.getPlayer();
        if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;
        String block = event.getClickedBlock().getType().toString().replace("_", "");
        if ((player.hasPermission("ds.place." + block) || player.hasPermission(Permission.BLOCK_OPEN.get()) || player.hasPermission(Permission.OP.get()))) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlacement(BlockPlaceEvent event) {
        List<String> blocksList = plugin.getConfig().getStringList("allow-certain-blocks-placement.blocks");
        if ((blocksList.contains(event.getBlockPlaced().getType().toString()) || !plugin.getConfig().getBoolean("allow-certain-blocks-placement.enable"))) {
            return;
        }
        Player player = event.getPlayer();
        if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;
        String block = event.getBlockPlaced().getType().toString().replace("_", "");
        if ((player.hasPermission("ds.place." + block) || player.hasPermission(Permission.BLOCK_PLACE.get()) || player.hasPermission(Permission.OP.get()))) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        List<String> blocksList = plugin.getConfig().getStringList("allow-certain-blocks-from-breaking.blocks");
        if ((blocksList.contains(event.getBlock().getType().toString()) || !plugin.getConfig().getBoolean("allow-certain-blocks-from-breaking.enable"))) {
            return;
        }
        Player player = event.getPlayer();
        if (manager.isDisabledInWorld(player.getWorld())) return;
        String block = event.getBlock().getType().toString().toLowerCase().replace("_", "");
        if ((player.hasPermission("ds.break." + block) || player.hasPermission(Permission.BLOCK_BREAK.get()) || player.hasPermission(Permission.OP.get()))) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }
}
package me.nosmastew.deadsociety.items.types;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.items.ItemType;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Flashlight implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    private List<UUID> effect = new ArrayList<>();

    public Flashlight(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onFlashlight(PlayerInteractEvent e) {
        if ((e.getHand() != EquipmentSlot.OFF_HAND) && (Item.hasItem(e.getPlayer(), ItemType.FLASHLIGHT))) {
            if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                Player player = e.getPlayer();
                if (manager.isDisabledInWorld(player.getWorld())) return;
                if ((player.hasPermission(Permission.FLASHLIGHT.get())) || (player.hasPermission(Permission.ITEMS_OP.get())) || (player.hasPermission(Permission.OP.get()))) {
                    if (!effect.contains(player.getUniqueId())) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 5));
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 2, 4);
                        effect.add(player.getUniqueId());
                        return;
                    }
                    if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1, 4);
                    effect.remove(player.getUniqueId());
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (effect.contains(e.getEntity().getUniqueId())) effect.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent e) {
        if (effect.contains(e.getPlayer().getUniqueId())) removeEffect(e.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (effect.contains(e.getPlayer().getUniqueId())) removeEffect(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (effect.contains(e.getPlayer().getUniqueId())) removeEffect(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (effect.contains(e.getWhoClicked().getUniqueId())) removeEffect((Player) e.getWhoClicked());
    }

    private void removeEffect(Player player) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1, 4);
        effect.remove(player.getUniqueId());
    }
}
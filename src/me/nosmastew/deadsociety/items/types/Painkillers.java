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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Painkillers implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public Painkillers(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onPainkillers(PlayerInteractEvent e) {
        if ((e.getHand() != EquipmentSlot.OFF_HAND) && (Item.hasItem(e.getPlayer(), ItemType.PAINKILLERS))) {
            if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                Player player = e.getPlayer();
                if (manager.isDisabledInWorld(player.getWorld())) return;
                if ((player.hasPermission(Permission.PAINKILLERS.get())) || (player.hasPermission(Permission.ITEMS_OP.get())) || (player.hasPermission(Permission.OP.get()))) {
                    if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) return;
                    Item.removeItem(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 410, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 410, 2));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 2, 1);
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            }
        }
    }
}
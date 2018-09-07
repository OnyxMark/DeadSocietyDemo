package me.nosmastew.deadsociety.items.types;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.diseases.Disease;
import me.nosmastew.deadsociety.diseases.DiseaseType;
import me.nosmastew.deadsociety.items.ItemType;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Antibiotics implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Disease disease;

    private Set<UUID> control = new HashSet<>();

    public Antibiotics(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.disease = plugin.getDisease();
    }

    @EventHandler
    public void onItem(PlayerInteractEvent e) {
        if ((e.getHand() != EquipmentSlot.OFF_HAND) && (Item.hasItem(e.getPlayer(), ItemType.ANTIBIOTICS))) {
            if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                Player player = e.getPlayer();
                if (control.contains(player.getUniqueId()) || manager.isDisabledInWorld(player.getWorld())) return;
                control.add(player.getUniqueId());

                Bukkit.getScheduler().runTaskLater(plugin, () -> control.remove(player.getUniqueId()), 30);
                if ((player.hasPermission(Permission.ANTIBIOTICS.get())) || (player.hasPermission(Permission.ITEMS_OP.get())) || (player.hasPermission(Permission.OP.get()))) {
                    if (disease.getDiseaseType(player) == null) {
                        player.sendMessage(Lang.NOT_INFECTED.get());
                        return;
                    }
                    if (disease.getDiseaseType(player) == DiseaseType.NECK) {
                        Item.removeItem(player);

                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 2, 1);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
                        player.sendMessage(Lang.ANTIBIOTICS_CURE.get());

                        plugin.getScreen().remove(player);
                    } else {
                        player.sendMessage(Lang.ANTIBIOTICS_WARNING.get());
                    }
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            }
        }
    }
}
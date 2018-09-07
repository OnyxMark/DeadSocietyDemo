package me.nosmastew.deadsociety.items.types;


import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.diseases.Disease;
import me.nosmastew.deadsociety.diseases.DiseaseType;
import me.nosmastew.deadsociety.items.ItemType;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Knife implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Disease disease;

    public Knife(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.disease = plugin.getDisease();
    }

    @EventHandler
    public void onKnife(PlayerInteractEvent e) {
        if ((e.getHand() != EquipmentSlot.OFF_HAND) && (Item.hasItem(e.getPlayer(), ItemType.KNIFE) && e.getPlayer().isSneaking())) {
            if ((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) return;
            Player player = e.getPlayer();
            if (disease.getDiseaseType(player) == null || manager.isDisabledInWorld(player.getWorld())) return;
            if ((player.hasPermission(Permission.KNIFE.get())) || (player.hasPermission(Permission.ITEMS_OP.get())) || (player.hasPermission(Permission.OP.get()))) {
                if ((disease.getDiseaseType(player) != DiseaseType.LEG)) return;

                double selfDamage = plugin.getConfig().getDouble("custom-items.knife.item-self-damage");
                if (player.getHealth() <= selfDamage) {
                    player.sendMessage(Lang.KNIFE_NOT_ENOUGH_HEALTH.get());
                    return;
                }
                plugin.getScreen().remove(player);

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 2, 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));

                player.sendMessage(Lang.KNIFE_CURE.get());
                player.damage(selfDamage);

                String title = DSUtils.colour(plugin.getConfig().getString("set-bleeding-screen-title"));
                Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getScreen().create(player, DiseaseType.BLEEDING, title, 0.025, 1, 40, 40000), 20L);
            } else {
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        }
    }

    @EventHandler
    public void onKnifeDamage(EntityDamageByEntityEvent event) {
        if ((event.getDamager().getType() != EntityType.PLAYER) || (!Item.hasItem((Player) event.getDamager(), ItemType.KNIFE))) {
            return;
        }
        event.setDamage(plugin.getConfig().getInt("custom-items.knife.item-damage"));
    }
}
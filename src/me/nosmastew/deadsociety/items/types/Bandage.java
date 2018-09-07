package me.nosmastew.deadsociety.items.types;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.Message;
import me.nosmastew.deadsociety.api.MessageType;
import me.nosmastew.deadsociety.diseases.Disease;
import me.nosmastew.deadsociety.diseases.DiseaseType;
import me.nosmastew.deadsociety.items.ItemType;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Bandage implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Disease disease;

    private Set<UUID> control = new HashSet<>();

    public Bandage(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.disease = plugin.getDisease();
    }

    @EventHandler
    public void onBandage(PlayerInteractEvent event) {
        if ((event.getHand() == EquipmentSlot.OFF_HAND) || (!Item.hasItem(event.getPlayer(), ItemType.BANDAGE))) return;

        if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();

            if (manager.isDisabledInWorld(player.getWorld())) return;

            if ((!player.hasPermission(Permission.BANDAGE.get())) && (!player.hasPermission(Permission.ITEMS_OP.get())) && (!player.hasPermission(Permission.OP.get()))) {
                player.sendMessage(Lang.NO_PERMISSION.get());
                return;
            }
            if (disease.hasDisease(player) && disease.getDiseaseType(player) != DiseaseType.BLEEDING){
                player.sendMessage(Lang.DYING_FROM_INFECTION.get());
                return;
            }
            if (control.contains(player.getUniqueId())) return;
            control.add(player.getUniqueId());

            Block block = player.getLocation().getBlock();
            long start = System.currentTimeMillis();
            new BukkitRunnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis() - start;
                    if (time > 5000) {
                        Item.removeItem(player);
                        double health = plugin.getConfig().getDouble("custom-items.bandage.item-heal");

                        control.remove(player.getUniqueId());
                        if (plugin.getScreen().hasScreen(player)) {
                            plugin.getScreen().remove(player);
                            player.sendMessage(Lang.BANDAGE_BLEEDING_HEAL.get());
                        }
                        player.setHealth(player.getHealth() + health >= player.getMaxHealth() ? player.getMaxHealth() : player.getHealth() + health);
                        cancel();
                        return;
                    }

                    if (!player.isSneaking() || !block.equals(player.getLocation().getBlock()) || !Item.hasItem(event.getPlayer(), ItemType.BANDAGE)) {
                        control.remove(player.getUniqueId());
                        cancel();
                        return;
                    }

                    String defaultSound = "custom-items.bandage.item-sound";
                    if (plugin.getConfig().getString(defaultSound) != null) {
                        String[] sound = plugin.getConfig().getString(defaultSound).split("-");
                        player.getWorld().playSound(player.getLocation(), Sound.valueOf(sound[0]), Integer.parseInt(sound[1]), Integer.parseInt(sound[2]));
                    }
                    String customSound = "custom-items.bandage.item-custom-sound";
                    if (plugin.getConfig().getString(customSound) != null) {
                        player.getWorld().playSound(player.getLocation(), plugin.getConfig().getString(customSound), 1, 1);
                    }
                    String timeFormatted = DSUtils.format(5 - (((double) time) / 1000));

                    MessageType type = MessageType.valueOf(plugin.getConfig().getString("custom-items.bandage.item-message-type"));
                    new Message(player, type, DSUtils.colour("&2&l" + timeFormatted)).send();
                }
            }.runTaskTimer(plugin, 1L, 1L);
        }
    }
}
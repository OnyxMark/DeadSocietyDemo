package me.nosmastew.deadsociety.items.types;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.diseases.Disease;
import me.nosmastew.deadsociety.diseases.DiseaseType;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class InfectedBottle implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Disease disease;

    public InfectedBottle(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.disease = plugin.getDisease();
    }

    @EventHandler
    public void onWaterInfection(PlayerItemConsumeEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().isSimilar(Item.infectedBottle())) {
            Player player = event.getPlayer();
            if ((player.hasPermission(Permission.INFECTION_IMMUNE.get())) || (player.hasPermission(Permission.OP.get()))) {
                return;
            }

            if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;

            disease.setDisease(player, DiseaseType.WATER);

            String[] b = UserData.getConfig(plugin, player).getString("disease." + player.getWorld().getName()).split("-");

            String title = DSUtils.colour(plugin.getConfig().getString("set-infection-screen-title"));
            plugin.getScreen().create(player, DiseaseType.valueOf(b[0]), title);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onCauldronBottles(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType() == Material.CAULDRON)) {
            if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;
            if ((event.getPlayer().hasPermission(Permission.WATER_BOTTLE.get())) || (event.getPlayer().hasPermission(Permission.OP.get()))) {
                Player player = event.getPlayer();
                int metadata = event.getClickedBlock().getData();
                ItemStack hand = player.getInventory().getItemInMainHand();
                if ((metadata != 0) && (hand.getType() == Material.GLASS_BOTTLE) || (metadata != 0) && (hand.isSimilar(Item.infectedBottle()))) {
                    event.setCancelled(true);
                    if (hand.getAmount() <= 1) {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.POTION, 1, (short) 0));
                        return;
                    }
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(Lang.NOT_ENOUGH_SPACE.get());
                        return;
                    }
                    hand.setAmount(hand.getAmount() - 1);
                    player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 0));
                }
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInfectedWaterBottles(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (!plugin.getConfig().getBoolean("enable-infected-water-bottles")) return;
                if (event.getClickedBlock() == null || event.getClickedBlock().getRelative(event.getBlockFace()) == null) {
                    return;
                }
                Block b = event.getClickedBlock().getRelative(event.getBlockFace());
                if ((b.getType() != Material.STATIONARY_WATER) && (b.getType() != Material.WATER)) {
                    return;
                }
                if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;
                event.setCancelled(true);

                Player player = event.getPlayer();
                ItemStack hand = player.getInventory().getItemInMainHand();
                ItemStack bottle = Item.infectedBottle();
                bottle.setAmount(1);
                if (hand.getAmount() <= 1) {
                    player.getInventory().setItemInMainHand(bottle);
                    return;
                }
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Lang.NOT_ENOUGH_SPACE.get());
                }
                hand.setAmount(hand.getAmount() - 1);
                player.getInventory().addItem(bottle);
            }
        }
    }
}
package me.nosmastew.deadsociety.items.types;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.ItemBuilder;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.configuration.ZoneData;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Compass implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    private final int[] slots = {36, 37, 38, 39, 41, 42, 43, 44};

    public Compass(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onCompass(PlayerInteractEvent event) {
        if ((event.getHand() != EquipmentSlot.OFF_HAND) && (event.getPlayer().getInventory().getItemInMainHand() != null)) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.COMPASS) {
                return;
            }
            if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                Player player = event.getPlayer();
                if (manager.isDisabledInWorld(player.getWorld())) return;

                if (player.hasPermission(Permission.COMPASS.get()) || player.hasPermission(Permission.TRACK_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    event.setCancelled(true);

                    Inventory inv = Bukkit.createInventory(null, 45, Lang.TRACK_COMPASS_TITLE.get());

                    ItemStack camp = new ItemBuilder(Material.WOOL).setAmount(1).setDurability((short) 14).setName(ChatColor.GRAY + "Camp").build();
                    for (int slot : slots) {
                        inv.setItem(slot, Item.GUI_GLASS);
                    }
                    inv.setItem(40, camp);

                    File directory = new File(plugin.getDataFolder(), "zones");
                    if (!directory.exists() || (directory.listFiles().length == 0) || directory.listFiles() == null) {
                        player.openInventory(inv);
                        return;
                    }

                    File[] listFiles = directory.listFiles();

                    for (File zones : listFiles) {
                        String zone = zones.getName().toLowerCase().replace(".yml", "");

                        ZoneData data = ZoneData.getConfig(plugin, zone);
                        Location loc = (Location) data.get("zone.spawn");

                        if ((data.getString("zone.spawn") != null) && (loc.getWorld().equals(player.getWorld()))) {
                            String type = data.getString("zone.type");
                            ItemStack z = new ItemBuilder(Material.COMPASS).setAmount(1).setName(ChatColor.GRAY + zone).setLore(new String[]{ChatColor.DARK_GRAY + type}).build();

                            inv.addItem(z);
                        }
                    }
                    player.openInventory(inv);
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            }
        }
    }

    @EventHandler
    public void onDefaultInventory(InventoryClickEvent event) {
        if ((event.getInventory() != null) && (event.getInventory().getName().equals(Lang.TRACK_COMPASS_TITLE.get()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        if ((event.getClickedInventory() != null) && (event.getClickedInventory().getName().equals(Lang.TRACK_COMPASS_TITLE.get()))) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if ((event.getClick().isShiftClick() || (item == null) || (item.getType() == Material.AIR) || item.getType() == Material.STAINED_GLASS_PANE)) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            String track = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            player.closeInventory();

            if (track.equals("Camp")) {
                if (player.hasPermission(Permission.TRACK_CAMP.get()) || player.hasPermission(Permission.TRACK_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    UserData data = UserData.getConfig(plugin, player);
                    if (data.getString("camps." + player.getWorld().getName()) == null) {
                        player.sendMessage(Lang.INVALID_CAMP_LOCATION.get());
                        return;
                    }
                    player.setCompassTarget((Location) data.get("camps." + player.getWorld().getName()));
                    plugin.sendActionBar(player, Lang.TRACKING.get() + ChatColor.AQUA + " Camp");
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
                return;
            }
            if (player.hasPermission(Permission.TRACK_ZONE.get()) || player.hasPermission(Permission.TRACK_OP.get()) || player.hasPermission(Permission.OP.get())) {
                ZoneData data = ZoneData.getConfig(plugin, track);

                player.setCompassTarget((Location) data.get("zone.spawn"));
                plugin.sendActionBar(player, Lang.TRACKING.get() + ChatColor.AQUA + " " + track);
            } else {
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        }
    }
}
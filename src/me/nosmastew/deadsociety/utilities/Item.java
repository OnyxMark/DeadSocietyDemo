package me.nosmastew.deadsociety.utilities;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.api.ItemBuilder;
import me.nosmastew.deadsociety.items.ItemType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class Item {

    private static DeadSociety plugin;
    private static boolean initialized = false;

    public static void initialize(DeadSociety plugin) {
        if (!initialized) {
            Item.plugin = plugin;
            initialized = true;
        } else {
            throw new IllegalStateException("Plugin was already initialized!");
        }
    }

    private static FileConfiguration config() {
        return plugin.getConfig();
    }

    public static final ItemStack GUI_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE)
            .setAmount(1)
            .setDurability((short) 8)
            .setName(" ")
            .build();

    public static ItemStack perk() {
        ItemStack perk = new ItemBuilder(Material.CLAY_BALL).setAmount(1).setLore(new String[]{"DSPerks"}).build();
        ItemMeta meta = perk.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        perk.setItemMeta(meta);
        return perk;
    }

    public static ItemStack getItem(ItemType item) {
        return new ItemBuilder(Material.getMaterial(config().getString(item.get() + ".item-type")))
                .setDurability((short) config().getInt(item.get() + ".item-data"))
                .setName(config().getString(item.get() + ".item-name"))
                .setLore(config().getString(item.get() + ".item-lore").split("\\|"))
                .build();
    }

    public static ItemStack getItem(ItemType item, int amount) {
        return new ItemBuilder(Material.getMaterial(config().getString(item.get() + ".item-type")))
                .setDurability((short) config().getInt(item.get() + ".item-data"))
                .setAmount(amount)
                .setName(config().getString(item.get() + ".item-name"))
                .setLore(config().getString(item.get() + ".item-lore").split("\\|"))
                .build();
    }

    public static ItemStack infectedBottle() {
        ItemStack bottle = new ItemBuilder(Material.POTION).setName(Lang.INFECTED_WATER_BOTTLE.get()).setLore(new String[]{Lang.INFECTED_WATER_BOTTLE_EFFECTS.get()}).build();
        PotionMeta meta = (PotionMeta) bottle.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setBasePotionData(new PotionData(PotionType.WATER));
        bottle.setItemMeta(meta);
        return bottle;
    }

    public static void removeItem(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if ((hand != null) && (hand.getAmount() >= 1) && (player.getGameMode() != GameMode.CREATIVE)) {
            hand.setAmount(hand.getAmount() - 1);
        }
    }

    public static boolean hasItem(Player player, Material type, String name) {
        return notNull(player.getInventory()) && isItem(player.getInventory(), type, name);
    }

    public static boolean hasItem(Player player, ItemType item) {
        return notNull(player.getInventory()) && isItem(player.getInventory(), item);
    }

    public static boolean hasItem(Inventory inv, ItemType item) {
        return notNull(inv.getItem(0)) && isItem(inv.getItem(0), item);
    }

    private static boolean notNull(PlayerInventory inventory) {
        ItemStack hand = inventory.getItemInMainHand();
        return hand != null && hand.getType() != null && hand.hasItemMeta() && hand.getItemMeta() != null && hand.getItemMeta().getDisplayName() != null;
    }

    private static boolean notNull(ItemStack i) {
        return i != null && i.getType() != null && i.hasItemMeta() && i.getItemMeta() != null && i.getItemMeta().getDisplayName() != null;
    }

    private static boolean isItem(PlayerInventory inventory, Material type, String name) {
        return (inventory.getItemInMainHand().getType() == type) && (inventory.getItemInMainHand().getItemMeta().getDisplayName().equals(DSUtils.colour(name)));
    }

    private static boolean isItem(PlayerInventory inventory, ItemType item) {
        return (inventory.getItemInMainHand().getType() == getMaterial(item) && (inventory.getItemInMainHand().getItemMeta().getDisplayName().equals(getDisplayName(item))));
    }

    private static boolean isItem(ItemStack i, ItemType item) {
        return (i.getType() == getMaterial(item) && (i.getItemMeta().getDisplayName().equals(getDisplayName(item))));
    }

    public static Material getMaterial(ItemType item) {
        return Material.getMaterial(config().getString(item.get() +".item-type"));
    }

    public static String getDisplayName(ItemType item) {
        return DSUtils.colour(config().getString(item.get() + ".item-name"));
    }

    public static boolean getArmour(PlayerInventory inventory) {
        return inventory != null && inventory.getHelmet() != null && inventory.getChestplate() != null && inventory.getLeggings() != null && inventory.getBoots() != null;
    }
}
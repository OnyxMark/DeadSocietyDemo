package me.nosmastew.deadsociety.diseases;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.utilities.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Disease {

    private DeadSociety plugin;

    private Map<UUID, DiseaseType> disease = new HashMap<>();

    public Disease(DeadSociety plugin) {
        this.plugin = plugin;
    }


    public DiseaseType getDiseaseType(Player player) {
        return disease.get(player.getUniqueId());
    }

    public boolean hasDisease(Player player) {
        return disease.containsKey(player.getUniqueId()) || UserData.getConfig(plugin, player).getString("disease." + player.getWorld().getName()) != null;
    }

    public boolean hasDisease(Player player, DiseaseType type) {
        String data = UserData.getConfig(plugin, player).getString("disease." + player.getWorld().getName());

        return data != null && data.split("-")[0].equals(type.get());
    }

    public void setDisease(Player player, DiseaseType diseaseType) {
        disease.put(player.getUniqueId(), diseaseType);
    }

    public void removeDisease(Player player) {
        disease.remove(player.getUniqueId());
    }

    public boolean hasProtection(Player player) {
        PlayerInventory inventory = player.getInventory();
        return Item.getArmour(inventory) && isHelmet(inventory) && isChestplate(inventory) && isLeggings(inventory) && isBoots(inventory);
    }

    private boolean isHelmet(PlayerInventory inventory) {
        return inventory.getHelmet().getType() == Material.IRON_HELMET || inventory.getHelmet().getType() == Material.DIAMOND_HELMET;
    }

    private boolean isChestplate(PlayerInventory inventory) {
        return inventory.getChestplate().getType() == Material.IRON_CHESTPLATE || inventory.getChestplate().getType() == Material.DIAMOND_CHESTPLATE;
    }

    private boolean isLeggings(PlayerInventory inventory) {
        return inventory.getLeggings().getType() == Material.IRON_LEGGINGS || inventory.getLeggings().getType() == Material.DIAMOND_LEGGINGS;
    }

    private boolean isBoots(PlayerInventory inventory) {
        return inventory.getBoots().getType() == Material.IRON_BOOTS || inventory.getBoots().getType() == Material.DIAMOND_BOOTS;
    }
}

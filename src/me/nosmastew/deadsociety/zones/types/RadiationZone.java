package me.nosmastew.deadsociety.zones.types;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.Message;
import me.nosmastew.deadsociety.api.MessageType;
import me.nosmastew.deadsociety.api.events.PlayerRadiationEffectsEvent;
import me.nosmastew.deadsociety.configuration.ZoneData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RadiationZone implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;

    private List<UUID> control = new ArrayList<>();
    private List<UUID> wRegion = new ArrayList<>();

    public RadiationZone(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onZone(PlayerMoveEvent event) {
        if (plugin.getWorldGuard() == null || manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        RegionManager regionManager = plugin.getWorldGuard().getRegionManager(player.getWorld());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(player.getLocation());
        if (control.contains(player.getUniqueId())) return;

        control.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> control.remove(player.getUniqueId()), 10);

        if (regions.size() == 0 && wRegion.contains(player.getUniqueId())) {
            wRegion.remove(player.getUniqueId());

            player.removePotionEffect(PotionEffectType.WITHER);
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.removePotionEffect(PotionEffectType.HUNGER);

            if (!plugin.getConfig().getBoolean("radiation-zone-leaving-message.enable")) return;

            MessageType type = MessageType.valueOf(plugin.getConfig().getString("radiation-zone-leaving-message.type"));
            String message = DSUtils.colour(plugin.getConfig().getString("radiation-zone-leaving-message.message"));
            new Message(player, type, message).send();
            return;
        }

        for (ProtectedRegion region : regions) {
            File file = new File(plugin.getDataFolder(), "zones" + File.separator + region.getId() + ".yml");
            if (!file.exists() || region == null || region.getId() == null) {
                return;
            }
            ZoneData data = ZoneData.getConfig(plugin, region.getId());
            if (data == null || data.getString("zone.type") == null || !data.getString("zone.type").equals("radiation-zone")) {
                return;
            }

            if (!wRegion.contains(player.getUniqueId())) {
                wRegion.add(player.getUniqueId());
                if (!plugin.getConfig().getBoolean("radiation-zone-entering-message.enable")) {
                    return;
                }
                MessageType type = MessageType.valueOf(plugin.getConfig().getString("radiation-zone-entering-message.type"));
                String message = DSUtils.colour(plugin.getConfig().getString("radiation-zone-entering-message.message"));
                new Message(player, type, message).send();
            }
            if (hasArmour(player, Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS)) {
                return;
            }

            if (player.hasPermission(Permission.ZONE_IMMUNE.get()) || player.hasPermission(Permission.OP.get())) {
                return;
            }

            PlayerRadiationEffectsEvent radiationEffectsEvent = new PlayerRadiationEffectsEvent(player);
            Bukkit.getPluginManager().callEvent(radiationEffectsEvent);
            if (radiationEffectsEvent.isCancelled()) {
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 999999, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 999999, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 999999, 2));
        }
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        if (!wRegion.contains(event.getPlayer().getUniqueId()) || !manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        wRegion.remove(player.getUniqueId());

        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if ((!wRegion.contains(event.getEntity().getUniqueId())) || !plugin.getConfig().getBoolean("custom-death-messages")) {
            return;
        }
        if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;

        EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
        if (cause == null || cause != EntityDamageEvent.DamageCause.WITHER) return;

        String deathMessage = DSUtils.colour(plugin.getConfig().getString("death-messages.radiation").replace("%player%", event.getEntity().getName()));
        event.setDeathMessage(deathMessage);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (wRegion.contains(event.getPlayer().getUniqueId())) wRegion.remove(event.getPlayer().getUniqueId());
    }

    private boolean hasArmour(Player player, Material h, Material c, Material l, Material b) {
        return Item.getArmour(player.getInventory()) && isArmour(player.getInventory(), h, c, l, b);
    }

    private boolean isArmour(PlayerInventory inventory, Material h, Material c, Material l, Material b) {
        return inventory.getHelmet().getType() == h && inventory.getChestplate().getType() == c && inventory.getLeggings().getType() == l && inventory.getBoots().getType() == b;
    }
}
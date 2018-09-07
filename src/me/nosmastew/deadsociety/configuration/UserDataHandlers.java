package me.nosmastew.deadsociety.configuration;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UserDataHandlers implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private FileConfiguration config;

    public UserDataHandlers(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;
        UserData data = UserData.getConfig(plugin, event.getPlayer());
        data.setAccountInfo();
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        if (manager.isDisabledInWorld(event.getPlayer().getWorld())) return;
        UserData data = UserData.getConfig(plugin, event.getPlayer());
        data.setAccountInfo();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        UserData.remove(event.getPlayer());
    }

    @EventHandler
    public void onHungerNausea(FoodLevelChangeEvent event) {
        if ((event.getEntity().getType() == EntityType.PLAYER) && (config.getBoolean("hungry-nausea.enable"))) {
            if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;
            if (event.getFoodLevel() > 2) {
                event.getEntity().removePotionEffect(PotionEffectType.CONFUSION);
                return;
            }
            int level = config.getInt("hungry-nausea.level");
            event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 999999, level));
        }
    }

    @EventHandler
    public void onFallSlowness(EntityDamageEvent event) {
        if ((event.getEntity().getType() == EntityType.PLAYER) && (event.getCause() != null) && (event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
            Player player = (Player) event.getEntity();
            if (manager.isDisabledInWorld(player.getWorld()) || !config.getBoolean("fall-slowness.enable")) {
                return;
            }
            if ((player.hasPermission(Permission.FALL.get())) || (player.hasPermission(Permission.OP.get()))) {
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 25 * config.getInt("fall-slowness.time"), config.getInt("fall-slowness.level")));
        }
    }
}
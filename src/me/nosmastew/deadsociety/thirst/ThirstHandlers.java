package me.nosmastew.deadsociety.thirst;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.MessageType;
import me.nosmastew.deadsociety.api.events.PlayerThirstLevelChangeEvent;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class ThirstHandlers implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Thirst thirst;

    public ThirstHandlers(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.thirst = plugin.getThirst();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!thirst.isEnabled() || manager.isDisabledInWorld(event.getPlayer().getWorld())) return;

        Player player = event.getPlayer();
        UserData data = UserData.getConfig(plugin, player);

        if (data.getString("thirst") == null) {
            thirst.setThirst(player, 100);
        } else {
            thirst.setThirst(player, data.getInt("thirst"));
        }
        thirst.setup(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!thirst.hasThirst(event.getPlayer()) || (manager.isDisabledInWorld(event.getPlayer().getWorld()))) return;

        thirst.save(event.getPlayer());
        thirst.removeThirst(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!thirst.hasThirst(event.getEntity()) || (manager.isDisabledInWorld(event.getEntity().getWorld()))) return;

        UserData data = UserData.getConfig(plugin, event.getEntity());
        data.set("thirst", null);
        data.save();

        if (plugin.getConfig().getBoolean("custom-death-messages") && (thirst.getThirst(event.getEntity()) <= 10)) {
            String deathMessage = DSUtils.colour(plugin.getConfig().getString("death-messages.thirst").replace("%player%", event.getEntity().getName()));
            event.setDeathMessage(deathMessage);
        }
        thirst.removeThirst(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!thirst.isEnabled() || manager.isDisabledInWorld(event.getPlayer().getWorld())) return;

        thirst.setThirst(event.getPlayer(), 100);
        thirst.setup(event.getPlayer());
    }

    @EventHandler
    public void onLevel(PlayerThirstLevelChangeEvent event) {
        if (event.getBossBar() != null && manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            thirst.save(event.getPlayer());
            event.getBossBar().removePlayer(event.getPlayer());
            thirst.removeThirst(event.getPlayer());
            return;
        }
        if (event.isUnderSwimming() || (event.notValidate())) event.setCancelled(true);

        Player player = event.getPlayer();

        if (player.isOp() && !plugin.getConfig().getBoolean("allow-operators-thirst-consumption")) {
            event.setCancelled(true);
        }
        int thirst = event.getThirst();

        if (thirst <= 10) {
            player.setHealth(0.0);
            event.setCancelled(true);
        }

        boolean enabled = plugin.getConfig().getBoolean("thirst-warning-messages.enable");
        if (!enabled || event.isCancelled()) return;

        MessageType type = MessageType.valueOf(plugin.getConfig().getString("thirst-warning-messages.type"));
        String path = plugin.getConfig().getString("thirst-warning-messages." + thirst);

        if (path == null || thirst < 15) return;

        event.setWarningMessage(type, warning("thirst-warning-messages." + thirst));
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == null) return;

        Player player = event.getPlayer();

        if (manager.isDisabledInWorld(player.getWorld())) return;

        String mat = player.getInventory().getItemInMainHand().getType().toString().toLowerCase();
        String path = plugin.getConfig().getString("thirst-consuming-items." + mat);

        if (path == null) return;

        thirst.setThirst(player, thirst.getThirst(player) + amount("thirst-consuming-items." + mat));

        thirst.setup(player);
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        if (thirst.hasThirst(event.getPlayer()) && manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            thirst.setup(event.getPlayer());
            return;
        }
        Player player = event.getPlayer();

        if (!thirst.isEnabled() || thirst.hasThirst(player) || manager.isDisabledInWorld(player.getWorld())) return;

        UserData data = UserData.getConfig(plugin, player);

        if (data.getString("thirst") == null) {
            thirst.setThirst(player, 100);
        } else {
            thirst.setThirst(player, data.getInt("thirst"));
        }
        thirst.setup(player);
    }

    private String warning(String path) {
        return DSUtils.colour(plugin.getConfig().getString(path));
    }

    private int amount(String path) {
        return plugin.getConfig().getInt(path);
    }
}
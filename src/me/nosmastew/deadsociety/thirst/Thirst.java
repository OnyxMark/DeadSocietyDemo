package me.nosmastew.deadsociety.thirst;

import com.google.common.base.Preconditions;
import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.api.events.PlayerThirstLevelChangeEvent;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Thirst {

    private DeadSociety plugin;

    private Map<UUID, Integer> thirst = new HashMap<>();
    private Map<UUID, BossBar> bossbar = new HashMap<>();

    public Thirst(DeadSociety plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, Integer> thirst() {
        if (thirst == null) {
            return null;
        }
        return thirst;
    }

    public int getThirst(Player player) {
        if (player == null) {
            return 0;
        }
        return thirst.get(player.getUniqueId());
    }

    public boolean hasThirst(Player player) {
        return thirst.containsKey(player.getUniqueId());
    }

    public void setThirst(Player player, int amount) {
        if (player == null) {
            Preconditions.checkArgument(player != null, "Player does not exist");
            return;
        }
        if (amount < 0) {
            Preconditions.checkArgument(amount >= 0, "Cannot set negative amount of thirst");
            return;
        }
        if (amount > 100) {
            thirst.put(player.getUniqueId(), 100);
            return;
        }
        thirst.put(player.getUniqueId(), amount);
    }

    public void removeThirst(Player player) {
        if (player == null || !hasThirst(player)) {
            return;
        }
        thirst.remove(player.getUniqueId());
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("enable-thirst");
    }

    public boolean isUnderSwimming(Player player) {
        return player.getEyeLocation().getBlock().getType() == Material.WATER || player.getEyeLocation().getBlock().getType() == Material.STATIONARY_WATER;
    }

    public boolean notValidate(Player player) {
        return player == null || player.isDead() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
    }

    public void run() {
        if (isEnabled()) {
            int time = plugin.getConfig().getInt("set-thirst-consumption-time");
            Bukkit.getScheduler().runTaskTimer(plugin, this::start, 0, 20 * time);
        }
    }

    public void save() {
        for (Object id : thirst.keySet().toArray()) {
            UserData data = UserData.getConfig(plugin, Bukkit.getPlayer((UUID) id));
            data.set("thirst", thirst.get(id));
            data.save();
        }
    }

    public void save(Player player) {
        UserData data = UserData.getConfig(plugin, player);
        data.set("thirst", getThirst(player));
        data.save();
    }

    private void start(){
        for (Object id : thirst.keySet().toArray()) {
            Player player = Bukkit.getPlayer((UUID) id);
            if (player != null && !player.isDead()) {
                setup(player);
            }
        }
    }

    public void setup(Player player) {
        BossBar bb = bossbar.get(player.getUniqueId());

        if (bb == null) {
            bb = Bukkit.createBossBar(ChatColor.GRAY + " ", BarColor.BLUE, BarStyle.SEGMENTED_10);
            bossbar.put(player.getUniqueId(), bb);
            bb.addPlayer(player);
        }

        bb.addPlayer(player);
        bb.setColor(BarColor.valueOf(plugin.getConfig().getString("set-thirst-bar-color")));

        for (int i = 100; i > 5; i--) {
            String title = DSUtils.colour(plugin.getConfig().getString("set-thirst-title"));
            bb.setTitle(title + ChatColor.DARK_GREEN + getThirst(player) + "%");
        }
        bb.setProgress(1.0);

        PlayerThirstLevelChangeEvent event = new PlayerThirstLevelChangeEvent(player, this, bb);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        if (hasThirst(player)) setThirst(player, getThirst(player) - 1);
    }
}
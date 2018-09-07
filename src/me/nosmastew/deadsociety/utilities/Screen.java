package me.nosmastew.deadsociety.utilities;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.events.PlayerQuitScreenEvent;
import me.nosmastew.deadsociety.api.events.PlayerWhileScreenEvent;
import me.nosmastew.deadsociety.diseases.Disease;
import me.nosmastew.deadsociety.diseases.DiseaseType;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Screen {

    private DeadSociety plugin;
    private Disease disease;
    private WorldManagement manager;

    public Screen(DeadSociety plugin) {
        this.plugin = plugin;
        this.disease = plugin.getDisease();
        this.manager = plugin.getWorldManagement();
    }

    private Map<UUID, BossBar> screen = new HashMap<>();

    public boolean hasScreen(Player player) {
        return screen.containsKey(player.getUniqueId());
    }

    public Map<UUID, BossBar> getScreen() {
        return this.screen;
    }

    public void remove(Player player) {
        screen.get(player.getUniqueId()).removePlayer(player);
        screen.remove(player.getUniqueId());
    }

    public void create(Player player, DiseaseType type, String title) {
        BossBar bb = screen.get(player.getUniqueId());

        disease.setDisease(player, type);
        if (bb == null) {
            bb = Bukkit.createBossBar(title.replace("%type%", disease.getDiseaseType(player).get()), BarColor.RED, BarStyle.SEGMENTED_20);
            screen.put(player.getUniqueId(), bb);
            bb.addPlayer(player);
        }
        bb.addPlayer(player);
        bb.setColor(BarColor.RED);

        String world = player.getWorld().getName();

        long start = System.currentTimeMillis();
        BossBar finalBb = bb;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (screen.get(player.getUniqueId()) == null) {
                    if (!player.isOnline() || player == null || !world.equals(player.getWorld().getName())) {
                        PlayerQuitScreenEvent quitScreenEvent = new PlayerQuitScreenEvent(player, type.get(), 1, "UNLIMITED");
                        Bukkit.getPluginManager().callEvent(quitScreenEvent);
                    }
                    if (disease.hasDisease(player)) disease.removeDisease(player);
                    cancel();
                    return;
                }
                finalBb.setTitle(title.replace("%type%", disease.getDiseaseType(player).get()));
                finalBb.setProgress(1);

                PlayerWhileScreenEvent whileScreenEvent = new PlayerWhileScreenEvent(player);
                Bukkit.getPluginManager().callEvent(whileScreenEvent);
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void create(Player player, DiseaseType type, String title, double removeProgress, double maxProgress, double maxSeconds, long maxTime) {
        BossBar bb = screen.get(player.getUniqueId());

        if (bb == null) {
            bb = Bukkit.createBossBar(title.replace("%seconds%", String.format("%.0f", maxSeconds)), BarColor.RED, BarStyle.SEGMENTED_20);
            screen.put(player.getUniqueId(), bb);
            bb.addPlayer(player);
        }
        bb.addPlayer(player);
        bb.setColor(BarColor.RED);

        disease.setDisease(player, type);
        String world = player.getWorld().getName();

        long start = System.currentTimeMillis();
        BossBar finalBb = bb;

        new BukkitRunnable() {
            private double barProgress = maxProgress;

            @Override
            public void run() {
                long time = System.currentTimeMillis() - start;

                if (time > maxTime) {
                    finalBb.removePlayer(player);
                    screen.remove(player.getUniqueId());

                    player.setHealth(0.0);
                    disease.removeDisease(player);
                    cancel();
                    return;
                }

                barProgress -= removeProgress;
                String timeFormatted = DSUtils.format(maxSeconds - (((double) time) / 1000));

                if (screen.get(player.getUniqueId()) == null) {
                    if (!player.isOnline() || player == null || !world.equals(player.getWorld().getName())) {
                        PlayerQuitScreenEvent quitScreenEvent = new PlayerQuitScreenEvent(player, type.get(), finalBb.getProgress(), String.format("%.0f", Double.parseDouble(timeFormatted)));
                        Bukkit.getPluginManager().callEvent(quitScreenEvent);
                    }
                    if (disease.hasDisease(player)) disease.removeDisease(player);
                    cancel();
                    return;
                }

                finalBb.setTitle(title.replace("%seconds%", String.format("%.0f", Double.parseDouble(timeFormatted))));
                finalBb.setProgress(barProgress <= 0 ? 0.0 : barProgress);

                PlayerWhileScreenEvent whileScreenEvent = new PlayerWhileScreenEvent(player);
                Bukkit.getPluginManager().callEvent(whileScreenEvent);
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}

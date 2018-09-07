package me.nosmastew.deadsociety.diseases;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.api.events.PlayerQuitScreenEvent;
import me.nosmastew.deadsociety.api.events.PlayerWhileScreenEvent;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import me.nosmastew.deadsociety.utilities.Screen;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DiseaseHandlers implements Listener {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Disease disease;
    private Screen screen;

    private Set<UUID> control = new HashSet<>();

    public DiseaseHandlers(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.disease = plugin.getDisease();
        this.screen = plugin.getScreen();
    }

    @EventHandler
    public void onRandomBiteDisease(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Zombie) && (event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            if (manager.isDisabledInWorld(player.getWorld())) return;

            if (player.hasPermission(Permission.INFECTION_IMMUNE.get()) || player.hasPermission(Permission.OP.get())) {
                return;
            }

            if (disease.hasDisease(player) || disease.hasProtection(player)) return;

            int random = DSUtils.randomInt(1, 3);
            if (random == 1) return;

            String title = DSUtils.colour(plugin.getConfig().getString("set-infection-screen-title"));

            if (random == 2) screen.create(player, DiseaseType.LEG, title);

            if (random == 3) screen.create(player, DiseaseType.NECK, title);

            player.sendMessage(Lang.valueOf("INFECTED_" + disease.getDiseaseType(player).get()).get());
        }
    }

    @EventHandler
    public void onWhileInfectionScreen(PlayerWhileScreenEvent event) {
        if (disease.getDiseaseType(event.getPlayer()) == DiseaseType.BLEEDING) return;
        Player player = event.getPlayer();

        if (control.contains(player.getUniqueId())) return;
        control.add(player.getUniqueId());

        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 3));

        int blindnessLevel = this.plugin.getConfig().getInt("infection-blindness-level");
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, blindnessLevel));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 3));

        Bukkit.getScheduler().runTaskLater(plugin, () -> control.remove(player.getUniqueId()), 20 * 10);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (manager.isDisabledInWorld(event.getEntity().getWorld())) return;
        Player player = event.getEntity();

        UserData data = UserData.getConfig(plugin, player);
        if (data.getString("disease." + player.getWorld().getName()) != null) {
            data.set("disease." + player.getWorld().getName(), null);
            data.save();
        }

        if (screen.hasScreen(player)) screen.remove(player);

        EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
        if (cause == null || cause != EntityDamageEvent.DamageCause.WITHER) {
            return;
        }
        if (!plugin.getConfig().getBoolean("custom-death-messages")) return;

        String deathMessage = DSUtils.colour(plugin.getConfig().getString("death-messages.infection").replace("%player%", event.getEntity().getName()));
        event.setDeathMessage(deathMessage);
    }

    @EventHandler
    public void onDiseaseJoin(PlayerJoinEvent event) {
        if ((!disease.hasDisease(event.getPlayer())) || manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            return;
        }

        if (disease.hasDisease(event.getPlayer(), DiseaseType.BLEEDING)) return;

        String[] b = UserData.getConfig(plugin, event.getPlayer()).getString("disease." + event.getPlayer().getWorld().getName()).split("-");

        String title = DSUtils.colour(plugin.getConfig().getString("set-infection-screen-title"));
        Bukkit.getScheduler().runTaskLater(plugin, () -> screen.create(event.getPlayer(), DiseaseType.valueOf(b[0]), title), 20L);
    }

    @EventHandler
    public void onBleedingJoin(PlayerJoinEvent event) {
        if ((!disease.hasDisease(event.getPlayer())) || manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            return;
        }

        if (!disease.hasDisease(event.getPlayer(), DiseaseType.BLEEDING)) return;
        Player player = event.getPlayer();

        String[] b = UserData.getConfig(plugin, event.getPlayer()).getString("disease." + event.getPlayer().getWorld().getName()).split("-");

        String title = DSUtils.colour(plugin.getConfig().getString("set-bleeding-screen-title"));
        Bukkit.getScheduler().runTaskLater(plugin, () -> screen.create(event.getPlayer(), DiseaseType.valueOf(b[0]), title, 0.025, Double.parseDouble(b[1]), Integer.parseInt(b[2]), Long.parseLong(b[2]) * 1000), 20L);
    }

    @EventHandler
    public void onWhileBleedingScreen(PlayerWhileScreenEvent event) {
        if (disease.getDiseaseType(event.getPlayer()) != DiseaseType.BLEEDING) return;
        event.getPlayer().getWorld().playEffect(event.getPlayer().getLocation().add(0, 0.5D, 0), Effect.LAVADRIP, 10);
    }

    @EventHandler
    public void onScreenQuit(PlayerQuitScreenEvent event) {
        UserData data = UserData.getConfig(plugin, event.getPlayer());
        data.set("disease." + event.getPlayer().getWorld().getName(), event.getDiseaseType() + "-" + event.getProgress() + "-" + event.getTimeFormatted());
        data.save();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (screen.hasScreen(event.getPlayer())) screen.remove(event.getPlayer());
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        if (screen.hasScreen(event.getPlayer())) screen.remove(event.getPlayer());
    }

    @EventHandler
    public void onItemConsuming(PlayerItemConsumeEvent event) {
        if (disease.getDiseaseType(event.getPlayer()) == null || manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(Lang.DYING_FROM_INFECTION.get());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (disease.getDiseaseType(event.getPlayer()) == null || manager.isDisabledInWorld(event.getPlayer().getWorld())) {
            return;
        }
        Player player = event.getPlayer();

        if (player.hasPermission(Permission.INFECTION_COMMANDS_USE.get()) || player.hasPermission(Permission.OP.get())) {
            return;
        }
        if (!plugin.getConfig().getBoolean("infection-commands-cancellation")) return;

        event.setCancelled(true);
        player.sendMessage(Lang.DYING_FROM_INFECTION.get());
    }
}
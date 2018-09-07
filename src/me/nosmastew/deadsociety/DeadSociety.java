package me.nosmastew.deadsociety;

import com.google.common.base.Charsets;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.nosmastew.deadsociety.commands.*;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.configuration.UserDataHandlers;
import me.nosmastew.deadsociety.configuration.ZoneData;
import me.nosmastew.deadsociety.diseases.Disease;
import me.nosmastew.deadsociety.diseases.DiseaseHandlers;
import me.nosmastew.deadsociety.items.types.*;
import me.nosmastew.deadsociety.nms.Title;
import me.nosmastew.deadsociety.nms.v1_10_R1.Title_v1_10_R1;
import me.nosmastew.deadsociety.nms.v1_11_R1.Title_v1_11_R1;
import me.nosmastew.deadsociety.nms.v1_12_R1.Title_v1_12_R1;
import me.nosmastew.deadsociety.nms.v1_9_R2.Title_v1_9_R2;
import me.nosmastew.deadsociety.survival.*;
import me.nosmastew.deadsociety.thirst.Thirst;
import me.nosmastew.deadsociety.thirst.ThirstHandlers;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Screen;
import me.nosmastew.deadsociety.zombies.Default;
import me.nosmastew.deadsociety.zones.ZoneTeleportHandlers;
import me.nosmastew.deadsociety.zones.types.RadiationZone;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DeadSociety extends JavaPlugin {

    private Economy econ = null;

    private Title title;

    private Thirst thirst;
    private Disease disease;
    private Screen screen;

    private WorldManagement worldManagement;

    private File langConfig;
    private FileConfiguration lang;

    @Override
    public void onEnable() {
        thirst = new Thirst(this);
        disease = new Disease(this);

        worldManagement = new WorldManagement(this);
        /* Version Check */
        if (setupVersion()) {
            screen = new Screen(this);
            /* Configurations */
            saveDefaultConfig();
            createFiles();
            updateFiles();
            /* Events Registration */
            PluginManager plgm = this.getServer().getPluginManager();
            /* Player Data */
            plgm.registerEvents(new UserDataHandlers(this), this);
            /* Survival Gamemode */
            plgm.registerEvents(new Blocks(this), this);
            plgm.registerEvents(new BarbedWire(this), this);
            plgm.registerEvents(new Entities(this), this);
            plgm.registerEvents(new CustomHorse(this), this);

            plgm.registerEvents(new InfectedBottle(this), this);
            plgm.registerEvents(new Doors(this), this);
            plgm.registerEvents(new BackpackCommand(this), this);
            plgm.registerEvents(new Rewards(this), this);
            /* Zombie Types */
            plgm.registerEvents(new Default(this), this);
            /* Diseases */
            plgm.registerEvents(new DiseaseHandlers(this), this);
            /* Safezone & Radiation Zones */
            plgm.registerEvents(new ZoneTeleportHandlers(this), this);
            /* Custom Items */
            plgm.registerEvents(new Antibiotics(this), this);
            plgm.registerEvents(new Bandage(this), this);
            plgm.registerEvents(new Flashlight(this), this);
            plgm.registerEvents(new Knife(this), this);
            plgm.registerEvents(new Painkillers(this), this);
            plgm.registerEvents(new Compass(this), this);
            /* Item Shop */
            plgm.registerEvents(new ItemsCommand(this), this);
            /* Thirst */
            plgm.registerEvents(new ThirstHandlers(this), this);
            /* Hook */
            registerWorldGuard();
            /* Commands Registration */
            getCommand("deadsociety").setExecutor(new DefaultCommand(this));
            getCommand("items").setExecutor(new ItemsCommand(this));
            getCommand("backpack").setExecutor(new BackpackCommand(this));
            getCommand("thirst").setExecutor(new ThirstCommand(this));
            getCommand("setcamp").setExecutor(new SetCampCommand(this));

            /* Thirst */
            thirst.run();

            worldManagement.loadEnabledWorlds();
            /* Initialize */
            Item.initialize(this);
        } else {
            getLogger().warning("Check if your server version is not compatible with this plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        worldManagement.clearEnabledWorlds();
        /* Data Removal */
        UserData.removeAll();
        ZoneData.removeAll();
        /* Save Data */
        thirst.save();
    }

    public Thirst getThirst() {
        return thirst;
    }

    public Disease getDisease() {
        return this.disease;
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy econ() {
        return this.econ;
    }

    private boolean setupVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException versionException) {
            return false;
        }

        switch (version) {
            case "v1_12_R1":
                title = new Title_v1_12_R1();
                break;
            case "v1_11_R1":
                title = new Title_v1_11_R1();
                break;
            case "v1_10_R1":
                title = new Title_v1_10_R1();
                break;
            case "v1_9_R2":
                title = new Title_v1_9_R2();
                break;
        }
        return title != null;
    }

    public WorldManagement getWorldManagement() {
        return worldManagement;
    }

    private void registerWorldGuard() {
        if (getWorldEdit() != null && getWorldGuard() != null) {
            getServer().getPluginManager().registerEvents(new ZonesCommand(this), this);
            getServer().getPluginManager().registerEvents(new RadiationZone(this), this);
            getCommand("zones").setExecutor(new ZonesCommand(this));
        }
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null;
        }
        return (WorldEditPlugin) plugin;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public Title getTitle() {
        return title;
    }

    public void sendActionBar(Player player, String string) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(string));
    }

    public FileConfiguration getLanguageConfig() {
        return this.lang;
    }

    public void reloadLanguageConfig() {
        lang = YamlConfiguration.loadConfiguration(langConfig);
        InputStream defItemsConfigStream = this.getResource("lang.yml");
        if (defItemsConfigStream != null) {
            lang.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defItemsConfigStream, Charsets.UTF_8)));
        }
    }

    private void createFiles() {
        langConfig = new File(getDataFolder(), "lang.yml");
        if (!langConfig.exists()) {
            if (!langConfig.exists()) {
                langConfig.getParentFile().mkdirs();
                saveResource("lang.yml", false);
                getLogger().info("Creating lang.yml configuration file...");
            }

            lang = new YamlConfiguration();
            try {
                lang.load(langConfig);
            } catch (IOException | InvalidConfigurationException e) {
                getLogger().warning("Cannot load lang.yml configuration file.");
            }
        }
    }

    public Screen getScreen(){ return this.screen; }

    private void updateFiles() {
        boolean update = false;

        String v1 = "1.0";
        String configVersion = getConfig().getString("configuration-version");
        if (getConfig() != null && !v1.equals(configVersion) || !getConfig().isSet("configuration-version")) {
            saveResource("config.yml", true);
            update = true;
        }

        String v2 = "1.0";
        String langConfigVersion = getLanguageConfig().getString("configuration-version");
        if (getLanguageConfig() != null && !v2.equals(langConfigVersion)) {
            saveResource("lang.yml", true);
            update = true;
        }

        if (update) getLogger().info("Updating configuration files ...");

    }
}

package me.nosmastew.deadsociety;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorldManagement {

    private final Set<UUID> enabledWorlds = new HashSet<>();

    private DeadSociety plugin;

    public WorldManagement(DeadSociety plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all enabled worlds from the configuration file to memory
     */

    public void loadEnabledWorlds() {
        this.enabledWorlds.clear();

        for (String worldName : plugin.getConfig().getStringList("enabled-worlds")) {
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getLogger().info("Unknown world found... \"" + worldName + "\". Ignoring...");
                continue;
            }

            this.enabledWorlds.add(world.getUID());
        }
    }

    /**
     * Check whether a world has DeadSociety enabled
     *
     * @param world the world to check
     * @return true if the world has DeadSociety enabled
     */
    public boolean isDisabledInWorld(World world) {
        Preconditions.checkArgument(world != null, "Cannot check state of deadsociety in null world");
        return !enabledWorlds.contains(world.getUID());
    }

    /**
     * Get a list of all worlds in which DeadSociety is enabled
     *
     * @return a list of all enabled worlds
     */
    public Set<World> getEnabledWorlds() {
        return enabledWorlds.stream().map(Bukkit::getWorld).collect(Collectors.toSet());
    }

    /**
     * Clear all worlds from the list
     */
    public void clearEnabledWorlds() {
        if (this.enabledWorlds != null) this.enabledWorlds.clear();
    }
}
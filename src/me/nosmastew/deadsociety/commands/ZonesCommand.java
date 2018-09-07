package me.nosmastew.deadsociety.commands;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.configuration.ZoneData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import me.nosmastew.deadsociety.zones.ZoneTeleport;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;

public class ZonesCommand implements Listener, CommandExecutor {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public ZonesCommand(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onCreate(SignChangeEvent e) {
        if ((e.getLine(0).equalsIgnoreCase(DSUtils.ZONE_SIGN)) && (e.getLine(1) != null)) {
            Player player = e.getPlayer();
            if (manager.isDisabledInWorld(player.getWorld())) return;
            if (player.hasPermission(Permission.ZONE_SIGN_CREATE.get()) || player.hasPermission(Permission.ZONE_SIGN_OP.get()) || player.hasPermission(Permission.OP.get())) {
                e.setLine(0, DSUtils.ZONE_SIGN);
                e.setLine(3, DSUtils.CLICK_HERE_SIGN);
                return;
            }
            player.sendMessage(Lang.NO_PERMISSION.get());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Sign) {
            Sign s = (Sign) e.getBlock().getState();
            if ((s.getLine(0).equals(DSUtils.ZONE_SIGN) && (s.getLine(3).equals(DSUtils.CLICK_HERE_SIGN)))) {
                Player player = e.getPlayer();
                if (manager.isDisabledInWorld(player.getWorld())) return;
                if (player.hasPermission(Permission.ZONE_SIGN_BREAK.get()) || player.hasPermission(Permission.ZONE_SIGN_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    return;
                }
                e.setCancelled(true);
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getState() instanceof Sign)) {
            Sign s = (Sign) e.getClickedBlock().getState();
            if ((s.getLine(0).equals(DSUtils.ZONE_SIGN)) && (s.getLine(3).equals(DSUtils.CLICK_HERE_SIGN))) {
                Player player = e.getPlayer();
                if (manager.isDisabledInWorld(player.getWorld())) return;
                if (player.hasPermission(Permission.ZONE_SIGN_USE.get()) || player.hasPermission(Permission.ZONE_SIGN_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    String zone = s.getLine(1).toLowerCase();
                    File file = new File(plugin.getDataFolder(), "zones" + File.separator + zone + ".yml");
                    if (!file.exists()) {
                        player.sendMessage(Lang.ZONE_NOT_EXIST.get());
                        return;
                    }
                    ZoneData data = ZoneData.getConfig(plugin, zone);
                    if (data.getString("zone.type").equals("radiation-zone") && (data.getString("zone.spawn") == null)) {
                        player.sendMessage(Lang.INVALID_ZONE_LOCATION.get());
                        return;
                    }
                    int cooldown = plugin.getConfig().getInt("zones-teleportation-cooldown");
                    new ZoneTeleport(plugin, player, zone).runTaskLater(plugin, 20 * cooldown);
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DSUtils.DS + Lang.PLAYER_COMMAND.get());
            return true;
        }
        Player player = (Player) sender;
        if (manager.isDisabledInWorld(player.getWorld())) return true;

        if (args.length == 0) {
            if (player.hasPermission(Permission.ZONE_USE.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                player.sendMessage(ChatColor.DARK_GRAY + "=======================[" + ChatColor.RED + "Zones" + ChatColor.DARK_GRAY + "]========================");
                player.sendMessage(ChatColor.RED + "/Zones List " + ChatColor.GRAY + "- Shows a list of all the available zones.");
                player.sendMessage(ChatColor.RED + "/Zones Teleport <Zone>" + ChatColor.GRAY + "- Teleports to a zone by its name.");
                player.sendMessage(ChatColor.RED + "/Zones Create <Zone> <ZoneType>" + ChatColor.GRAY + "- Creates a zone by its name and type.");
                player.sendMessage(ChatColor.RED + "/Zones Spawn <Zone>" + ChatColor.GRAY + "- Sets a zone spawnpoint by its name.");
                player.sendMessage(ChatColor.RED + "/Zones Remove <Zone> " + ChatColor.GRAY + "- Removes a zone by its name.");
            } else {
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (player.hasPermission(Permission.ZONE_LIST.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    File directory = new File(plugin.getDataFolder(), "zones");
                    if ((!directory.exists()) || ((directory.exists()) && ((directory.listFiles().length == 0)))) {
                        sender.sendMessage(Lang.NO_AVAILABLE_ZONES.get());
                        return true;
                    }
                    File[] listFiles = directory.listFiles();
                    if (listFiles == null) return true;
                    player.sendMessage("");
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.UNDERLINE + Lang.AVAILABLE_ZONES.get());
                    player.sendMessage("");
                    for (File zones : listFiles) {
                        String zone = zones.getName().toLowerCase().replace(".yml", "");
                        String zoneType = ZoneData.getConfig(plugin, zone).getString("zone.type");
                        player.sendMessage(DSUtils.DOT + ChatColor.GRAY + zone + " - " + ChatColor.RED + "[" + ChatColor.GRAY + zoneType + ChatColor.RED + "]");
                    }
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("spawn")) {
                if (!player.hasPermission("ds.zone." + args[0].toLowerCase()) && !player.hasPermission(Permission.ZONE_OP.get()) && !player.hasPermission(Permission.OP.get())) {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                    return true;
                }
                player.sendMessage(Lang.SPECIFY_ZONE.get());
            } else {
                sender.sendMessage(Lang.WRONG_USAGE.get());
            }
        } else if (args.length == 2) {
            String zone = args[1].toLowerCase();
            if (args[0].equalsIgnoreCase("teleport")) {
                if (player.hasPermission(Permission.ZONE_TELEPORT.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    File file = new File(plugin.getDataFolder(), "zones" + File.separator + zone + ".yml");
                    if (!file.exists()) {
                        player.sendMessage(Lang.ZONE_NOT_EXIST.get());
                        return true;
                    }
                    ZoneData data = ZoneData.getConfig(plugin, zone);
                    if (data.getString("zone.type").equals("radiation-zone") && (data.getString("zone.spawn") == null)) {
                        player.sendMessage(Lang.INVALID_ZONE_LOCATION.get());
                        return true;
                    }
                    int cooldown = plugin.getConfig().getInt("zones-teleportation-cooldown");
                    new ZoneTeleport(plugin, player, zone).runTaskLater(plugin, 20 * cooldown);
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (player.hasPermission(Permission.ZONE_REMOVE.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    File file = new File(plugin.getDataFolder(), "zones" + File.separator + zone + ".yml");
                    RegionManager regionManager = plugin.getWorldGuard().getRegionManager(player.getWorld());
                    if (!file.exists() && regionManager.getRegion(zone) == null) {
                        player.sendMessage(Lang.ZONE_NOT_EXIST.get());
                        return true;
                    }
                    if (regionManager.getRegion(zone) != null) regionManager.removeRegion(zone);

                    ZoneData data = ZoneData.getConfig(plugin, zone);
                    data.set("zone", null);
                    data.save();
                    file.delete();

                    player.sendMessage(ChatColor.GOLD + zone + " " + Lang.ZONE_REMOVED.get());
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission(Permission.ZONE_CREATE.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    if (zone.length() > 15) {
                        player.sendMessage(Lang.ARGS_WARNING.get());
                        return true;
                    }
                    File directory = new File(plugin.getDataFolder(), "zones");
                    if ((directory.exists()) && ((directory.listFiles().length == 30))) {
                        player.sendMessage(Lang.ZONE_MAX.get());
                        return true;
                    }
                    player.sendMessage(Lang.SPECIFY_ZONE_TYPE.get());
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            } else if (args[0].equalsIgnoreCase("spawn")) {
                if (player.hasPermission(Permission.ZONE_SPAWN.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    File file = new File(plugin.getDataFolder(), "zones" + File.separator + zone + ".yml");
                    if (!file.exists()) {
                        player.sendMessage(Lang.ZONE_NOT_EXIST.get());
                        return true;
                    }
                    ZoneData data = ZoneData.getConfig(plugin, zone);
                    data.set("zone.spawn", player.getLocation());
                    data.save();

                    player.sendMessage(ChatColor.GOLD + zone + " " + Lang.SPAWNPOINT_SET.get());
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            } else {
                sender.sendMessage(Lang.WRONG_USAGE.get());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission(Permission.ZONE_CREATE.get()) || player.hasPermission(Permission.ZONE_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    String type = args[2].toLowerCase();
                    if (!type.equalsIgnoreCase("safezone") && !type.equals("radiationzone")) {
                        player.sendMessage("Available Zones: [safezone, radiationzone]");
                        return true;
                    }
                    String zone = args[1].toLowerCase();
                    ZoneData data = ZoneData.getConfig(plugin, zone);
                    if (type.equalsIgnoreCase("radiationzone")) {
                        Selection selection = plugin.getWorldEdit().getSelection(player);
                        if (selection == null) {
                            player.sendMessage(Lang.NO_SELECTION.get());
                            return true;
                        }
                        RegionManager regionManager = plugin.getWorldGuard().getRegionManager(player.getWorld());
                        if (regionManager.getRegion(zone) != null) {
                            player.sendMessage(ChatColor.GOLD + zone + " " + Lang.ALREADY_EXISTS.get());
                            return true;
                        }

                        ProtectedCuboidRegion region = new ProtectedCuboidRegion(zone, new BlockVector(selection.getNativeMinimumPoint()), new BlockVector(selection.getNativeMaximumPoint()));
                        plugin.getWorldGuard().getRegionManager(player.getWorld()).addRegion(region);

                        data.create("radiation-zone", player.getLocation());
                        player.sendMessage(ChatColor.GOLD + zone + " " + Lang.ZONE_CREATED.get());
                        return true;
                    }

                    data.create("safezone", player.getLocation());

                    player.sendMessage(ChatColor.GOLD + zone + " " + Lang.ZONE_CREATED.get());
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.get());
                }
            } else {
                player.sendMessage(Lang.WRONG_USAGE.get());
            }
        } else {
            player.sendMessage(Lang.WRONG_USAGE.get());
        }
        return false;
    }
}

package me.nosmastew.deadsociety.commands;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.thirst.Thirst;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ThirstCommand implements CommandExecutor {

    private DeadSociety plugin;
    private final WorldManagement manager;
    private Thirst thirst;

    public ThirstCommand(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
        this.thirst = plugin.getThirst();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (thirst.isEnabled() || !thirst.hasThirst((Player) sender)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (manager.isDisabledInWorld(player.getWorld())) return true;
                if (args.length == 0) {
                    if (player.hasPermission(Permission.THIRST.get()) || player.hasPermission(Permission.THIRST_OP.get()) || player.hasPermission(Permission.OP.get())) {
                        player.sendMessage(Lang.THIRST.get() + " " + ChatColor.AQUA + thirst.getThirst(player) + "%");
                        if (!player.hasPermission(Permission.THIRST_OP.get()) && !player.hasPermission(Permission.OP.get())) {
                            return true;
                        }
                        player.sendMessage("");
                        player.sendMessage(ChatColor.RED + "/Thirst Help" + ChatColor.GRAY + "- Shows all available thirst commands.");
                    } else {
                        player.sendMessage(Lang.NO_PERMISSION.get());
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        if (player.hasPermission(Permission.THIRST_HELP.get()) || player.hasPermission(Permission.THIRST_OP.get()) || player.hasPermission(Permission.OP.get())) {
                            player.sendMessage(ChatColor.DARK_GRAY + "=======================[" + ChatColor.RED + "Thirst" + ChatColor.DARK_GRAY + "]=======================");
                            player.sendMessage(ChatColor.RED + "/Thirst " + ChatColor.GRAY + "- Shows your current thirst.");
                            player.sendMessage(ChatColor.RED + "/Thirst Refill " + ChatColor.GRAY + "- Sets your thirst back to 100%");
                            player.sendMessage(ChatColor.RED + "/Thirst Refill <Player> " + ChatColor.GRAY + "- Sets a player thirst back to 100%");
                        } else {
                            player.sendMessage(Lang.NO_PERMISSION.get());
                        }
                    } else if (args[0].equalsIgnoreCase("refill")) {
                        if (player.hasPermission(Permission.THIRST_REFILL.get()) || player.hasPermission(Permission.THIRST_OP.get()) || player.hasPermission(Permission.OP.get())) {
                            thirst.setThirst(player, 100);
                            thirst.setup(player);
                            player.sendMessage(Lang.THIRST_REFILLED.get());
                        } else {
                            player.sendMessage(Lang.NO_PERMISSION.get());
                        }
                    } else {
                        player.sendMessage(Lang.WRONG_USAGE.get());
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("refill")) {
                        if (player.hasPermission(Permission.THIRST_REFILL_OTHERS.get()) || player.hasPermission(Permission.THIRST_OP.get()) || player.hasPermission(Permission.OP.get())) {
                            Player otherPlayer = player.getServer().getPlayer(args[1]);
                            if (otherPlayer == null) {
                                player.sendMessage(Lang.PLAYER_NOT_FOUND.get());
                                return true;
                            }
                            thirst.setThirst(otherPlayer, 100);
                            thirst.setup(otherPlayer);
                            player.sendMessage(Lang.THIRST_REFILLED_OTHERS.get().replace("%player%", otherPlayer.getName()));
                            otherPlayer.sendMessage(Lang.THIRST_REFILLED.get());
                        } else {
                            player.sendMessage(Lang.NO_PERMISSION.get());
                        }
                    } else {
                        player.sendMessage(Lang.WRONG_USAGE.get());
                    }
                } else {
                    player.sendMessage(Lang.WRONG_USAGE.get());
                }
            } else if (sender instanceof ConsoleCommandSender) {
                ConsoleCommandSender console = (ConsoleCommandSender) sender;
                console.sendMessage(DSUtils.DS + Lang.PLAYER_COMMAND.get());
            }
        } else {
            sender.sendMessage(Lang.THIRST_NOT_AVAILABLE.get());
        }
        return false;
    }
}
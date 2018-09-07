package me.nosmastew.deadsociety.commands;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DefaultCommand implements CommandExecutor {

    private DeadSociety plugin;

    public DefaultCommand(DeadSociety plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission(Permission.DS_USE.get()) && !sender.hasPermission(Permission.OP.get())) {
                sender.sendMessage(Lang.NO_PERMISSION.get());
                return true;
            }
            sender.sendMessage(ChatColor.DARK_GRAY + "=====================[" + ChatColor.RED + "DeadSociety" + ChatColor.DARK_GRAY + "]====================");
            sender.sendMessage(ChatColor.RED + "/DS Commands " + ChatColor.GRAY + "- Shows all the available commands at Spigot.");
            sender.sendMessage(ChatColor.RED + "/DS Permissions " + ChatColor.GRAY + "- Shows all the available permissions at Spigot.");
            sender.sendMessage(ChatColor.RED + "/DS Reload " + ChatColor.GRAY + "- Reloads all the configuration files.");

        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("commands")) {
                if (!sender.hasPermission(Permission.DS_COMMANDS.get()) && !sender.hasPermission(Permission.OP.get())) {
                    sender.sendMessage(Lang.NO_PERMISSION.get());
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "All the available commands can be found:");
                sender.sendMessage(DSUtils.WIKI);

            } else if (args[0].equalsIgnoreCase("permissions") || (args[0].equalsIgnoreCase("perms"))) {
                if (!sender.hasPermission(Permission.DS_PERMISSIONS.get()) && !sender.hasPermission(Permission.OP.get())) {
                    sender.sendMessage(Lang.NO_PERMISSION.get());
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "All the available permissions can be found:");
                sender.sendMessage(DSUtils.WIKI);

            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission(Permission.DS_RELOAD.get()) && !sender.hasPermission(Permission.OP.get())) {
                    sender.sendMessage(Lang.NO_PERMISSION.get());
                    return true;
                }
                plugin.reloadConfig();
                plugin.reloadLanguageConfig();
                sender.sendMessage(DSUtils.DS + DSUtils.FILES_RELOADED);
            } else {
                sender.sendMessage(Lang.WRONG_USAGE.get());
            }
        } else {
            sender.sendMessage(Lang.WRONG_USAGE.get());
        }
        return false;
    }
}
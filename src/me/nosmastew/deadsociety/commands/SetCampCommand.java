package me.nosmastew.deadsociety.commands;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetCampCommand implements CommandExecutor {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public SetCampCommand(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (manager.isDisabledInWorld(player.getWorld())) return true;
            if (player.hasPermission(Permission.CAMP.get()) || player.hasPermission(Permission.OP.get())) {
                if (args.length == 0) {
                    UserData data = UserData.getConfig(plugin, player);
                    data.set("camps." + player.getWorld().getName(), player.getLocation());
                    data.save();

                    player.sendMessage(Lang.CAMP_SET.get());
                }
            } else {
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        } else if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            console.sendMessage(DSUtils.DS + Lang.PLAYER_COMMAND.get());
        }
        return false;
    }
}

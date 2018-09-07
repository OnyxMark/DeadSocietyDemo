package me.nosmastew.deadsociety.commands;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.configuration.UserData;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BackpackCommand implements Listener, CommandExecutor {

    private DeadSociety plugin;
    private final WorldManagement manager;

    private List<UUID> backpack = new ArrayList<>();

    public BackpackCommand(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }
    /* Available Slots : 9, 18, 27, 36, 45, 54*/

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (!event.getInventory().getName().equals(Lang.BACKPACK_TITLE.get())) return;

        Player player = (Player) event.getPlayer();
        if (backpack.contains(player.getUniqueId()) || manager.isDisabledInWorld(player.getWorld())) return;

        backpack.add(player.getUniqueId());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().getName().equals(Lang.BACKPACK_TITLE.get())) return;
        Player player = (Player) event.getPlayer();

        if (!backpack.contains(player.getUniqueId()) || manager.isDisabledInWorld(player.getWorld())) return;
        Inventory inv = event.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            UserData data = UserData.getConfig(plugin, player);
            data.set("backpack.slot" + i, item);
            data.save();
        }
        backpack.remove(player.getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        UserData data = UserData.getConfig(plugin, event.getEntity());
        if (manager.isDisabledInWorld(event.getEntity().getWorld()) || data.getString("backpack") == null) {
            return;
        }
        Player player = event.getEntity();
        if ((player.hasPermission(Permission.BACKPACK_KEEP.get())) || (player.hasPermission(Permission.BACKPACK_OP.get())) || (player.hasPermission(Permission.OP.get()))) {
            return;
        }
        for (String path : data.getConfigurationSection("backpack").getKeys(false)) {
            event.getDrops().add(data.getItemStack("backpack." + path));
        }
        data.set("backpack", null);
        data.save();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (backpack.contains(event.getPlayer().getUniqueId())) backpack.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (manager.isDisabledInWorld(player.getWorld())) return true;
            if (player.hasPermission(Permission.BACKPACK.get()) || player.hasPermission(Permission.BACKPACK_OP.get()) || player.hasPermission(Permission.OP.get())) {
                if (args.length != 0) {
                    player.sendMessage(Lang.WRONG_USAGE.get());
                    return true;
                }
                Inventory inv = Bukkit.createInventory(null, 45, Lang.BACKPACK_TITLE.get());
                UserData data = UserData.getConfig(plugin, player);
                if (data.getString("backpack") != null) {
                    for (int i = 0; i < inv.getSize(); i++) {
                        inv.setItem(i, data.getItemStack("backpack.slot" + i));
                    }
                }
                player.openInventory(inv);
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
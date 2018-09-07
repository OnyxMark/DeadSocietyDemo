package me.nosmastew.deadsociety.commands;

import me.nosmastew.deadsociety.DeadSociety;
import me.nosmastew.deadsociety.WorldManagement;
import me.nosmastew.deadsociety.items.ItemType;
import me.nosmastew.deadsociety.utilities.DSUtils;
import me.nosmastew.deadsociety.utilities.Item;
import me.nosmastew.deadsociety.utilities.Lang;
import me.nosmastew.deadsociety.utilities.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemsCommand implements Listener, CommandExecutor {

    private DeadSociety plugin;
    private final WorldManagement manager;

    public ItemsCommand(DeadSociety plugin) {
        this.plugin = plugin;
        this.manager = plugin.getWorldManagement();
    }

    @EventHandler
    public void onCreate(SignChangeEvent event) {
        String name = plugin.getConfig().getString("set-item-sign-shop-name");
        if ((event.getLine(0).equals(name)) && (event.getLine(1) != null) && (event.getLine(2) != null) && (event.getLine(3) != null)) {
            Player player = event.getPlayer();
            if (manager.isDisabledInWorld(player.getWorld())) return;
            if (player.hasPermission(Permission.ITEM_SHOP_CREATE.get()) || player.hasPermission(Permission.ITEM_SHOP_OP.get()) || player.hasPermission(Permission.OP.get())) {
                return;
            }
            player.sendMessage(Lang.NO_PERMISSION.get());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            Sign s = (Sign) event.getBlock().getState();
            String name = plugin.getConfig().getString("set-item-sign-shop-name");
            if (s.getLine(0).equals(name)) {
                Player player = event.getPlayer();
                if (manager.isDisabledInWorld(player.getWorld())) return;
                if (player.hasPermission(Permission.ITEM_SHOP_BREAK.get()) || player.hasPermission(Permission.ITEM_SHOP_OP.get()) || player.hasPermission(Permission.OP.get())) {
                    return;
                }
                event.setCancelled(true);
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getState() instanceof Sign) && plugin.setupEconomy()) {
            Sign s = (Sign) event.getClickedBlock().getState();
            String name = plugin.getConfig().getString("set-item-sign-shop-name");
            if ((!s.getLine(0).equalsIgnoreCase(name)) || (s.getLine(1) == null) || (s.getLine(2) == null) || (s.getLine(3) == null)) {
                return;
            }
            Player player = event.getPlayer();
            if (manager.isDisabledInWorld(player.getWorld())) return;
            if (player.hasPermission(Permission.ITEM_SHOP_USE.get()) || player.hasPermission(Permission.ITEM_SHOP_OP.get()) || player.hasPermission(Permission.OP.get())) {
                try {
                    int price = Integer.parseInt(s.getLine(3));
                    if ((plugin.econ().getBalance(player) < price) || !player.hasPermission(Permission.ITEM_SHOP_INFINITE.get())) {
                        player.sendMessage(Lang.NOT_ENOUGH_MONEY.get());
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 3, 4);
                        return;
                    }
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(Lang.NOT_ENOUGH_SPACE.get());
                        return;
                    }
                    String item = s.getLine(1).toLowerCase();

                    if (plugin.getConfig().getString("custom-items." + item) == null) {
                        player.sendMessage(Lang.NOT_VALID_ITEM.get());
                        return;
                    }
                    if (!player.hasPermission(Permission.ITEM_SHOP_INFINITE.get()) && !player.hasPermission(Permission.ITEM_SHOP_OP.get())) {
                        plugin.econ().withdrawPlayer(player, price);
                    }
                    int amount = Integer.parseInt(s.getLine(2));

                    ItemType itemType = ItemType.valueOf(item.toUpperCase());
                    player.getInventory().addItem(Item.getItem(itemType, amount));
                    String bought = Lang.BOUGHT.get().replace("%amount%", Integer.toString(amount)).replace("%item%", item).replace("%price%", Integer.toString(price));
                    player.sendMessage(DSUtils.ARROW + bought);

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 3, 3);
                } catch (NumberFormatException e) {
                    player.sendMessage(Lang.NOT_VALID_AMOUNT.get());
                }
            } else {
                player.sendMessage(Lang.NO_PERMISSION.get());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (manager.isDisabledInWorld(player.getWorld())) return true;
            onItemsCommandMethod(player, args);

        } else if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            onItemsCommandMethod(console, args);
        }
        return false;
    }

    private void onItemsCommandMethod(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permission.ITEMS_COMMAND.get()) || (sender.hasPermission(Permission.OP.get()))) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/Items Give <Player> <Type> <Amount>" + ChatColor.GRAY + " - Give an item to a player with the defined amount.");
                sender.sendMessage(ChatColor.RED + "/Items List" + ChatColor.GRAY + " - Shows a list of all the available items.");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("give")) {
                    sender.sendMessage(Lang.SPECIFY_PLAYER.get());

                } else if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.UNDERLINE + Lang.AVAILABLE_ITEMS.get());
                    for (String items : plugin.getConfig().getConfigurationSection("custom-items").getKeys(false)) {
                        if (items != null) {
                            sender.sendMessage(ChatColor.GRAY + items);
                        }
                    }
                } else if (!args[0].equalsIgnoreCase("give") || (!args[0].equalsIgnoreCase("list"))) {
                    sender.sendMessage(Lang.WRONG_USAGE.get());
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(Lang.WRONG_USAGE.get());

                } else if (args[0].equalsIgnoreCase("give")) {
                    Player selectedplayer = Bukkit.getServer().getPlayer(args[1]);
                    if (selectedplayer == null) {
                        sender.sendMessage(Lang.PLAYER_NOT_FOUND.get());
                        return;
                    }
                    sender.sendMessage(Lang.SPECIFY_ITEM.get());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(Lang.WRONG_USAGE.get());

                } else if (args[0].equalsIgnoreCase("give")) {
                    sender.sendMessage(Lang.SPECIFY_AMOUNT.get());
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(Lang.WRONG_USAGE.get());

                } else if (args[0].equalsIgnoreCase("give")) {
                    try {
                        Player selectedPlayer = Bukkit.getServer().getPlayer(args[1]);
                        int amount = Integer.parseInt(args[3]);
                        if (selectedPlayer == null) {
                            sender.sendMessage(Lang.PLAYER_NOT_FOUND.get());
                            return;
                        }
                        if (manager.isDisabledInWorld(selectedPlayer.getWorld())) return;
                        if (selectedPlayer.getInventory().firstEmpty() == -1) {
                            sender.sendMessage(Lang.NOT_ENOUGH_SPACE.get());
                            return;
                        }
                        if (amount <= 0) {
                            sender.sendMessage(Lang.NOT_VALID_AMOUNT.get());
                            return;
                        }
                        String item = args[2].toLowerCase();
                        if (plugin.getConfig().getString("custom-items." + item) == null) {
                            sender.sendMessage(Lang.NOT_VALID_ITEM.get());
                            return;
                        }
                        ItemType itemType = ItemType.valueOf(item.toUpperCase());
                        selectedPlayer.getInventory().addItem(Item.getItem(itemType, amount));
                        sender.sendMessage(Lang.GAVE.get() + " " + ChatColor.DARK_GREEN + selectedPlayer.getName() + " " + ChatColor.RED + amount + " " + ChatColor.RED + item + DSUtils.s(item, amount) + ".");
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Lang.NOT_VALID_AMOUNT.get());
                    }
                }
            } else {
                sender.sendMessage(Lang.WRONG_USAGE.get());
            }
        } else {
            sender.sendMessage(Lang.NO_PERMISSION.get());
        }
    }
}

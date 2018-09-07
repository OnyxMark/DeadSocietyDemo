package me.nosmastew.deadsociety.utilities;

import me.nosmastew.deadsociety.DeadSociety;
import org.bukkit.ChatColor;

public enum Lang {
    NO_PERMISSION("&cYou do not have the permission to do that."),
    NOT_ENOUGH_MONEY("&cNot enough money to afford it."),
    PLAYER_NOT_FOUND("Player not found online."),
    PLAYER_COMMAND("&cThis command is only available to players."),
    WRONG_USAGE("Wrong usage."),
    ALREADY_EXISTS("&calready exists."),
    NO_SELECTION("&cCould not find any selection."),
    ARGS_WARNING("&cYou can't add more than 15 arguments."),
    INVALID_CAMP_LOCATION("&cInvalid camp location."),
    CAMP_SET("&6Camp set to current location."),
    AVAILABLE_ITEMS("Available Items:"),
    SPECIFY_PLAYER("Specify a player."),
    SPECIFY_AMOUNT("Specify an amount."),
    SPECIFY_ITEM("Specify an item type."),
    NOT_VALID_ITEM("&cInvalid item."),
    NOT_VALID_AMOUNT("&cInvalid amount."),
    NOT_ENOUGH_SPACE("&cNot enough space."),
    INVALID_ZONE_LOCATION("&cInvalid spawn location."),
    ZONE_NOT_EXIST("&cZone does not exist."),
    ZONE_CREATED("&2has been created."),
    ZONE_REMOVED("&chas been removed."),
    ZONE_MAX("&cYou can't create more than 30 zones."),
    SPECIFY_ZONE("Specify a zone name."),
    SPECIFY_ZONE_TYPE("Specify a zone type."),
    AVAILABLE_ZONES("Available Zones:"),
    NO_AVAILABLE_ZONES("There aren't any available zones."),
    SPAWNPOINT_SET("&6Spawnpoint has been set."),
    TRACKING("&7Now tracking:"),
    GAVE("&7You successfuly gave"),
    THIRST("&6Thirst:"),
    THIRST_NOT_AVAILABLE("&cThirst is not available."),
    THIRST_REFILLED("&6Your thirst has been set back to 100%"),
    THIRST_REFILLED_OTHERS("&6You set %player% thirst back to 100%"),
    TELEPORTING("&6Teleporting..."),
    TELEPORTING_COOLDOWN("&6Teleporting in a few seconds."),
    INFECTED_WATER_BOTTLE("Infected Water Bottle"),
    INFECTED_WATER_BOTTLE_EFFECTS("&cInfection II"),
    DYING_FROM_INFECTION("&cYou can't do that while you are dying from infection."),
    INFECTED_LEG("&cYou have been bitten in the leg. Find a knife to perform amputation before you die from infection."),
    INFECTED_NECK("&cYou have been bitten badly in the neck! You have to find a cure and use it before you die from infection."),
    NOT_INFECTED("&cYou are not infected to use that."),
    ANTIBIOTICS_WARNING("&cYou can't use an antibiotic for your infection type."),
    ANTIBIOTICS_CURE("&6You successfully cured yourself by using antibiotics."),
    KNIFE_NOT_ENOUGH_HEALTH("&cYou don't have enough health to perform an amputation."),
    KNIFE_CURE("&cYou cured yourself by performing an amputation on your leg. Unfortunately, you are now bleeding and you need to find a splint to heal yourself before it's too late."),
    BOUGHT("&7You bought &c%amount% %item% &7for &e%price%$"),
    TRACK_COMPASS_TITLE("&3Track"),
    BACKPACK_TITLE("&3Backpack"),
    BANDAGE_BLEEDING_HEAL("&2You used a bandage to heal your leg from the amputation bleeding."),
    TIME_AND("and"),
    TIME_MINUTE("minute"),
    TIME_MINUTES("minutes"),
    TIME_SECOND("second"),
    TIME_SECONDS("seconds");

    private String type;
    private DeadSociety plugin;

    Lang(String type) {
        this.type = type;
        plugin = DeadSociety.getPlugin(DeadSociety.class);
    }

    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String get() {
        String value = plugin.getLanguageConfig().getString(getKey());
        if (value == null) {
            plugin.getLogger().warning("Missing lang message data: " + getKey());
            value = "&c[Missing lang message data - '" + get() + "']";
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }
}

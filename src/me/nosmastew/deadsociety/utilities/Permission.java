package me.nosmastew.deadsociety.utilities;

public enum Permission {
    DS_COMMANDS("ds.commands"),
    DS_PERMISSIONS("ds.permissions"),
    DS_RELOAD("ds.reload"),
    DS_USE("ds.use"),
    KNIFE("ds.knife"),
    FLASHLIGHT("ds.flashlight"),
    ANTIBIOTICS("ds.antibiotics"),
    PAINKILLERS("ds.painkillers"),
    BANDAGE("ds.bandage"),
    ITEMS_OP("ds.items.*"),
    ITEMS_COMMAND("ds.items.cmd"),
    COMPASS("ds.compass"),
    TRACK_CAMP("ds.track.camp"),
    TRACK_ZONE("ds.track.zone"),
    TRACK_OP("ds.track.*"),
    ZONE_USE("ds.zone.use"),
    ZONE_LIST("ds.zone.list"),
    ZONE_TELEPORT("ds.zone.teleport"),
    ZONE_CREATE("ds.zone.create"),
    ZONE_REMOVE("ds.zone.remove"),
    ZONE_SPAWN("ds.zone.spawn"),
    ZONE_IMMUNE("ds.zone.immune"),
    ZONE_OP("ds.zone.*"),
    ZONE_SIGN_USE("ds.zone.sign.use"),
    ZONE_SIGN_CREATE("ds.zone.sign.create"),
    ZONE_SIGN_BREAK("ds.zone.sign.break"),
    ZONE_SIGN_OP("ds.zone.sign.*"),
    ITEM_SHOP_CREATE("ds.items.shop.create"),
    ITEM_SHOP_BREAK("ds.items.shop.break"),
    ITEM_SHOP_USE("ds.items.shop.use"),
    ITEM_SHOP_INFINITE("ds.items.infinite"),
    ITEM_SHOP_OP("ds.items.shop.*"),
    INFECTION_COMMANDS_USE("ds.infection.cmd"),
    INFECTION_IMMUNE("ds.infection.immune"),
    FALL("ds.fall"),
    WATER_BOTTLE("ds.waterbottle"),
    IRON_DOOR("ds.irondoor"),
    CAMP("ds.camp.set"),
    BLOCK_BREAK("ds.break"),
    BLOCK_PLACE("ds.place"),
    BLOCK_OPEN("ds.open"),
    BACKPACK("ds.backpack"),
    BACKPACK_KEEP("ds.backpack.keep"),
    BACKPACK_OP("ds.backpack.*"),
    THIRST("ds.thirst"),
    THIRST_HELP("ds.thirst.help"),
    THIRST_REFILL("ds.thirst.refill"),
    THIRST_REFILL_OTHERS("ds.thirst.refill.others"),
    THIRST_OP("ds.thirst.*"),
    OP("ds.*");

    private String type;

    Permission(String type) {
        this.type = type;
    }

    public String get() {
        return this.type;
    }
}
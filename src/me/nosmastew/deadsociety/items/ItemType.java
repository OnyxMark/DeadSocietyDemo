package me.nosmastew.deadsociety.items;

public enum ItemType {

    ANTIBIOTICS,
    PAINKILLERS,
    BANDAGE,
    FLASHLIGHT,
    KNIFE;

    ItemType() {

    }

    public String get() {
        return "custom-items." + name().toLowerCase().replace("_", "-");
    }
}

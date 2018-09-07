package me.nosmastew.deadsociety.nms;

import org.bukkit.entity.Player;

public interface Title {

    void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);
}

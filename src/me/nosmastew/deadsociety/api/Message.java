package me.nosmastew.deadsociety.api;

import me.nosmastew.deadsociety.nms.Title;
import me.nosmastew.deadsociety.nms.v1_10_R1.Title_v1_10_R1;
import me.nosmastew.deadsociety.nms.v1_11_R1.Title_v1_11_R1;
import me.nosmastew.deadsociety.nms.v1_12_R1.Title_v1_12_R1;
import me.nosmastew.deadsociety.nms.v1_9_R2.Title_v1_9_R2;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Message {

    private Title title;
    private Player player;
    private String message;

    private MessageType type;

    public Message(Player player, MessageType type, String message) {
        this.title = getTitle();
        this.player = player;
        this.message = message;
        this.type = type;
    }

    public void send() {
        switch (this.type) {
            case TITLE:
                title.sendTitle(player, "", message, 5, 60, 5);
                break;
            case ACTION_BAR:
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                break;
            case CHAT:
                player.sendMessage(message);
                break;
            case NONE:
                break;
        }
    }

    private Title getTitle() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException versionException) {
            return title;
        }
        switch (version) {
            case "v1_12_R1":
                title = new Title_v1_12_R1();
                break;
            case "v1_11_R1":
                title = new Title_v1_11_R1();
                break;
            case "v1_10_R1":
                title = new Title_v1_10_R1();
                break;
            case "v1_9_R2":
                title = new Title_v1_9_R2();
                break;
        }
        return title;
    }
}
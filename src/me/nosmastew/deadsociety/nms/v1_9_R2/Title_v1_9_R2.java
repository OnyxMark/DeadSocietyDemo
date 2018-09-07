package me.nosmastew.deadsociety.nms.v1_9_R2;

import me.nosmastew.deadsociety.nms.Title;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class Title_v1_9_R2 implements Title {

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PacketPlayOutTitle times;
        if (title != null) {
            times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, CraftChatMessage.fromString(title)[0]);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(times);
        }
        if (subtitle != null) {
            times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(subtitle)[0]);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(times);
        }
        times = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(times);
    }
}

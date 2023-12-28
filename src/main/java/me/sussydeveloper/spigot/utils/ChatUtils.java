package me.sussydeveloper.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static String filter(String message){return ChatColor.translateAlternateColorCodes('&', message);}
    public static void sendMessage(String message, Player player){player.sendMessage(filter(message));}
    public static void sendMessage(String message, CommandSender sender){sender.sendMessage(filter(message));}
    public static void sendTitle(String titleMessage, String message, Player player, int fadeIn, int fadeOut, int stayTime){
        player.sendTitle(filter(titleMessage), filter(message), fadeIn, stayTime, fadeOut);
    }
}

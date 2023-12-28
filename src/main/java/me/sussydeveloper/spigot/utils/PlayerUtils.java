package me.sussydeveloper.utils;

import org.bukkit.entity.Player;

public class PlayerUtils {
    public static String getUUID(Player player){
        return player.getUniqueId().toString();
    }
}

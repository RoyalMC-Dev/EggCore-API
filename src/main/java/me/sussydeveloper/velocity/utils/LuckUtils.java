package me.verfiedfemboy.velocitycore.utils;

import com.velocitypowered.api.proxy.Player;
import me.verfiedfemboy.velocitycore.VelocityCore;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;

public class LuckUtils {

    public static String getPrefix(Player player) {
        User user = VelocityCore.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            CachedDataManager cachedData = user.getCachedData();
            return cachedData.getMetaData().getPrefix();
        }
        return "";
    }

    public static String getPrefixWithName(Player player){
        User user = VelocityCore.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        if(user != null){
            CachedDataManager cachedData = user.getCachedData();
            String prefix = cachedData.getMetaData().getPrefix();
            String playerName = player.getUsername();
            return prefix + " " + playerName;
        }
        return "";
    }

}

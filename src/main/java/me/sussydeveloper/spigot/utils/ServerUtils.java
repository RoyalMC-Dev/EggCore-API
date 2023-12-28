package me.sussydeveloper.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ServerUtils {

    public static Double[] getPlayerCords(Player p){
        Location loc = p.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return new Double[]{x, y, z};
    }
    public static boolean validateArguments(String[] args, int number, CommandSender sender, String message){
        if(args.length < number){
            ChatUtils.sendMessage(message, sender);
            return false;
        }
        return true;
    }

    public static ItemStack getItemStack(Material material, String displayName){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(displayName != null)
            itemMeta.setDisplayName(ChatUtils.filter(displayName));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getItemStack(Material material, String displayName, List<String> description){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(displayName != null)
            itemMeta.setDisplayName(ChatUtils.filter(displayName));
        if(description != null)
            itemMeta.setLore(description);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}

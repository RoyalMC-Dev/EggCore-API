package me.sussydeveloper.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class InventoryUtils {

    public static InventoryView openGUI(Player player, String titleName){
        Inventory inventory = Bukkit.createInventory(null, 27, ChatUtils.filter(titleName));
        return player.openInventory(inventory);
    }

    public static InventoryView openGUI(Player player, String titleName, int size) {
        size = Math.min(size, 54);
        Inventory inventory = Bukkit.createInventory(null, size, ChatUtils.filter(titleName));
        return player.openInventory(inventory);
    }
}

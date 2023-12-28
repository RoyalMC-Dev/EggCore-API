package me.sussydeveloper.commands.title;

import me.sussydeveloper.utils.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player playerSender = (Player) sender;
        if(args.length == 0){
            ChatUtils.sendMessage("&cPoprawny argument: /tytul <tekst>", playerSender);
            return false;
        }
        switch (args[0]){
            case "admin":
                break;
            default:
                InventoryView view = InventoryUtils.openGUI(playerSender, "&7Wybór tytułu", 27 * 2);
                int x = 10;
                for(TitleColor tc : TitleColor.Cache.getTitleColorsCache().values()){
                    List<String> lore = new ArrayList<>();
                    lore.add("");

                    boolean isOnSale = tc.isOnSale();
                    String NormalPrice = "&6" + FormatUtils.FloatFormat(tc.getColorPrice()) + " zł";
                    float sale_price = MathUtils.calculateDiscountedPrice(tc.getColorPrice(), tc.getPriceOnSale());

                    String onSalePrice = "&8&m" + FormatUtils.FloatFormat(tc.getColorPrice()) + "&6 " +
                            FormatUtils.FloatFormat(sale_price) + " zł" + " &b&l&o(-" + FormatUtils.FloatFormat(tc.getPriceOnSale()) + "%)";

                    String priceMSG = "&r&fCena: " + (isOnSale ? onSalePrice : NormalPrice);

                    lore.add(ChatUtils.filter(priceMSG));

                    lore.add(ChatUtils.filter("&r&fPodgląd: "));
                    String color_prefix = tc.getColorName().equalsIgnoreCase("Rainbow") ? ColorUtils.getRainbow("[" + args[0] + "]") : tc.getColorPrefix() + "[" + args[0] + "]";
                    lore.add(ChatUtils.filter(tc.getColorPrefix() + color_prefix + " &7" + playerSender.getName()));
                    String displayName = tc.getColorName().equalsIgnoreCase("Rainbow") ? ColorUtils.getRainbow(tc.getColorName()) : tc.getColorPrefix() + tc.getColorName();
                    view.setItem(x,
                            ServerUtils.getItemStack(Material.OAK_SIGN, displayName, lore));
                    x++;
                    if(x == 17){
                        x = 19;
                    }
                    if(x == 26){
                        x = 28;
                    }
                }
                for(int i = 45; i <= 53; i++){
                    view.setItem(i, ServerUtils.getItemStack(Material.BLACK_STAINED_GLASS_PANE, ChatUtils.filter("&d:3")));
                }
                break;
        }

        return false;
    }

    public static class InventoryEvent implements Listener {

        @EventHandler
        public void onInvClick(InventoryClickEvent event){
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            Player player = (Player) event.getWhoClicked();

            Inventory clickedInventory = event.getClickedInventory();
            if(clickedInventory != null && clickedInventory.equals(player.getOpenInventory().getTopInventory())){
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                    return;
                }
            }


            //TODO: Click Actions
        }
    }
}

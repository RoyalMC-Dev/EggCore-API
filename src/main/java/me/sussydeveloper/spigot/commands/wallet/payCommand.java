package me.sussydeveloper.commands.wallet;

import me.sussydeveloper.utils.ChatUtils;
import me.sussydeveloper.utils.FormatUtils;
import me.sussydeveloper.utils.WalletUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class payCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            ChatUtils.sendMessage("&cNie możesz użyć tej komendy, ponieważ należy tylko do gracza.", commandSender);
            return false;
        }
        Player sender = (Player) commandSender;
        if (args.length == 2) {
            String targetName = args[0];
            String value = args[1];
            float newValue = WalletUtils.getFloatValue(value);
            WalletData data = WalletData.Cache.getWalletDataCache().get(sender.getUniqueId().toString());
            if(data.doesUUIDExist(data.getUuid())){
                if(newValue > 0.1F){
                    if(Float.parseFloat(value) <= data.getWallet()){
                        String valueFormat = FormatUtils.FloatFormat(newValue);
                        data.updateWallet(Float.parseFloat(value), true);
                        ChatUtils.sendMessage("&aPrzelano &6" + valueFormat + " zł &agraczowi " + targetName, sender);
                        Player target = Bukkit.getPlayer(targetName);

                        data = WalletData.Cache.getWalletDataCache().get(WalletUtils.getTargetUUID(targetName));
                        data.updateWallet(Float.parseFloat(value), false);

                        ChatUtils.sendMessage("&aOtrzymałeś &6" + valueFormat + " zł &aod " + targetName + "\nSprawdz portfel", target);
                        ChatUtils.sendTitle("&6&lPortfel", "&aOtrzymałeś &6" + valueFormat + "zł &aod &6" + sender.getName(), target, 5, 5, 80);
                    }else{
                        ChatUtils.sendMessage("&cNie masz wystarczająco pieniędzy. Wpisz &e/doladuj &caby doładować swój portfel.", sender);
                    }
                }else{
                    ChatUtils.sendMessage("&cWartość musi być większa od 0.1.", sender);
                }
            }else{
                ChatUtils.sendMessage("&cTen gracz nigdy nie był na serwerze.", sender);
            }
        } else {
            ChatUtils.sendMessage("&cNie poprawny argument: /przelej <gracz> <ilość>", sender);
            return false;
        }
        return false;
    }



    public static class payTabCompleter implements TabCompleter {
        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            List<String>comp = new ArrayList<>();
            if(args.length == 1){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    comp.add(player.getName());
                });
            }
            return comp;
        }
    }
}

package me.sussydeveloper.commands.wallet;

import me.sussydeveloper.utils.ChatUtils;
import me.sussydeveloper.utils.ServerUtils;
import me.sussydeveloper.utils.WalletUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class walletCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player sender = (Player) commandSender;
        if(args.length >= 1){
            if(sender.hasPermission("eggcore.wallet.admin")){
                switch (args[0]){
                    case "add": {
                        if(!ServerUtils.validateArguments(args, 3, commandSender, "&cPoprawny argument: /wallet add <gracz> <ilość>"))
                            return true;
                        String targetName = args[1];
                        WalletData data = WalletUtils.getWalletDataByUUID(WalletUtils.getTargetUUID(targetName));
                        if(data != null) {
                            float value = WalletUtils.getFloatValue(args[2]);
                            if(!(value >= 0.1F)){
                                ChatUtils.sendMessage("&cWartość musi być większa od 0.1", commandSender);
                                return false;
                            }
                            data.updateWallet(value, false);
                        } else {
                            ChatUtils.sendMessage("&cTen gracz nigdy nie był na serwerze", commandSender);
                            return true;
                        }
                        ChatUtils.sendMessage(String.format("&aPomylśnie przelano %s do portfela gracza " + targetName, Float.parseFloat(args[2])), commandSender);
                        break;
                    }
                    case "remove": {
                        if(!ServerUtils.validateArguments(args, 3, commandSender, "&cPoprawny argument: /wallet remove <gracz> <ilość>"))
                            return true;
                        String targetName = args[1];
                        WalletData data = WalletUtils.getWalletDataByUUID(WalletUtils.getTargetUUID(targetName));
                        if(data != null)
                            data.updateWallet(Float.parseFloat(args[2]), true);
                        else {
                            ChatUtils.sendMessage("&cTen gracz nigdy nie był na serwerze", sender);
                            return true;
                        }
                        ChatUtils.sendMessage(String.format("&aPomylśnie odjęto %s z portfela gracza " + targetName, Float.parseFloat(args[2])), sender);
                        break;
                    }
                    case "reset":{
                        if(!ServerUtils.validateArguments(args, 2, commandSender, "&cPoprawny argument: /wallet reset <gracz>"))
                            return true;
                        String targetName = args[1];
                        WalletData data = WalletUtils.getWalletDataByUUID(WalletUtils.getTargetUUID(targetName));
                        if(data != null)
                            data.resetWallet();
                        else {
                            ChatUtils.sendMessage("&cTen gracz nigdy nie był na serwerze", sender);
                            return true;
                        }
                        ChatUtils.sendMessage("&aPomylśnie zresetowano portfel gracza " + targetName, sender);
                        break;
                    }
                    case "set":{
                        if(!ServerUtils.validateArguments(args, 3, commandSender, "&cPoprawny argument: /wallet set <gracz> <ilość>"))
                            return true;
                        String targetName = args[1];
                        WalletData data = WalletUtils.getWalletDataByUUID(WalletUtils.getTargetUUID(targetName));
                        if(data != null)
                            data.setWallet(Float.parseFloat(args[2]));
                        else {
                            ChatUtils.sendMessage("&cTen gracz nigdy nie był na serwerze", sender);
                            return true;
                        }
                        ChatUtils.sendMessage(String.format("&aPomylśnie ustawiono %s w portfelu gracza " + targetName, Float.parseFloat(args[2])), sender);
                        break;
                    }
                }
            }else{
                ChatUtils.sendMessage("&cNie masz permisji do tej komendy.", sender);
            }
        }else{
            WalletData data = WalletData.Cache.getWalletDataCache().get(sender.getUniqueId().toString());
            ChatUtils.sendMessage("&6Twój stan portfela: " + data.getWalletFormatted(), sender);
        }
        return false;
    }



}

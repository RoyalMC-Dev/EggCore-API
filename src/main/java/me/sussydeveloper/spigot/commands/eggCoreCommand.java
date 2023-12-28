package me.sussydeveloper.commands;

import me.sussydeveloper.Main;
import me.sussydeveloper.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class eggCoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length >= 1){
            switch (args[0]){
                case "help":
                    sender.sendMessage(ChatUtils.filter("&c&lHelp:"));
                    break;
                case "reload":
                    //Config reload
                    sender.sendMessage(ChatUtils.filter("&a&lPrzeładowano config poprawnie."));
                    Main.reload();
                    break;
            }
        }else{
            sender.sendMessage(ChatUtils.filter("&c&lHelp:"));
        }
        return false;
    }
}

package me.sussydeveloper.commands.wallet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class walletTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> comp = new ArrayList<>();
        if(commandSender.hasPermission("eggcore.wallet.admin")){
            if(args.length == 1){
                comp.add("add");
                comp.add("remove");
                comp.add("set");
                comp.add("reset");
            }
            if(args.length == 2){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    comp.add(player.getName());
                });
            }
        }

        return comp;
    }
}

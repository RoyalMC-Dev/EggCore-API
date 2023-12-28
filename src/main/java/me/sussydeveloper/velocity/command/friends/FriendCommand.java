package me.verfiedfemboy.velocitycore.command.friends;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.config.ConfigManager;
import me.verfiedfemboy.velocitycore.utils.ChatUtils;
import me.verfiedfemboy.velocitycore.utils.ConsoleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class FriendCommand implements SimpleCommand {

    final ProxyServer proxyServer;

    public FriendCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        if(!(source instanceof Player)) {
            ConsoleUtils.sendMessage(source, "<red>Ta komenda może zostać wykonana tylko przez Gracza.");
            return;
        }
        Player player = (Player) source;

        String[] args = invocation.arguments();
        if(args.length >= 1){
            switch (args[0]){
                case "help":
                    ChatUtils.sendMessage(player, ConfigManager.getConfig("friends").getString("friend-help-message"));
                    break;
                case "invite":
                case "zapros":
                case "add":
                case "dodaj": {
                    String targetName = args[1];
                    if(targetName == null) {
                        ChatUtils.sendMessage(player, "<red>Zabrakło argumentów. Poprawny zapis: /friend add <gracz>");
                        return;
                    }
                    FriendUtils.Invite(player, targetName, proxyServer);
                    break;
                }
                case "accept":
                case "akceptuj":{
                    String targetName = args[1];
                    if(targetName == null) {
                        ChatUtils.sendMessage(player, "<red>Zabrakło argumentów. Poprawny zapis: /friend accept <gracz>");
                        return;
                    }
                    FriendUtils.Accept(player, targetName, proxyServer);
                    break;
                }
                case "deny":
                case "odrzuc":{
                    String targetName = args[1];
                    if(targetName == null) {
                        ChatUtils.sendMessage(player, "<red>Zabrakło argumentów. Poprawny zapis: /friend deny <gracz>");
                        return;
                    }
                    FriendUtils.Deny(player, targetName, proxyServer);
                    break;
                }
                case "remove":
                case "usun": {
                    String targetRemoverName = args[1];
                    if(targetRemoverName == null) {
                        ChatUtils.sendMessage(player, "<red>Zabrakło argumentów. Poprawny zapis: /friend remove <gracz>");
                        return;
                    }
                    FriendUtils.Remove(player, targetRemoverName);
                    break;
                }
                case "ignore":
                case "block":
                case "zablokuj": {
                    String targetBlockerName = args[1];
                    if(targetBlockerName == null) {
                        ChatUtils.sendMessage(player, "<red>Zabrakło argumentów. Poprawny zapis: /friend block <gracz>");
                        return;
                    }
                    FriendUtils.Block(player, targetBlockerName);
                    break;
                }
                case "unblock":
                case "odblokuj":{
                    String targetUnblockerName = args[1];
                    if(targetUnblockerName == null) {
                        ChatUtils.sendMessage(player, "<red>Zabrakło argumentów. Poprawny zapis: /friend unblock <gracz>");
                        return;
                    }
                    FriendUtils.Unblock(player, targetUnblockerName);
                    break;
                }
                case "list":
                case "lista":
                    FriendUtils.List(player, proxyServer);
                    break;
                default:{
                    String targetAddName = args[0];
                    FriendUtils.Invite(player, targetAddName, proxyServer);
                    break;
                }

            }
        }else{
            ChatUtils.sendMessage(player, ConfigManager.getConfig("friends").getString("friend-help-message"));
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();
        CopyOnWriteArrayList<String> friendSuggests = new CopyOnWriteArrayList<>(Arrays.asList(
                "add", "dodaj", "remove", "usun", "block", "zablokuj", "unblock", "odblokuj", "list", "lista", "info"
        ));
        if(args.length == 2){
            CopyOnWriteArrayList<String> playersName = new CopyOnWriteArrayList<>();
            Player sender = (Player) invocation.source();
            FriendData data = new FriendData(sender.getUsername(), sender.getUniqueId().toString());
            switch (args[0]){
                case "remove":
                case "usun":
                case "block":
                case "zablokuj":
                    for(String player : data.getFriendList()){
                        playersName.add(FriendUtils.getPlayerNameByUUID(player));
                    }
                    break;
                case "odblokuj":
                case "unblock":
                    for(String player : data.getBlockedPlayers()){
                        playersName.add(FriendUtils.getPlayerNameByUUID(player));
                    }
                    break;
                default:
                    for(Player player : proxyServer.getAllPlayers()){
                        playersName.add(player.getUsername());
                    }
                    break;
            }


            return CompletableFuture.completedFuture(playersName);
        }
        return CompletableFuture.completedFuture(friendSuggests);
    }

    public List<String> getSuggestions(String partialName) {
        List<String> suggestions = new ArrayList<>();

        List<Player> onlinePlayers = (List<Player>) proxyServer.getAllPlayers();

        for (Player player : onlinePlayers) {
            String playerName = player.getUsername();
            if (playerName.toLowerCase().startsWith(partialName.toLowerCase())) {
                suggestions.add(playerName);
            }
        }

        return suggestions;
    }
}

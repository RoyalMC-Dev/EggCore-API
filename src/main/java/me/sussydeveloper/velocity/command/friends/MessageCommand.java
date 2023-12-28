package me.verfiedfemboy.velocitycore.command.friends;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.config.ConfigManager;
import me.verfiedfemboy.velocitycore.utils.ChatUtils;
import net.luckperms.api.LuckPerms;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MessageCommand implements SimpleCommand {

    final ProxyServer proxyServer;
    final LuckPerms luckPerms;

    public MessageCommand(ProxyServer proxyServer, LuckPerms luckPerms) {
        this.proxyServer = proxyServer;
        this.luckPerms = luckPerms;
    }

    @Override
    public void execute(final Invocation invocation) {
        String[] args = invocation.arguments();
        Player sender = (Player) invocation.source();
        if(args.length > 1){
            Optional<Player> player = proxyServer.getPlayer(args[0]);
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i <= args.length; i++){
                sb.append(args[i]).append(" ");
            }
            String message = sb.toString();
            player.ifPresentOrElse(target -> {
                if(!target.equals(sender)){
                    String targetName = args[0];
                    FriendData friendBlock = FriendData.Cache.getFriendsCache().get(FriendUtils.getTargetUUID(targetName));
                    if(FriendUtils.isPlayerAlreadyBlocked(friendBlock.getBlockedPlayers(), sender.getUniqueId().toString())){
                        ChatUtils.sendMessage(sender, "<red>Jesteś zablokowany przez tego użytkownika.");
                        return;
                    }
                    String finalMessage = ConfigManager.getConfig("friends").getString("sender-and-reciver-msg-format");
                    finalMessage = finalMessage.replace("%sender%", sender.getUsername());
                    finalMessage = finalMessage.replace("%target%", target.getUsername());
                    finalMessage = finalMessage.replace("%message%", message);
                    //To targeted player
                    ChatUtils.sendMessage(target, finalMessage);
                    //To sender
                    ChatUtils.sendMessage(sender, finalMessage);
                }else {
                    ChatUtils.sendMessage(sender, "<red>Nie możesz wysyłać do siebie żadnych wiadomości.");
                }
            }, () -> ChatUtils.sendMessage(sender, "<red>Ten gracz nie jest online."));
        }else{
            ChatUtils.sendMessage(sender, "<red>Musisz podać argument. /msg <gracz> <wiadomosc>");
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        ArrayList<String> playersName = new ArrayList<>();
        for(Player player : proxyServer.getAllPlayers()){
            Player sender = (Player) invocation.source();
            if(!player.getUsername().equalsIgnoreCase(sender.getUsername())){
                playersName.add(player.getUsername());
            }
        }
        return CompletableFuture.completedFuture(playersName);
    }
}

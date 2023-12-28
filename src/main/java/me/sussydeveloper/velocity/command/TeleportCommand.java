package me.verfiedfemboy.velocitycore.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.verfiedfemboy.velocitycore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

public class TeleportCommand implements SimpleCommand {

    Component parse;
    MiniMessage miniMessage = MiniMessage.miniMessage();

    final ProxyServer proxy;

    public TeleportCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Player player = (Player) source;

        if(!player.hasPermission("velocity.core.bungeetp")){
            String msg = "<red>Nie masz permisji do tej komendy.";
            parse = miniMessage.deserialize(msg);
            player.sendMessage(parse);
            return;
        }
        if(args.length >= 1){
            String targetName = args[0];

            Optional<Player> optionalTarget = proxy.getPlayer(targetName);
            if (!optionalTarget.isPresent()) {
                ChatUtils.sendMessage(player, "<red> Gracz o podanej nazwie nie jest online.");
                return;
            }

            proxy.getPlayer(targetName).ifPresent(target ->{
                RegisteredServer registeredServer = target.getCurrentServer().get().getServer();
                player.createConnectionRequest(registeredServer).connect().thenRun(()->{
                    ChatUtils.sendMessage(player, "<green>Teloportowano Cię do gracza: " + targetName + "\nServer: " + target.getCurrentServer().get().getServer().getServerInfo().getName());
                });
            });
        }else {
            ChatUtils.sendMessage(player, "<red>Brakuje jednego argumentu. Poprawne użycie: /btp <gracz>");
        }
    }
}

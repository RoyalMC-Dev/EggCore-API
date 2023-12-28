package me.verfiedfemboy.velocitycore.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class LobbyCommand implements SimpleCommand {

    private final ProxyServer server;

    Component parse;
    MiniMessage miniMessage = MiniMessage.miniMessage();

    public LobbyCommand(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        Player player = (Player) source;

        if (!(source instanceof Player)) {
            parse = miniMessage.deserialize("<red>Ta komenda moze tylko zostac uzyta przez Gracza lub Administratora");
            return;
        }

        Random rand = new Random();
        List<String> TargetServers = ConfigManager.getConfig("config").getList("lobbies");
        String RandServer = TargetServers.get(rand.nextInt(TargetServers.size()));

        if(!server.getServer(RandServer).isPresent()){
            parse = miniMessage.deserialize("<red>Dołączanie się nie powiodło. Prawdopodobnie serwer (" + RandServer + ") jest wyłączony lub nie istnieje.");
            player.sendMessage(parse);
            return;
        }

        if (server.getServer(RandServer).get().getPlayersConnected().contains(player)) {
            parse = miniMessage.deserialize("<red>Wybacz przyjacielu, ale już jesteś na lobby.");
            source.sendMessage(parse);
            return;
        }

        Optional<RegisteredServer> TargetServer = server.getServer(RandServer);
        parse = miniMessage.deserialize("<green>Dołączanie do Lobby (" + RandServer + ")...");
        source.sendMessage(parse);
        player.createConnectionRequest(TargetServer.get()).fireAndForget();

    }
}

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

import java.util.HashMap;
import java.util.UUID;

public class LiveCommand implements SimpleCommand {

    private final ProxyServer server;

    Component parse;
    MiniMessage miniMessage = MiniMessage.miniMessage();

    HashMap<UUID, Long> cooldown = new HashMap<>();

    public LiveCommand(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Player player = (Player) source;

        if (!(source instanceof Player)) {
            parse = miniMessage.deserialize("<red>Ta komenda moze tylko zostac uzyta przez Gracza lub Administratora");
            return;
        }

        if(!player.hasPermission("velocity.core.live")){
            parse = miniMessage.deserialize("<red>Nie masz permisji do tej komendy!");
            player.sendMessage(parse);
            return;
        }

        if(cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis() && !player.hasPermission("velocity.core.live.cooldown")){
            long remaningTime = cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
            parse = miniMessage.deserialize("<red>Jeszcze pozostało <yellow>" + remaningTime / 1000 + "</yellow> sekund</red>");
            player.sendMessage(parse);
            return;
        }

        if(args.length >= 1){
            String link = args[0];
            if(!link.contains("https://")) {
                parse = miniMessage.deserialize("<red>Link musi zawierać https://");
                player.sendMessage(parse);
                return;
            }
            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (ConfigManager.getConfig("config").getLong("live-cooldown") * 1000));
            for (RegisteredServer s : server.getAllServers()){
                String msg = ConfigManager.getConfig("config").getString("wiadomosc-live");
                msg = msg.replace("%player%", player.getUsername());
                msg = msg.replace("%link%", link);

                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                    sb.append(args[i] + " ");

                msg = msg.replace("%msg%", sb);
                parse = miniMessage.deserialize(msg);
                s.sendMessage(parse);
            }
        }else{
            parse = miniMessage.deserialize("<red>Poprawne argumenty /live <link> <tekst>");
            player.sendMessage(parse);
        }

    }
}

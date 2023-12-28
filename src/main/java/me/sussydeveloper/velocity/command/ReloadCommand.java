package me.verfiedfemboy.velocitycore.command;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.verfiedfemboy.velocitycore.VelocityCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.nio.file.Path;

public class ReloadCommand implements SimpleCommand {

    MiniMessage miniMessage = MiniMessage.miniMessage();
    Component parse;

    final Path path;

    public ReloadCommand(Path path) {
        this.path = path;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        Player player = (Player) source;
        if(!player.hasPermission("velocity.core.reload")){
            parse = miniMessage.deserialize("<red>Nie masz permisji do tej komendy.");
            player.sendMessage(parse);
            return;
        }
        update_config();
        parse = miniMessage.deserialize("<green>Config został zaaktualizowany.");
        player.sendMessage(parse);

    }

    public void update_config() {
        VelocityCore.configManager.update();
    }
}

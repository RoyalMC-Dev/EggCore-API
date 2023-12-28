package me.verfiedfemboy.velocitycore.utils;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ConsoleUtils {

    public static void sendMessage(CommandSource commandSource, String message){
        Component component;
        MiniMessage miniMessage = MiniMessage.miniMessage();
        component = miniMessage.deserialize(message);
        commandSource.sendMessage(component);
    }
}

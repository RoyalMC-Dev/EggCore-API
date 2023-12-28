package me.verfiedfemboy.velocitycore.utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class ChatUtils {

    public static void sendMessage(Player p, String message){
        Component component;
        MiniMessage miniMessage = MiniMessage.miniMessage();
        component = miniMessage.deserialize(message);
        p.sendMessage(component);
    }

    public static void sendMessageWithStyle(Player p, String message, String target){
        Component component;
        MiniMessage miniMessage = MiniMessage.miniMessage();
        component = miniMessage.deserialize(message, Placeholder.styling("accept", ClickEvent.runCommand("/friend accept " + target)),
                Placeholder.styling("deny", ClickEvent.runCommand("/friend deny " + target)),
                Placeholder.styling("ignore", ClickEvent.runCommand("/friend ignore " + target)));
        p.sendMessage(component);
    }


}

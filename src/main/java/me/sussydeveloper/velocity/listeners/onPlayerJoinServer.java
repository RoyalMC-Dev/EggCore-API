package me.verfiedfemboy.velocitycore.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import me.verfiedfemboy.velocitycore.command.friends.FriendData;
import me.verfiedfemboy.velocitycore.command.friends.FriendUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class onPlayerJoinServer {
    

    public onPlayerJoinServer() {
    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onPlayerJoin(PostLoginEvent event){
        String playerName = event.getPlayer().getUsername();
        String UUID = event.getPlayer().getUniqueId().toString();
        FriendData friendData = new FriendData(playerName, UUID);
        FriendData.Cache.createUserIfNotExists(friendData.getPlayerName(), friendData.getUuid());

        FriendData friendRequest = FriendData.Cache.getFriendsCache().get(UUID);
        List<String> requestList = friendRequest.getInviteRequests();
        if(requestList != null){
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(() -> {
                String message = String.format("<list><red><bold>[<dark_red>!</dark_red>]</bold></red> <yellow>Masz %s zaproszeń do znajomych", requestList.size());
                Component component;
                MiniMessage miniMessage = MiniMessage.miniMessage();
                ArrayList<String> inviteRequests = new ArrayList<>();

                for(String request : friendRequest.getInviteRequests())
                    inviteRequests.add(FriendUtils.getPlayerNameByUUID(request));

                component = miniMessage.deserialize(message, Placeholder.styling("list", HoverEvent.showText(
                        Component.text(String.join("\n", inviteRequests)))));
                event.getPlayer().sendMessage(component);
                service.shutdown();
            }, 1800, TimeUnit.MILLISECONDS);
        }
    }
}

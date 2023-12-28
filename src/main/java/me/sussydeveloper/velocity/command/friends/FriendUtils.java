package me.verfiedfemboy.velocitycore.command.friends;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.config.ConfigManager;
import me.verfiedfemboy.velocitycore.utils.ChatUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FriendUtils {
    public static void Invite(Player p, String target, ProxyServer proxyServer){
        if(!target.equals(p.getUsername())){
            FriendData friendData = FriendData.Cache.getFriendsCache().get(p.getUniqueId().toString());
            List<String> currentFriendList = friendData.getFriendList();

            String targetUUID = getTargetUUID(target);
            if(!isFriendAlreadyAdded(currentFriendList, targetUUID))
                SendRequest(p, target, proxyServer);
            else
                ChatUtils.sendMessage(p,"<red>Jest już na twojej liście znajomych.");
        }else{
            ChatUtils.sendMessage(p, "<red>Nie możesz siebie dodawać do znajomych!");
        }
    }
    public static void SendRequest(Player sender, String targetName, ProxyServer proxyServer) {
        Optional<Player> player = proxyServer.getPlayer(targetName);
        String targetUuid = getTargetUUID(targetName);
        String senderUuid = getTargetUUID(sender.getUsername());
        if (targetUuid == null) {
            ChatUtils.sendMessage(sender, "<red>Ten gracz nigdy nie był na naszym serwerze.");
            return;
        }
        FriendData friendBlock = FriendData.Cache.getFriendsCache().get(targetUuid);
        if(isPlayerAlreadyBlocked(friendBlock.getBlockedPlayers(), senderUuid)){
            ChatUtils.sendMessage(sender, "<red>Jesteś zablokowany u gracza " + targetName + ". Więc nie możesz wysyłać mu zaproszeń do znajomych.");
            return;
        }
        FriendData friendRequest = FriendData.Cache.getFriendsCache().get(targetUuid);
        //Sending invite request
        if (!isPlayerAlreadyRequested(friendRequest.getInviteRequests(), sender.getUniqueId().toString())) {
            player.ifPresent(target -> {
                String message = ConfigManager.getConfig("friends").getString("friend-receive-request");
                String accept_format = ConfigManager.getConfig("friends").getString("friend-accept-format");
                String deny_format = ConfigManager.getConfig("friends").getString("friend-deny-format");
                String ignore_format = ConfigManager.getConfig("friends").getString("friend-ignore-format");
                message = message.replace("%player%", sender.getUsername()).replace("%accept%", accept_format)
                        .replace("%deny%", deny_format).replace("%ignore%", ignore_format);

                //Sending request
                ChatUtils.sendMessageWithStyle(target, message, sender.getUsername());
            });

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(() -> {

                List<String> currentRequestsAfterDelay = friendRequest.getInviteRequests();
                if (isPlayerAlreadyRequested(currentRequestsAfterDelay, senderUuid)) {

                   friendRequest.RemoveRequest(senderUuid);

                    ChatUtils.sendMessage(sender, ConfigManager.getConfig("friends").getString("friend-deny-outtime-sender").replace("%player%", targetName));
                    Optional<Player> onlineTarget = proxyServer.getPlayer(targetName);
                    onlineTarget.ifPresent(target ->{
                        ChatUtils.sendMessage(target, ConfigManager.getConfig("friends").getString("friend-deny-outtime-reciver").replace("%player%", sender.getUsername()));
                    });
                }
                executorService.shutdown();
            }, ConfigManager.getConfig("friends").getLong("friend-accept-time"), TimeUnit.MINUTES);

            //Adding request to list
            friendRequest.addRequest(sender.getUniqueId().toString());

            String message = ConfigManager.getConfig("friends").getString("friend-send-request");
            message = message.replace("%player%", targetName);
            ChatUtils.sendMessage(sender, message);
        } else {
            ChatUtils.sendMessage(sender, "<red>Wysłałeś już zaproszenie.");
        }
    }
    public static void Accept(Player sender, String targetName, ProxyServer proxyServer){
        String senderName = sender.getUsername();
        String senderUUID = sender.getUniqueId().toString();
        String targetUUID = getTargetUUID(targetName);
        FriendData friendRequest = FriendData.Cache.getFriendsCache().get(senderUUID); //SenderUUID
        if(isPlayerAlreadyRequested(friendRequest.getInviteRequests(), targetUUID)){
            //Removing friend request
            friendRequest.RemoveRequest(targetUUID);
            //Adding target to sender's friendlist
            FriendData friendAdd = FriendData.Cache.getFriendsCache().get(senderUUID);
            friendAdd.addFriendToList(targetUUID);
            ChatUtils.sendMessage(sender, ConfigManager.getConfig("friends").getString("friend-sender-accept").replace("%player%", targetName));

            //Adding sender to target's list
            Optional<Player> targetPlayer = proxyServer.getPlayer(targetName);
            friendAdd = FriendData.Cache.getFriendsCache().get(targetUUID);
            friendAdd.addFriendToList(senderUUID);
            targetPlayer.ifPresent(target -> ChatUtils.sendMessage(target, ConfigManager.getConfig("friends").getString("friend-target-accept").replace("%player%", senderName)));
        }else{
            ChatUtils.sendMessage(sender, "<red>Nie ma żadnego zaproszenia od " + targetName + ".");
        }
    }
    public static void Deny(Player sender, String targetName, ProxyServer proxyServer){
        String targetUUID = getTargetUUID(targetName);
        String senderName = sender.getUsername();
        String senderUUID = sender.getUniqueId().toString();
        FriendData friendRequest = FriendData.Cache.getFriendsCache().get(senderUUID);
        List<String> currentRequestList = friendRequest.getInviteRequests();
        if(isPlayerAlreadyRequested(currentRequestList, targetUUID)){
            //Removing request from sender's list
            friendRequest.RemoveRequest(targetUUID);

            ChatUtils.sendMessage(sender, ConfigManager.getConfig("friends").getString("friend-sender-deny").replace("%player%", targetName));

            Optional<Player>targetPlayer = proxyServer.getPlayer(targetName);
            targetPlayer.ifPresent(target ->{
                ChatUtils.sendMessage(target, ConfigManager.getConfig("friends").getString("friend-target-deny").replace("%player%", senderName));
            });
        }else{
            ChatUtils.sendMessage(sender, "<red>Nie ma żadnego zaproszenia od " + targetName + ".");
        }
    }
    public static void Remove(Player p, String targetName){
        if(!targetName.equals(p.getUsername())){
            String message = ConfigManager.getConfig("friends").getString("friend-remove");
            message = message.replace("%player%", targetName);
            String targetUUID = getTargetUUID(targetName);
            if(isFriendAlreadyAdded(FriendData.Cache.getFriendsCache().get(p.getUniqueId().toString()).getFriendList(), targetUUID)){
                //Remove from sender's list
                FriendData friendRemove = FriendData.Cache.getFriendsCache().get(p.getUniqueId().toString());
                friendRemove.RemoveFriend(targetUUID);

                //Removing from target's list
                friendRemove = FriendData.Cache.getFriendsCache().get(targetUUID);
                friendRemove.RemoveFriend(p.getUniqueId().toString());
                ChatUtils.sendMessage(p, message);
            }else{
                ChatUtils.sendMessage(p, "<red>Nie masz takiego znajomego na liście znajomych.");
            }
        }else{
            ChatUtils.sendMessage(p, "<red>Nie możesz siebie usuwać ze znajomych!");
        }
    }
    public static void Block(Player p, String targetName){
        if(!targetName.equals(p.getUsername())){
            String message = ConfigManager.getConfig("friends").getString("friend-block");
            message = message.replace("%player%", targetName);
            String senderUUID = p.getUniqueId().toString();
            FriendData friendBlock = FriendData.Cache.getFriendsCache().get(senderUUID);

            List<String> currentBlockedList = friendBlock.getBlockedPlayers();
            //Getting targeted UUID from our database (znajomi)
            String targetUUID = getTargetUUID(targetName);
            if(targetUUID == null){
                ChatUtils.sendMessage(p, "<red>Ten gracz nigdy nie był na naszym serwerze.");
                return;
            }
            if (!isPlayerAlreadyBlocked(currentBlockedList, targetUUID)) {
                FriendData friendRemove = FriendData.Cache.getFriendsCache().get(senderUUID);
                List<String> currentFriendList = friendRemove.getFriendList();
                if(isFriendAlreadyAdded(currentFriendList, targetUUID)){
                    friendRemove.RemoveFriend(targetUUID);
                    friendRemove = FriendData.Cache.getFriendsCache().get(targetUUID);
                    friendRemove.RemoveFriend(senderUUID);
                }
                // Adding new Blocked to current list
                friendBlock.addBlocked(targetUUID);
                ChatUtils.sendMessage(p, message);
            } else {
                ChatUtils.sendMessage(p, "<red>Ten gracz już znajduje się na liście zablokowanych.");
            }
        }else {
            ChatUtils.sendMessage(p, "<red>Nie możesz siebie zablokować!");
        }
    }
    public static void Unblock(Player p, String target){
        if(!target.equals(p.getUsername())){
            String message = ConfigManager.getConfig("friends").getString("friend-unblock");
            message = message.replace("%player%", target);
            String uuid = p.getUniqueId().toString(); //Getting sender's uuid
            FriendData friendUnblock = FriendData.Cache.getFriendsCache().get(uuid);
            target = getTargetUUID(target);
            if(target == null){
                ChatUtils.sendMessage(p, "<red>Ten gracz nigdy nie był na naszym serwerze.");
                return;
            }
            if (isPlayerAlreadyBlocked(friendUnblock.getBlockedPlayers(), target)) {
                // Removing blocked
                friendUnblock.RemoveBlock(target);

                ChatUtils.sendMessage(p, message);
            } else {
                ChatUtils.sendMessage(p, "<red>Ten gracz się nie znajduje w liście zablokowanych.");
            }
        }else{
            ChatUtils.sendMessage(p, "<red>Nie możesz siebie odblokowywać!");
        }
    }
    public static void List(Player p, ProxyServer proxyServer) {
        String playerName = p.getUsername();
        String uuid = p.getUniqueId().toString();
        FriendData friendData = FriendData.Cache.getFriendsCache().get(uuid);
        List<String> friendListWithUUIDs = friendData.getFriendList();
        ArrayList<String> friendList = new ArrayList<>();

        Map<String, String> friendStatusMap = new HashMap<>();
        for (String friendUUID : friendListWithUUIDs) {
            String friendName = getPlayerNameByUUID(friendUUID);
            Optional<Player> friendPlayer = proxyServer.getPlayer(friendName);
            String status = friendPlayer.isPresent() ? "Online" : "Offline";
            friendStatusMap.put(friendName, status);
        }
        List<Map.Entry<String, String>> sortedFriendList = new ArrayList<>(friendStatusMap.entrySet());
        sortedFriendList.sort((friend1, friend2) -> {
            String status1 = friend1.getValue();
            String status2 = friend2.getValue();
            int weight1 = status1.equalsIgnoreCase("Online") ? 0 : 1;
            int weight2 = status2.equalsIgnoreCase("Online") ? 0 : 1;
            return Integer.compare(weight1, weight2);
        });
        for (Map.Entry<String, String> entry : sortedFriendList) {
            String friendName = entry.getKey();
            String status = entry.getValue();
            if (status.equalsIgnoreCase("Online")) {
                String serverName = proxyServer.getPlayer(friendName).flatMap(Player::getCurrentServer).get().getServerInfo().getName();
                friendList.add(friendName + " - <green>Online</green><gray>(" + serverName + ")</gray>");
            } else {
                friendList.add(friendName + " - <red>Offline</red>");
            }
        }

        if (!friendList.isEmpty()) {
            ChatUtils.sendMessage(p, ConfigManager.getConfig("friends").getString("friend-list")
                    .replace("%friends%", String.join("\n", friendList))
                    .replace("%count%", String.valueOf(friendList.size())));
        } else {
            ChatUtils.sendMessage(p, "<yellow>Nie masz jeszcze żadnych znajomych.");
        }
    }
    public static boolean isFriendAlreadyAdded(List<String> currentFriendList, String newFriend) {
        return currentFriendList.equals(newFriend);
    }
    public static boolean isPlayerAlreadyBlocked(List<String> currentBlockedList, String playerToBlock) {
        return currentBlockedList.equals(playerToBlock);
    }
    public static boolean isPlayerAlreadyRequested(List<String> currentRequestList, String playerToRequest) {
        return currentRequestList.equals(playerToRequest);
    }
    public static String getTargetUUID(String target) {
        String uuid = null;
        for (FriendData data : FriendData.Cache.getFriendsCache().values()) {
            if (data.getPlayerName().equals(target)) {
                uuid = data.getUuid();
                return uuid;
            }
        }
        return uuid;
    }
    public static String getPlayerNameByUUID(String uuid) {
        String playerName = null;
        FriendData cachedData = FriendData.Cache.getFriendsCache().get(uuid);
        if (cachedData != null) {
            playerName = cachedData.getPlayerName();
            return playerName;
        }
        return playerName;
    }

}

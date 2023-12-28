package me.verfiedfemboy.velocitycore.command.friends;

import lombok.Getter;
import lombok.Setter;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.utils.LoggerUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FriendData {

    @Setter @Getter
    String playerName;
    @Getter
    String uuid;

    //TODO: To fix caches
    @Setter @Getter
    List<String> friendList = new ArrayList<>();
    @Setter @Getter
    List<String> inviteRequests = new ArrayList<>();
    @Setter @Getter
    List<String> blockedPlayers = new ArrayList<>();

    @Setter @Getter
    boolean updated;

    public FriendData(String playerName, String uuid){
        this.playerName = playerName;
        this.uuid = uuid;
    }

    public void addBlocked(String targetUUID){
        getBlockedPlayers().add(targetUUID);
        LoggerUtils.log(LoggerUtils.DEBUG, "Zablokowano Znajomego (" + getPlayerName() + ")" + "\nCurrent:" + getBlockedPlayers());
        setUpdated(true);
    }

    public void RemoveBlock(String targetUUID){
        getBlockedPlayers().remove(targetUUID);
        LoggerUtils.log(LoggerUtils.DEBUG, "Odblokowano (" + getPlayerName() + ")" + "\nCurrent:" + getBlockedPlayers());
        setUpdated(true);
    }

    public void addFriendToList(String targetUUID){
        getFriendList().add(targetUUID);
        LoggerUtils.log(LoggerUtils.DEBUG, "Dodano Znajomego (" + getPlayerName() + ")");
        setUpdated(true);
    }

    public void RemoveFriend(String targetUUID){
        getFriendList().remove(targetUUID);
        LoggerUtils.log(LoggerUtils.DEBUG, "Dodano Znajomego (" + getPlayerName() + ")");
        setUpdated(true);
    }

    public void addRequest(String targetUUID){
        LoggerUtils.log(LoggerUtils.DEBUG, "Before:" + getInviteRequests());
        getInviteRequests().add(targetUUID);
        LoggerUtils.log(LoggerUtils.DEBUG, "Wysłano Zaproszenie -> (" + getPlayerName() + ")" + "\nCurrent:" + getInviteRequests());
        setUpdated(true);
    }

    public void RemoveRequest(String targetUUID){
        getInviteRequests().remove(targetUUID);
        LoggerUtils.log(LoggerUtils.DEBUG, "Usunięto Zaproszenie -> (" + getPlayerName() + ")" + "\nCurrent:" + getBlockedPlayers());
        setUpdated(true);
    }

    public static class Cache{
        @Getter
        private static HashMap<String, FriendData> friendsCache = new HashMap<>();

        public static void createUserIfNotExists(String playerName, String uuid) {
            FriendData user = Cache.friendsCache.get(uuid);
            if (user == null) {
                user = new FriendData(playerName, uuid);
                Cache.friendsCache.put(uuid, user);
                LoggerUtils.log(LoggerUtils.DEBUG, "Utworzono użytkownika w Cache (FriendData)");
                user.setUpdated(true);
            }
        }

        public static void loadFriendFromDatabase() {
            try {
                Connection connection = VelocityCore.getConnector().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM znajomi");
                while (resultSet.next()) {
                    String playerName = resultSet.getString("playerName");
                    String uuid = resultSet.getString("uuid");
                    FriendData friendData = new FriendData(playerName, uuid);

                    String blockedPlayers = resultSet.getString("blockedPlayers");
                    String friendList = resultSet.getString("friendList");
                    String inviteRequest = resultSet.getString("inviteRequests");

                    friendData.setBlockedPlayers(Arrays.asList(blockedPlayers.split(",")));
                    friendData.setFriendList(Arrays.asList(friendList.split(",")));
                    friendData.setInviteRequests(Arrays.asList(inviteRequest.split(",")));
                    Cache.friendsCache.put(uuid, friendData);
                }
                resultSet.close();
                statement.close();
                LoggerUtils.log(LoggerUtils.DEBUG, "Pobrano dane do Cache (FriendData)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void updateRecordInDatabase(FriendData friendData) {
            try {
                Connection connection = VelocityCore.getConnector().getConnection();
                if (doesRecordExist(friendData.getUuid())) {
                    String query = "UPDATE znajomi SET friendList = ?, inviteRequests = ?, blockedPlayers = ? WHERE uuid = ?";
                    if(friendData.isUpdated()){
                        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                            pstmt.setString(1, String.join(",", friendData.friendList));
                            pstmt.setString(2, String.join(",", friendData.inviteRequests));
                            pstmt.setString(3, String.join(",", friendData.blockedPlayers));

                            pstmt.executeUpdate();
                            friendData.setUpdated(false);
                            LoggerUtils.log(LoggerUtils.DEBUG, "Zaktualizowano rekord w bazie danych. (" + friendData.getPlayerName() + ")");
                        }
                    }
                } else {
                    //Adding new Record
                    String query = "INSERT INTO znajomi (playerName, uuid, friendList, inviteRequests, blockedPlayers) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        pstmt.setString(1, friendData.getPlayerName());
                        pstmt.setString(2, friendData.getUuid());

                        pstmt.setString(3, String.join(",", friendData.friendList));
                        pstmt.setString(4, String.join(",", friendData.inviteRequests));
                        pstmt.setString(5, String.join(",", friendData.blockedPlayers));

                        pstmt.executeUpdate();
                        friendData.setUpdated(false);
                        LoggerUtils.log(LoggerUtils.DEBUG, "Dodano nowy rekord do bazy danych.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        private static boolean doesRecordExist(String uuid) {
            try {
                Connection connection = VelocityCore.getConnector().getConnection();
                String query = "SELECT COUNT(*) AS count FROM znajomi WHERE uuid = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setString(1, uuid);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("count") > 0;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}

package me.sussydeveloper.commands.wallet;

import lombok.Getter;
import lombok.Setter;
import me.sussydeveloper.Main;
import me.sussydeveloper.utils.LoggerUtils;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

@Getter
public class WalletData {

    final String playerName;
    final String uuid;
    @Setter
    float wallet;

    public WalletData(String playerName, String uuid) {
        this.playerName = playerName;
        this.uuid = uuid;
    }

    public void createUser() {
        String[] columns = {"playerName", "uuid", "wallet"};
        String[] values = {playerName, uuid, "0.00"};
        try {
            if (!doesUUIDExist(uuid)) {
                String query = "INSERT INTO wallet (playerName, uuid, wallet) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = Main.getDatabase().getConnection().prepareStatement(query)) {
                    for (int i = 0; i < columns.length; i++) {
                        pstmt.setString(i + 1, values[i]);
                    }
                    pstmt.executeUpdate();
                    LoggerUtils.log(LoggerUtils.DEBUG, "Utworzono użytkownika.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesUUIDExist(String uuid) {
        return WalletData.Cache.doesUUIDExist(uuid);
    }

    public String getWalletFormatted() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance());
        return decimalFormat.format(wallet);
    }

    public void updateWallet(float value, boolean remove){
        float currentWallet = getWallet();
        float newValue = remove ? (currentWallet - value) : (currentWallet + value);
        WalletData walletData = Cache.getWalletDataCache().get(uuid);
        walletData.setWallet(newValue);
    }

    public void resetWallet() {
        WalletData walletData = Cache.getWalletDataCache().get(uuid);
        walletData.setWallet(0);
    }

    public static class Cache {
        @Getter
        private static HashMap<String, WalletData> walletDataCache = new HashMap<>();

        public static void loadWalletFromDatabase() {
            try {
                Connection connection = Main.getDatabase().getConnection();
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM wallet");
                while (resultSet.next()) {
                    String playerName = resultSet.getString("playerName");
                    String uuid = resultSet.getString("uuid");
                    float wallet = resultSet.getFloat("wallet");

                    WalletData walletData = new WalletData(playerName, uuid);
                    walletData.setWallet(wallet);

                    walletDataCache.put(uuid, walletData);
                }
                resultSet.close();
                statement.close();
                LoggerUtils.log(LoggerUtils.DEBUG, "Pobrano dane do Cache (WalletData)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static String findUUIDByPlayerName(String playerName) {
            for (WalletData walletData : walletDataCache.values()) {
                if (walletData.getPlayerName().equals(playerName)) {
                    return walletData.getUuid();
                }
            }
            return null;
        }

        public static boolean doesUUIDExist(String uuid) {
            for (WalletData walletData : walletDataCache.values()) {
                if (walletData.getUuid().equals(uuid)) {
                    return true;
                }
            }
            return false;
        }

    }
}

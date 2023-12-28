package me.sussydeveloper.commands.title;

import lombok.Getter;
import me.sussydeveloper.Main;
import me.sussydeveloper.database.DatabaseConnector;
import me.sussydeveloper.utils.ChatUtils;
import me.sussydeveloper.utils.DatabaseUtils;
import me.sussydeveloper.utils.LoggerUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

@Getter
public class TitleColor {

    private int colorId;
    private String colorName;
    private String colorPrefix;
    private float colorPrice;
    private float priceOnSale;

    public TitleColor(int colorId, String colorName, String colorPrefix, float colorPrice, float priceOnSale){
        this.colorId = colorId;
        this.colorName = colorName;
        this.colorPrefix = colorPrefix;
        this.colorPrice = colorPrice;
        this.priceOnSale = priceOnSale;
    }

    public static void createTable(DatabaseConnector database){
        DatabaseUtils.createTable(database.getConnection(), "TitleColors",
                "color_id INT AUTO_INCREMENT PRIMARY KEY",
                "color_name VARCHAR(255) NOT NULL",
                "color_prefix VARCHAR(255) NOT NULL",
                "color_price DECIMAL(5, 2)",
                "color_price_onsale DECIMAL(5,2)");
        insertColors(database.getConnection());
    }

    private static void insertColors(Connection conn) {
        String[] colors = {
                "('Ciemnoczerwony', '&4', 5.0, 0.0)",
                "('Czerwony', '&c', 5.0, 0.0)",
                "('Złoty', '&6', 5.0, 0.0)",
                "('Żółty', '&e', 10.0, 0.0)",
                "('Ciemnozielony', '&2', 10.0, 0.0)",
                "('Zielony', '&a', 10.0, 0.0)",
                "('Ciemnomorski', '&3', 10.0, 0.0)",
                "('Morski', '&b', 10.0, 0.0)",
                "('Ciemnoniebieski', '&1', 10.0, 0.0)",
                "('Niebieski', '&9', 10.0, 0.0)",
                "('Ciemnofioletowy', '&5', 10.0, 0.0)",
                "('Fioletowy', '&d', 10.0, 0.0)",
                "('Biały', '&f', 2.99, 0.0)",
                "('Szary', '&7', 1.99, 0.0)",
                "('Ciemnoszary', '&8', 0.99, 0.0)",
                "('Czarny', '&0', 0.99, 0.0)",
                "('Rainbow', '', 19.99, 25.0)"
        };
        try {
            for (String color : colors) {
                String insertSQL = "INSERT INTO TitleColors (color_name, color_prefix, color_price, color_price_onsale) VALUES " + color;
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(insertSQL);
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOnSale(){
        return priceOnSale > 0;
    }

    public static class Cache{
        @Getter
        private static HashMap<Integer, TitleColor> titleColorsCache = new HashMap<>();

        public static void loadTitlesFromDatabase(){
            try {
                Connection connection = Main.getDatabase().getConnection();
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM TitleColors");

                while (resultSet.next()) {
                    int colorId = resultSet.getInt("color_id");
                    String colorName = resultSet.getString("color_name");
                    String colorPrefix = resultSet.getString("color_prefix");
                    float colorPrice = resultSet.getFloat("color_price");
                    float priceOnSale = resultSet.getFloat("color_price_onsale");

                    TitleColor color = new TitleColor(colorId, colorName, colorPrefix, colorPrice, priceOnSale);
                    titleColorsCache.put(colorId, color);
                }
                resultSet.close();
                statement.close();
                LoggerUtils.log(LoggerUtils.DEBUG, "Pobrano dane do Cache (TitleColors)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        public static TitleColor getTitleColorById(int colorId) {
            return titleColorsCache.get(colorId);
        }

        public static void updateCacheToDatabase(){
            //TODO: Make a cache updater to database
        }
    }
}

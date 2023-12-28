package me.sussydeveloper.database;

import lombok.Getter;
import me.sussydeveloper.Main;
import me.sussydeveloper.utils.LoggerUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@Getter
public class DatabaseConnector {

    private Connection connection = null;

    public void connectToDatabase() {
        String hostname = Main.config.getString("database.host");

        String user = Main.config.getString("database.user");

        String password = Main.config.getString("database.password");

        String port = Main.config.getString("database.port");

        String database = Main.config.getString("database.database-name");

        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, user, password);

            LoggerUtils.log(LoggerUtils.DEBUG,"Pomyślnie połączono z bazą danych");
        } catch (ClassNotFoundException e) {
            LoggerUtils.log(LoggerUtils.ERROR,"Nie znaleziono Classy");
        } catch (SQLException e) {
            LoggerUtils.log(LoggerUtils.ERROR,"Nie udało się połączyć z bazą danych");
        }
    }

}

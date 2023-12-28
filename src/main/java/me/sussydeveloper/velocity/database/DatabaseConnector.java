package me.verfiedfemboy.velocitycore.database;

import lombok.Getter;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.config.ConfigManager;
import me.verfiedfemboy.velocitycore.utils.LoggerUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Getter
public class DatabaseConnector {

    private Connection connection = null;

    public void connectToDatabase() {
        String hostname = ConfigManager.getConfig("database").getString("hostname");

        String user = ConfigManager.getConfig("database").getString("username");

        String password = ConfigManager.getConfig("database").getString("password");

        String port = ConfigManager.getConfig("database").getString("port");

        String database = ConfigManager.getConfig("database").getString("database");

        String typedb = ConfigManager.getConfig("database").getString("type");
        String jdbcUrl = "jdbc:" + typedb + "://" + hostname + ":" + port + "/" + database;

        try {
            switch (typedb){
                case "mysql":
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    break;
                case "mariadb":
                    Class.forName("org.mariadb.jdbc.Driver");
                    break;
            }
            connection = DriverManager.getConnection(jdbcUrl, user, password);

            LoggerUtils.log(LoggerUtils.DEBUG,"Pomyślnie połączono z bazą danych");
        } catch (ClassNotFoundException e) {
            LoggerUtils.log(LoggerUtils.ERROR,"Nie udało się połączyć z bazą danych, ponieważ nie znaleziono sterownika");
        } catch (SQLException e) {
            LoggerUtils.log(LoggerUtils.ERROR,"Nie udało się połączyć z bazą danych");
        }
    }

    public DatabaseConnector getConnector(){
        return VelocityCore.getConnector();
    }

}

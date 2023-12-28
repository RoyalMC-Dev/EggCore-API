package me.sussydeveloper.commands.title;

import me.sussydeveloper.database.DatabaseConnector;
import me.sussydeveloper.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TitleData {

    String playerName;
    String uuid;

    public TitleData(String playerName, String uuid){

    }


    public static class Cache{
        //TODO: Title data cache
    }
}

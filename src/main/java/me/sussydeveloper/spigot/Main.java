package me.sussydeveloper;

import lombok.Getter;
import me.sussydeveloper.commands.eggCoreCommand;
import me.sussydeveloper.commands.title.TitleColor;
import me.sussydeveloper.commands.title.TitleCommand;
import me.sussydeveloper.commands.wallet.*;
import me.sussydeveloper.database.DatabaseConnector;
import me.sussydeveloper.listeners.PlayerJoinEvent;
import me.sussydeveloper.utils.DatabaseUtils;
import me.sussydeveloper.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin implements Listener {

    @Getter
    private static DatabaseConnector database;

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        loadConfig();
        dataBase();
        registeringEvents();
        registeringCommands();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new walletExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {

    }


    public static void reload(){
        getPlugin().reloadConfig();
        config = getPlugin().getConfig();
    }

    public static JavaPlugin getPlugin(){return JavaPlugin.getPlugin(Main.class);}

    public void loadConfig(){
        if(!this.getDataFolder().exists()){this.getDataFolder().mkdirs();}
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = getConfig();
    }

    private void registeringEvents(){
        //Join message changer on Join Event
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        //Inv Click Title
        getServer().getPluginManager().registerEvents(new TitleCommand.InventoryEvent(), this);
    }

    private void registeringCommands(){
        //Main command
        Objects.requireNonNull(getPlugin().getCommand("eggcore")).setExecutor(new eggCoreCommand());
        //Tytuł command
        Objects.requireNonNull(getPlugin().getCommand("tytul")).setExecutor(new TitleCommand());
        //Wallet command
        Objects.requireNonNull(getPlugin().getCommand("portfel")).setExecutor(new walletCommand());
        Objects.requireNonNull(getPlugin().getCommand("portfel")).setTabCompleter(new walletTabCompleter());
        //pay command
        Objects.requireNonNull(getPlugin().getCommand("przelej")).setExecutor(new payCommand());
        Objects.requireNonNull(getPlugin().getCommand("przelej")).setTabCompleter(new payCommand.payTabCompleter());
    }

    private void dataBase(){
        database = new DatabaseConnector();
        database.connectToDatabase();
        if(!DatabaseUtils.doesTableExist(database.getConnection(), "wallet")){
            DatabaseUtils.createTable(database.getConnection(), "wallet",
                    "id INT AUTO_INCREMENT PRIMARY KEY",
                    "playerName VARCHAR(255) NOT NULL",
                    "uuid VARCHAR(255) NOT NULL",
                    "wallet DECIMAL(20, 2)");
        }else{
            LoggerUtils.log(LoggerUtils.DEBUG,"Nie utworzono tabeli 'wallet', ponieważ już istnieje.");
        }
        if(!DatabaseUtils.doesTableExist(database.getConnection(), "TitleColors")){
            TitleColor.createTable(database);
        }else{
            LoggerUtils.log(LoggerUtils.DEBUG,"Nie utworzono tabeli 'TitleColors', ponieważ już istnieje.");
        }
        TitleColor.Cache.loadTitlesFromDatabase();
        WalletData.Cache.loadWalletFromDatabase();
    }

}

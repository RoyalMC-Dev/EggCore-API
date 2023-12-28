package me.verfiedfemboy.velocitycore;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.verfiedfemboy.velocitycore.command.*;
import me.verfiedfemboy.velocitycore.command.friends.FriendCommand;
import me.verfiedfemboy.velocitycore.command.friends.FriendData;
import me.verfiedfemboy.velocitycore.command.friends.MessageCommand;
import me.verfiedfemboy.velocitycore.config.ConfigManager;
import me.verfiedfemboy.velocitycore.database.DatabaseConnector;
import me.verfiedfemboy.velocitycore.listeners.onPlayerJoinServer;
import me.verfiedfemboy.velocitycore.utils.DatabaseUtils;
import me.verfiedfemboy.velocitycore.utils.LoggerUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "velocitycore",
        name = "VelocityCore",
        version = "1.0",
        authors = {"MaciekTM"}
)
@Getter
public class VelocityCore {

    @Getter
    public static Path folder = null;
    private final Logger logger;
    @Getter
    private static LuckPerms luckPerms;
    private final ProxyServer proxyServer;
    @Getter
    private static DatabaseConnector connector;
    public static ConfigManager configManager;

    @Inject
    public VelocityCore(ProxyServer server, Logger logger, @DataDirectory final Path folder) {
        this.proxyServer = server;
        this.logger = logger;
        setFolder(folder);

        /*toml = loadConfig(folder);
        if (toml == null) {
            logger.warn("Failed to load config. Shutting down the plugin...");
            return;
        }*/

        configManager = new ConfigManager(folder);
        configManager.load();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        luckPerms = LuckPermsProvider.get();
        connector = new DatabaseConnector();
        connector.connectToDatabase();

        //Creating database
        if(!DatabaseUtils.doesTableExist(connector.getConnection(), "znajomi")){
            DatabaseUtils.createTable(connector.getConnection(), "znajomi",
                    "id INT AUTO_INCREMENT PRIMARY KEY",
                    "playerName VARCHAR(255) NOT NULL",
                    "uuid VARCHAR(255) NOT NULL",
                    "friendList TEXT",
                    "inviteRequests TEXT",
                    "blockedPlayers TEXT");
        }else{
            LoggerUtils.log(LoggerUtils.DEBUG,"Nie utworzono tabeli 'znajomi', ponieważ już istnieje.");
        }

        FriendData.Cache.loadFriendFromDatabase();

        CommandManager commandManager = proxyServer.getCommandManager();

        CommandMeta liveCMD = commandManager.metaBuilder("live").build();
        commandManager.register(liveCMD, new LiveCommand(proxyServer));

        CommandMeta alertCMD = commandManager.metaBuilder("ogloszenie").aliases("ogloszenia").build();
        commandManager.register(alertCMD, new AlertCommand(proxyServer));

        CommandMeta lobbyCMD = commandManager.metaBuilder("lobby").aliases("hub").build();
        commandManager.register(lobbyCMD, new LobbyCommand(proxyServer));

        CommandMeta reloadCMD = commandManager.metaBuilder("velreload").build();
        commandManager.register(reloadCMD, new ReloadCommand(folder));

        CommandMeta tpCMD = commandManager.metaBuilder("btp").build();
        commandManager.register(tpCMD, new TeleportCommand(proxyServer));

        CommandMeta friendCMD = commandManager.metaBuilder("friend").aliases("f", "znajomi", "z").build();
        commandManager.register(friendCMD, new FriendCommand(proxyServer));

        CommandMeta msgCMD = commandManager.metaBuilder("msg").aliases("message", "wiadomosc").build();
        commandManager.register(msgCMD, new MessageCommand(proxyServer, luckPerms));

        //Listeners
        proxyServer.getEventManager().register(this, new onPlayerJoinServer());
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event){
        for (FriendData data : FriendData.Cache.getFriendsCache().values()){
            if(data.isUpdated())
                FriendData.Cache.updateRecordInDatabase(data);
        }
    }

    public static void setFolder(Path folder) {
        VelocityCore.folder = folder;
    }

}

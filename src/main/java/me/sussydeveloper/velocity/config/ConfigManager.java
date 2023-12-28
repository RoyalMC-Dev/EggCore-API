package me.verfiedfemboy.velocitycore.config;

import com.moandjiezana.toml.Toml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {

    static List<Config>configs = new ArrayList<>();

    Path path;

    public ConfigManager(Path path){
        this.path = path;
        Config main = new Config("config");
        Config database = new Config("database");
        Config friend = new Config("friend");
        configs.addAll(Arrays.asList(main, database, friend));
    }

    public void load(){
        for(Config config : configs){
            config.loadConfig(path);
        }
    }

    public void update(){
        for(Config config : configs){
            config.update(path);
        }
    }

    public static Toml getConfig(String name){
        for(Config config : configs){
            if(config.name.equals(name + ".toml")){
                return config.toml;
            }
        }
        return null;
    }
}

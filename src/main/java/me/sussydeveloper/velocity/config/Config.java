package me.verfiedfemboy.velocitycore.config;

import com.moandjiezana.toml.Toml;
import me.verfiedfemboy.velocitycore.VelocityCore;
import me.verfiedfemboy.velocitycore.utils.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public Toml toml;
    public final String name;

    public Config(String name) {
        this.name = name + ".toml";
    }

    public void loadConfig(Path path){
        File folder = path.toFile();
        File file = new File(folder, name);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (InputStream input = VelocityCore.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        toml = new Toml().read(file);
    }

    public void update(Path path){
        File file = new File(path.toFile(), name);
        toml = new Toml().read(file);
        LoggerUtils.log(LoggerUtils.INFO, "Updated Config: " + name);
    }
}

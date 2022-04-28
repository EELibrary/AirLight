package org.skylight.config;

import org.apache.logging.log4j.LogManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AirLightConfig {
    public static Map<String,ConfigurationSection> sectionMap = new ConcurrentHashMap<>();
    public static YamlConfiguration config;
    public static void init() {
        try {
            LogManager.getLogger().info("Config loading");
            File file = new File("airlight.yml");
            config = new org.bukkit.configuration.file.YamlConfiguration();
            if (!file.exists()) {
                file.createNewFile();
                LogManager.getLogger().info("Config didn't detected!Creating");
                config.options().copyDefaults(true);
                config.options().header("The config file of AirLight.Don't edit the debug section!");
                ConfigurationSection configurationSection = config.createSection("executor");
                configurationSection.addDefault("entities-thread-count", Runtime.getRuntime().availableProcessors());
                configurationSection.addDefault("entities-per-thread", 100);
                configurationSection.addDefault("tile-entities-thread-count", Runtime.getRuntime().availableProcessors());
                configurationSection.addDefault("weather-effects-thread-count", Runtime.getRuntime().availableProcessors());
                configurationSection.addDefault("tracker-thread-count", Runtime.getRuntime().availableProcessors());
                sectionMap.put("executor", configurationSection);
                ConfigurationSection configurationSection1 = config.createSection("debug");
                configurationSection1.addDefault("wait-entities-tick", true);
                configurationSection1.addDefault("wait-tracker", true);
                sectionMap.put("debug", configurationSection1);
                config.load(file);
                config.save(file);
                LogManager.getLogger().info("Finish loading!");
                return;
            }
            LogManager.getLogger().info("Detected config file.Loading");
            config.load(file);
            ConfigurationSection configurationSection = config.getConfigurationSection("executor");
            sectionMap.put("executor", configurationSection);
            ConfigurationSection configurationSection1 = config.getConfigurationSection("debug");
            sectionMap.put("debug", configurationSection1);
            LogManager.getLogger().info("Finish loading!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

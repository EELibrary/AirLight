package org.skylight.config;

import org.apache.logging.log4j.LogManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.skylight.executor.EntityMiscTickThread;

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
                configurationSection.addDefault("entity-worker-core-size",Runtime.getRuntime().availableProcessors()*2);
                configurationSection.addDefault("tracker-thread-count", Runtime.getRuntime().availableProcessors());
                configurationSection.addDefault("force-bukkit-event-on-main-thread",false);
                configurationSection.addDefault("force-forge-event-on-main-thread",false);
                ConfigurationSection configurationSection2 = config.createSection("network");
                configurationSection2.addDefault("pps-limit",2000);
                configurationSection2.addDefault("enable-pps-limitor",false);
                sectionMap.put("network",configurationSection2);
                sectionMap.put("executor", configurationSection);
                ConfigurationSection configurationSection1 = config.createSection("debug");
                configurationSection1.addDefault("wait-tracker", true);
                configurationSection1.addDefault("wait-entity-worker", true);
                sectionMap.put("debug", configurationSection1);
                config.load(file);
                config.save(file);
                LogManager.getLogger().info("Finish loading!");
            }else{
                LogManager.getLogger().info("Detected config file.Loading");
                config.load(file);
                ConfigurationSection configurationSection = config.getConfigurationSection("executor");
                sectionMap.put("executor", configurationSection);
                ConfigurationSection configurationSection1 = config.getConfigurationSection("debug");
                sectionMap.put("debug", configurationSection1);
                ConfigurationSection configurationSection2 = config.getConfigurationSection("network");
                sectionMap.put("network",configurationSection2);
                LogManager.getLogger().info("Finish loading!");
            }
            net.minecraft.world.World.waitEntitiesProcessor = sectionMap.get("debug").getBoolean("wait-entity-worker");
            net.minecraft.network.NetworkManager.ppsl = sectionMap.get("network").getInt("pps-limit");
            net.minecraft.network.NetworkManager.enablePPSLimitor = sectionMap.get("network").getBoolean("enable-pps-limitor");
            EntityMiscTickThread.init(sectionMap.get("executor").getInt("entity-worker-core-size"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

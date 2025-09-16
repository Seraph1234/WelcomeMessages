package com.FiveDollaGobby.WelcomeMessages;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.FiveDollaGobby.WelcomeMessages.commands.WelcomeCommand;
import com.FiveDollaGobby.WelcomeMessages.listeners.PlayerJoinListener;
import com.FiveDollaGobby.WelcomeMessages.managers.MessageManager;
import com.FiveDollaGobby.WelcomeMessages.managers.EffectManager;
import com.FiveDollaGobby.WelcomeMessages.managers.DataManager;
import com.FiveDollaGobby.WelcomeMessages.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class WelcomePlugin extends JavaPlugin {

    private static WelcomePlugin instance;
    private MessageManager messageManager;
    private EffectManager effectManager;
    private DataManager dataManager;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    @Override
    public void onEnable() {
        instance = this;

        // Create plugin folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Load configurations
        saveDefaultConfig();
        loadMessagesConfig();

        // Initialize managers
        messageManager = new MessageManager(this);
        effectManager = new EffectManager(this);
        dataManager = new DataManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register commands
        getCommand("welcome").setExecutor(new WelcomeCommand(this));
        getCommand("welcome").setTabCompleter(new WelcomeCommand(this));

        // Log successful enable
        MessageUtils.sendConsole("&aWelcomeMessages v" + getDescription().getVersion() + " has been enabled!");

        // Metrics (optional - using bStats)
        if (getConfig().getBoolean("metrics.enabled", true)) {
            int pluginId = 20000; // Replace with your bStats plugin ID
            new Metrics(this, pluginId);
        }
    }

    @Override
    public void onDisable() {
        // Save data
        if (dataManager != null) {
            dataManager.saveData();
        }

        MessageUtils.sendConsole("&cWelcomeMessages has been disabled!");
    }

    private void loadMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Look for defaults in the jar
        InputStream defConfigStream = getResource("messages.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defConfigStream)
            );
            messagesConfig.setDefaults(defConfig);
        }
    }

    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save messages.yml!", e);
        }
    }

    // Getters
    public static WelcomePlugin getInstance() {
        return instance;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void reload() {
        reloadConfig();
        reloadMessagesConfig();
        messageManager.reload();
        effectManager.reload();
        MessageUtils.sendConsole("&aConfiguration reloaded successfully!");
    }
}
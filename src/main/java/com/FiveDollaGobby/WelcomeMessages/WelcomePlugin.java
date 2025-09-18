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
import com.FiveDollaGobby.WelcomeMessages.utils.ConfigValidator;

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

        // setup plugin folder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // load configs
        saveDefaultConfig();
        loadMessagesConfig();

        // validate configuration
        ConfigValidator validator = new ConfigValidator(this);
        if (!validator.validateConfig()) {
            MessageUtils.sendConsole("&cPlugin disabled due to configuration errors!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // init managers
        messageManager = new MessageManager(this);
        effectManager = new EffectManager(this);
        dataManager = new DataManager(this);

        // register stuff
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // commands
        getCommand("welcome").setExecutor(new WelcomeCommand(this));
        getCommand("welcome").setTabCompleter(new WelcomeCommand(this));

        MessageUtils.sendConsole("&aWelcomeMessages v" + getDescription().getVersion() + " has been enabled!");

        // bStats metrics if enabled
        if (getConfig().getBoolean("metrics.enabled", true)) {
            int pluginId = 20000; // replace with actual bStats ID if you get one
            new Metrics(this, pluginId);
        }
    }

    @Override
    public void onDisable() {
        // save player data
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

        // set defaults from jar
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

    // getters
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
        
        // validate configuration after reload
        ConfigValidator validator = new ConfigValidator(this);
        if (!validator.validateConfig()) {
            MessageUtils.sendConsole("&cReload failed due to configuration errors!");
            return;
        }
        
        messageManager.reload();
        effectManager.reload();
        MessageUtils.sendConsole("&aConfiguration reloaded successfully!");
    }
}
package com.comonier.gpft;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;
import java.io.File;

public class Main extends JavaPlugin {

	private static Main instance;
	private FileConfiguration langConfig;
	private FileConfiguration modulesConfig;
	private FlagManager flagManager;
	private MenuManager menuManager;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		saveResource("modules.yml", false);
		loadLocalization();
		loadModules();

		this.flagManager = new FlagManager(this);
		this.menuManager = new MenuManager(this, flagManager);

		if (null != getCommand("gpft")) {
			CommandHandler cmdHandler = new CommandHandler(this, flagManager, menuManager);
			getCommand("gpft").setExecutor(cmdHandler);
			getCommand("gpft").setTabCompleter(new GpftTabCompleter());
		}

		// Registro de Listeners
		Bukkit.getPluginManager().registerEvents(new MenuListener(this, flagManager, menuManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEvents(flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsEnvironment(flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsPlayer(this, flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsPlayerFly(flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsTrust(flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsSpawning(flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsCommands(this, flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsWeather(flagManager), this);
		Bukkit.getPluginManager().registerEvents(new FlagEventsMovement(this, flagManager), this);
		
		getLogger().info("GPFT v" + getDescription().getVersion() + " loaded successfully.");
	}

	public void loadLocalization() {
		String lang = getConfig().getString("language", "pt");
		File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
		if (false == langFile.exists()) {
			saveResource("messages_pt.yml", false);
			saveResource("messages_en.yml", false);
		}
		this.langConfig = YamlConfiguration.loadConfiguration(langFile);
	}

	public void loadModules() {
		File file = new File(getDataFolder(), "modules.yml");
		if (false == file.exists()) {
			saveResource("modules.yml", false);
		}
		this.modulesConfig = YamlConfiguration.loadConfiguration(file);
	}

	public String getMsg(String path) {
		String message = langConfig.getString(path);
		if (null == message) return "§c" + path;
		String prefix = langConfig.getString("prefix", "&8[&6GPFT&8] ").replace("&", "§");
		return prefix + message.replace("&", "§");
	}

	public String getMsgRaw(String path) {
		String message = langConfig.getString(path);
		if (null == message) return "§c" + path;
		return message.replace("&", "§");
	}

	public boolean isModuleEnabled(String flag) {
		return modulesConfig.getBoolean("modules." + flag, true);
	}

	public static Main getInstance() { return instance; }
	public FileConfiguration getLang() { return this.langConfig; }
}

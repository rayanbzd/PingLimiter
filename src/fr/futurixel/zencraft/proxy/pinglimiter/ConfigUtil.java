package fr.futurixel.zencraft.proxy.pinglimiter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import fr.futurixel.zencraft.proxy.pinglimiter.logger.LogLoader;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigUtil {
	
	private final PingLimiter main;
	public ConfigUtil(PingLimiter main) {this.main = main;}
	
	private static Configuration configuration = null;
	
	private int refresh;
	private int limit;
	private boolean protection;
	private boolean serverIconOnlyFirstTime;
	private String motd;
	private String versionMessage;
	private int limitBeforeAlert;
	private String titleAlert;
	private String subTitleAlert;
	private boolean enableActionBar;
	private String actionBar;
	private String noPermission;
	private String configReloaded;
	private String enabledNotifications;
	private String disabledNotifications;
	private boolean consoleFilter;
	private List<String> filter;
	
	public void loadConfig() throws IOException {
		if (main.mainTask != null) {
			main.mainTask.cancel();
			main.mainTask = null;
		}
		main.map.clear();
		main.blacklist.clear();
		
		if (!main.getDataFolder().exists()) {
            main.getDataFolder().mkdir();
		}

        File file = new File(main.getDataFolder(), "config.yml");
   
        if (!file.exists()) {
            try (InputStream in = main.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

		configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(main.getDataFolder(), "config.yml"));
		
		if(isntSet("protection.enable")) {
			configuration.set("protection.enable" , true);
			setProtection(true);
		}else {
			setProtection(configuration.getBoolean("protection.enable"));
		}
		
		if (isntSet("protection.refreshRate")) {
			configuration.set("protection.refreshRate", 8);
			setRefresh(8);
		} else {
			setRefresh(configuration.getInt("protection.refreshRate"));
		}

		if (isntSet("protection.limit")) {
			configuration.set("protection.limit", 3);
			setLimit(3);
		} else {
			setLimit(configuration.getInt("protection.limit"));
		}
		
		if (isntSet("protection.serverIconOnlyFirstTime")) {
			configuration.set("protection.serverIconOnlyFirstTime", true);
			setServerIconOnlyFirstTime(true);
		} else {
			setServerIconOnlyFirstTime(configuration.getBoolean("protection.serverIconOnlyFirstTime"));
		}
		
		if(isntSet("limitedRespond.motd")) {
			configuration.set("limitedRespond.motd", "&cPlease do not spam refresh !");
			setMotd("§cPlease do not spam refresh !");
		}else {
			setMotd(configuration.getString("limitedRespond.motd").replace("&", "�"));
		}
		
		if(isntSet("limitedRespond.versionMessage")) {
			configuration.set("limitedRespond.versionMessage", "&b&lZen&a&lCraft &8&l| &7%connecteds%&8/&7%maxPlayers%");
			setVersionMessage("§b§lZen§a§lCraft §8§l| §7%connecteds%§8/§7%maxPlayers%");
		}else {
			setVersionMessage(configuration.getString("limitedRespond.versionMessage").replace("&", "�"));
		}
		
		if (isntSet("message.limitBeforeAlert")) {
			configuration.set("message.limitBeforeAlert", 100);
			setLimitBeforeAlert(100);
		} else {
			setLimitBeforeAlert(configuration.getInt("message.limitBeforeAlert"));
		}
		
		if(isntSet("message.titleAlert")) {
			configuration.set("message.titleAlert", "&cServer under Ddos attack");
			setTitleAlert("§cServer under Ddos attack");
		}else {
			setTitleAlert(configuration.getString("message.titleAlert").replace("&", "�"));
		}
		
		if(isntSet("message.subTitleAlert")) {
			configuration.set("message.subTitleAlert", "&e%numberIP% &7IP addresses was limiteds");
			setSubTitleAlert("§e%numberIP% §7IP addresses was limiteds");
		}else {
			setSubTitleAlert(configuration.getString("message.subTitleAlert").replace("&", "�"));
		}
		
		if(isntSet("message.enableActionBar")) {
			configuration.set("message.enableActionBar" , false);
			setEnableActionBar(false);
		}else {
			setEnableActionBar(configuration.getBoolean("message.enableActionBar"));
		}
		
		if(isntSet("message.actionBar")) {
			configuration.set("message.actionBar", "&6%IP% &rexceeded the ping limit");
			setActionBar("§6%IP% §rexceeded the ping limit");
		}else {
			setActionBar(configuration.getString("message.actionBar").replace("&", "�"));
		}
		
		if(isntSet("message.noPermission")) {
			configuration.set("message.noPermission", "&cYou don't have permission to use this command");
			setNoPermission("§cYou don't have permission to use this command");
		}else {
			setNoPermission(configuration.getString("message.noPermission").replace("&", "�"));
		}
		
		if(isntSet("message.configReloaded")) {
			configuration.set("message.configReloaded", "&aThe configuration has been reloaded");
			setConfigReloaded("§aThe configuration has been reloaded");
		}else {
			setConfigReloaded(configuration.getString("message.configReloaded").replace("&", "�"));
		}
		
		if(isntSet("message.enabledNotifications")) {
			configuration.set("message.enabledNotifications", "&aDdos notifications enabled");
			setEnabledNotifications("§aDdos notifications enabled");
		}else {
			setEnabledNotifications(configuration.getString("message.enabledNotifications").replace("&", "�"));
		}
		
		if(isntSet("message.disabledNotifications")) {
			configuration.set("message.disabledNotifications", "&cDdos notifications disabled");
			setDisabledNotifications("§cDdos notifications disabled");
		}else {
			setDisabledNotifications(configuration.getString("message.disabledNotifications").replace("&", "�"));
		}
		
		if(isntSet("consoleFilter.enable")) {
			configuration.set("consoleFilter.enable", true);
			setConsoleFilter(true);
		}else {
			setConsoleFilter(configuration.getBoolean("consoleFilter.enable"));
		}
		
		if(isntSet("consoleFilter.blacklist")) {
			configuration.set("consoleFilter.blacklist", Collections.singletonList("InitialHandler"));
			setFilter(Collections.singletonList("InitialHandler"));
		}else {
			setFilter(configuration.getStringList("consoleFilter.blacklist"));
		}

		ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,new File(main.getDataFolder(), "config.yml"));
		new LogLoader(main).loadLoggerFilter();
	}
	
	private boolean isntSet(String path) {
		Object obj = configuration.get(path);
		return obj == null;
	}

	public int getRefresh() {
		return refresh;
	}

	public void setRefresh(int refresh) {
		this.refresh = refresh;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isProtection() {
		return protection;
	}

	public void setProtection(boolean protection) {
		this.protection = protection;
	}

	public boolean isServerIconOnlyFirstTime() {
		return serverIconOnlyFirstTime;
	}

	public void setServerIconOnlyFirstTime(boolean serverIconOnlyFirstTime) {
		this.serverIconOnlyFirstTime = serverIconOnlyFirstTime;
	}

	public int getLimitBeforeAlert() {
		return limitBeforeAlert;
	}

	public void setLimitBeforeAlert(int limitBeforeAlert) {
		this.limitBeforeAlert = limitBeforeAlert;
	}

	public String getTitleAlert() {
		return titleAlert;
	}

	public void setTitleAlert(String titleAlert) {
		this.titleAlert = titleAlert;
	}

	public String getSubTitleAlert() {
		return subTitleAlert;
	}

	public void setSubTitleAlert(String subTitleAlert) {
		this.subTitleAlert = subTitleAlert;
	}

	public String getActionBar() {
		return actionBar;
	}

	public void setActionBar(String actionBar) {
		this.actionBar = actionBar;
	}

	public boolean isConsoleFilter() {
		return consoleFilter;
	}

	public void setConsoleFilter(boolean consoleFilter) {
		this.consoleFilter = consoleFilter;
	}

	public List<String> getFilter() {
		return filter;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public String getVersionMessage() {
		return versionMessage;
	}

	public void setVersionMessage(String versionMessage) {
		this.versionMessage = versionMessage;
	}

	public boolean isEnableActionBar() {
		return enableActionBar;
	}

	public void setEnableActionBar(boolean enableActionBar) {
		this.enableActionBar = enableActionBar;
	}

	public String getNoPermission() {
		return noPermission;
	}

	public void setNoPermission(String noPermission) {
		this.noPermission = noPermission;
	}

	public String getConfigReloaded() {
		return configReloaded;
	}

	public void setConfigReloaded(String configReloaded) {
		this.configReloaded = configReloaded;
	}

	public String getEnabledNotifications() {
		return enabledNotifications;
	}

	public void setEnabledNotifications(String enabledNotifications) {
		this.enabledNotifications = enabledNotifications;
	}

	public String getDisabledNotifications() {
		return disabledNotifications;
	}

	public void setDisabledNotifications(String disabledNotifications) {
		this.disabledNotifications = disabledNotifications;
	}

}

package fr.futurixel.zencraft.proxy.pinglimiter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

public class PingLimiter extends Plugin implements Listener{
	
	public Map<String, Integer> map = new HashMap<>();
	public List<String> blacklist = new ArrayList<>();
	public List<ProxiedPlayer> notifications = new ArrayList<>();
	public ScheduledTask mainTask;
	public ConfigUtil config = new ConfigUtil(this);
	private boolean rateLimit = false;
	
	@Override
	public void onEnable() {
		PluginManager pm = getProxy().getPluginManager();
		System.out.println("§8================================================================================");
		System.out.println("§a  _____    _                   §6_        _               _   _                 ");
		System.out.println("§a |  __ \\  (_)                 §6| |      (_)             (_) | |                ");
		System.out.println("§a | |__) |  _   _ __     __ _  §6| |       _   _ __ ___    _  | |_    ___   _ __ ");
		System.out.println("§a |  ___/  | | | '_ \\   / _` | §6| |      | | | '_ ` _ \\  | | | __|  / _ \\ | '__|");
		System.out.println("§a | |      | | | | | | | (_| | §6| |____  | | | | | | | | | | | |_  |  __/ | |   ");
		System.out.println("§a |_|      |_| |_| |_|  \\__, | §6|______| |_| |_| |_| |_| |_|  \\__|  \\___| |_|   ");
		System.out.println("§a                        __/ |                                                 ");
		System.out.println("§a                       |___/                                                  ");
		System.out.println("§bDeveloped with §c<3 §bby Futurixel");
		System.out.println("§2Support on spigot plugin page : §ehttps://bit.ly/34DavyY");
		System.out.println("§8================================================================================");

		pm.registerListener(this, this);
		pm.registerCommand(this, new Commands("pinglimiter", this));
		
		try {
			config.loadConfig();
			System.out.println("§8[§aPing§6Limiter§8] §aPlugin successfully enabled");
		} catch (IOException e) {
			e.printStackTrace();
		}
		runMainTask();
	}
	
	@Override
	public void onDisable() {
		
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPingEvent(ProxyPingEvent e) {
		if (config.isProtection()) {
			String adress = e.getConnection().getAddress().getAddress().toString().replace("/", "");

			if (blacklist.contains(adress)) {
				e.setResponse(new ServerPing(
						new Protocol(config.getVersionMessage().replace("%connecteds%", String.valueOf(getProxy().getOnlineCount())).replace("%maxPlayers%", String.valueOf(getMaxPlayers())), 1),
						new Players(0, 0, null), config.getMotd(), ""));
				return;
			}

			if (map.containsKey(adress)) {
				if (map.get(adress) >= config.getLimit()) {
					rateLimit = true;
					if (!blacklist.contains(adress)) {
						blacklist.add(adress);
						if (config.isEnableActionBar()) {
							for (ProxiedPlayer pl : getProxy().getPlayers()) {
								if (notifications.contains(pl)) {
									if (pl.hasPermission("pinglimiter.alert")) {
										pl.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(config.getActionBar().replace("%IP%", adress)));
									}
								}
							}
						}
					}
				}else {
					rateLimit = false;
				}
				map.put(adress, map.get(adress) + 1);
				
				if(config.isServerIconOnlyFirstTime()) {
					ServerPing response = e.getResponse();
					response.setFavicon("");
					e.setResponse(response);
				}
				return;
			} else {
				map.put(adress, 1);
			}
			if(rateLimit) {
				ServerPing response = e.getResponse();
				response.setFavicon("");
				e.setResponse(response);
			}
		}
	}
	
	public void runMainTask() {
		if (config.isProtection()) {
			mainTask = ProxyServer.getInstance().getScheduler().schedule(this, () -> {

				if (blacklist.size() > config.getLimitBeforeAlert()) {
					Title t = ProxyServer.getInstance().createTitle();
					t.title(TextComponent.fromLegacyText(config.getTitleAlert()));
					t.subTitle(TextComponent.fromLegacyText(config.getSubTitleAlert().replace("%numberIP%", String.valueOf(blacklist.size()))));

					for (ProxiedPlayer pl : getProxy().getPlayers()) {
						if (notifications.contains(pl)) {
							if (pl.hasPermission("antibot.notifications")) {
								t.send(pl);
							}
						}
					}
				}

				List<String> temp = new ArrayList<>();
				for (String s : blacklist) {
					if (map.containsKey(s)) {
						if (map.get(s) < config.getLimit()) {
							temp.add(s);
						}
					} else {
						temp.add(s);
					}
				}
				if (!temp.isEmpty()) {
					blacklist.removeAll(temp);
				}

				map.clear();

			}, 1, config.getRefresh(), TimeUnit.SECONDS);
		}
	}
	
	private int getMaxPlayers() {
		int maxplayers = 0;
		for(ListenerInfo listener:getProxy().getConfigurationAdapter().getListeners()){
		    maxplayers = listener.getMaxPlayers();
		    break;
		}
		return maxplayers;
	}
	
}

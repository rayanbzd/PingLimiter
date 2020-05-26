package fr.futurixel.zencraft.proxy.pinglimiter;

import java.io.IOException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Commands extends Command{
	
	private final PingLimiter main;
	public Commands(String name, PingLimiter main) {
		super(name);
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (sender.hasPermission("pinglimiter.admin")) {

			if (args.length == 0) {
				sender.sendMessage(TextComponent.fromLegacyText("§8====================================================="));
				sender.sendMessage(TextComponent.fromLegacyText("§aPing§6Limiter §7DDOS Protection §8- §7Version " + main.getDescription().getVersion()));
				sender.sendMessage(TextComponent.fromLegacyText("§7By Futurixel §8- §7Support:§a https://bit.ly/34DavyY"));
				sender.sendMessage(TextComponent.fromLegacyText("§8§m-----------------------------------------------------"));
				sender.sendMessage(TextComponent.fromLegacyText("§6/pinglimiter §8- §7Display this help message"));
				sender.sendMessage(TextComponent.fromLegacyText("§6/pinglimiter reload §8- §7Reload configuration"));
				sender.sendMessage(TextComponent.fromLegacyText("§6/pinglimiter alert §8- §7Alerts on attack (only for players)"));
				sender.sendMessage(TextComponent.fromLegacyText("§8====================================================="));
			}

			if (args.length == 1) {

				if (args[0].equalsIgnoreCase("reload")) {
					try {
						main.config.loadConfig();

						main.runMainTask();

						sender.sendMessage(TextComponent.fromLegacyText(main.config.getConfigReloaded()));
					} catch (IOException e) {
						sender.sendMessage(TextComponent.fromLegacyText("§cError. Please look the console..."));
						e.printStackTrace();
					}
				}

				if (args[0].equalsIgnoreCase("alert")) {
					if (sender instanceof ProxiedPlayer) {
						ProxiedPlayer p = (ProxiedPlayer) sender;
						if (main.notifications.contains(p)) {
							main.notifications.remove(p);
							p.sendMessage(TextComponent.fromLegacyText(main.config.getDisabledNotifications()));
						} else {
							main.notifications.add(p);
							p.sendMessage(TextComponent.fromLegacyText(main.config.getEnabledNotifications()));
						}
					} else {
						sender.sendMessage(TextComponent.fromLegacyText("§cOnly a player can run this command."));
					}
				}

			}

		}else {
			sender.sendMessage(TextComponent.fromLegacyText(main.config.getNoPermission()));
		}
	}

}

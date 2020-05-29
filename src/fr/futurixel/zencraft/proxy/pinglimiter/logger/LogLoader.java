package fr.futurixel.zencraft.proxy.pinglimiter.logger;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import fr.futurixel.zencraft.proxy.pinglimiter.PingLimiter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.log.ConciseFormatter;

public class LogLoader {
	
	private PingLimiter main;
	public LogLoader(PingLimiter main) {
		this.main = main;
	}
	
	public void loadLoggerFilter() {
		if(main.config.isConsoleFilter()) {
			if(isLog4J()) {
				new Log4JFilter(main).loadFiltrer();
				main.getLogger().log(Level.INFO, "§aLog4J found! Start log filtering...");
			}else {
				loadDefaultFilter();
				main.getLogger().log(Level.WARNING, "§cLog4J was not found. §eEnabled default logger filter. Highly recommended to use WaterFall to reduce CPU Usage.");
			}
		}else {
			main.getLogger().log(Level.WARNING, "§cConsole filters are disabled. It is strongly recommended to enable them in order to reduce spam in the console and CPU usage.");
		}
	}
	
	private void loadDefaultFilter() {
		Logger logger = BungeeCord.getInstance().getLogger();
		logger.setFilter(new Filter() {
			@Override
			public boolean isLoggable(LogRecord record) {
				boolean canLog = true;
				LogRecord formated = record;
				if (record.getMessage().length() > 2) {
					String msg = new ConciseFormatter().formatMessage(formated).trim();
					for (String word : main.config.getFilter()) {
						if (record.getMessage().contains(word)||msg.contains(word)) {

							canLog = false;
							break;
						}
					}
				}
				return canLog;
			}
		});
	}
	
	private boolean isLog4J() {
		try {
			org.apache.logging.log4j.LogManager.getRootLogger();
			return true;
		}catch(NoClassDefFoundError ex) {
			return false;
		}
	}

}

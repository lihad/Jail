package main.java.com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import main.java.com.matejdro.bukkit.jail.Jail;
import main.java.com.matejdro.bukkit.jail.Util;

public class JailReloadCommand extends BaseCommand {
	
	public JailReloadCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailreload";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Jail.instance.IO.LoadSettings();
		Jail.instance.IO.LoadJails();
		Jail.instance.IO.LoadPrisoners();
		Jail.instance.IO.LoadCells();
		
		Util.Message("&6Jail data reloaded.", sender);
		return true;
	}

}

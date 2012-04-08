package main.java.com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import main.java.com.matejdro.bukkit.jail.PrisonerManager;

public class JailCommand extends BaseCommand {	
	public JailCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jail";
	}
	
	public Boolean run(CommandSender sender, String[] args) {		
		PrisonerManager.PrepareJail(sender, args);
		return true;
		
	}

}

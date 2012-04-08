package main.java.com.matejdro.bukkit.jail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.com.matejdro.bukkit.jail.commands.BaseCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailCheckCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailClearCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailClearForceCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailCreateCellsCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailCreateCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailCreateWeCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailDeleteCellCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailDeleteCellsCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailDeleteCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailListCellsCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailListCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailMuteCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailPayCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailReloadCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailSetCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailStatusCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailStickCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailStopCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailTeleInCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailTeleOutCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailTransferAllCommand;
import main.java.com.matejdro.bukkit.jail.commands.JailTransferCommand;
import main.java.com.matejdro.bukkit.jail.commands.UnJailCommand;
import main.java.com.matejdro.bukkit.jail.commands.UnJailForceCommand;
import main.java.com.matejdro.bukkit.jail.listeners.JailBlockListener;
import main.java.com.matejdro.bukkit.jail.listeners.JailEntityListener;
import main.java.com.matejdro.bukkit.jail.listeners.JailPlayerListener;
import main.java.com.matejdro.bukkit.jail.listeners.JailPlayerProtectionListener;

public class Jail extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");

	private JailPlayerListener playerListener;
	private JailBlockListener blockListener;
	private JailPlayerProtectionListener playerPreventListener;
	private JailEntityListener entityListener;
	public JailAPI API;
	public InputOutput IO;
	public static HashMap<String,JailZone> zones = new HashMap<String,JailZone>();
	public static HashMap<String,JailPrisoner> prisoners = new HashMap<String,JailPrisoner>();
	public static HashMap<Wolf, JailPrisoner> guards = new HashMap<Wolf, JailPrisoner>();
	public static HashMap<Player, Boolean> jailStickToggle = new HashMap<Player, Boolean>();
	private Timer timer;
	private int UpdateTime = 1;
	
	public static Jail instance;
	
	public static Plugin permissions = null;
	
	public static Boolean timeUpdateRunning = false;
	
	private HashMap<String, BaseCommand> commands = new HashMap<String, BaseCommand>();

	//Test
	//public Jail(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		//super(pluginLoader, instance, desc, folder, plugin, cLoader);
		
		
      // }

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		timer.stop();
		InputOutput.freeConnection();
		for (Wolf w : guards.keySet())
			w.remove();
	}

	@Override
	public void onEnable() {
		instance = this;
		playerListener = new JailPlayerListener();
		blockListener = new JailBlockListener();
		playerPreventListener = new JailPlayerProtectionListener(this);
		entityListener = new JailEntityListener(this);
		IO = new InputOutput();
		API = new JailAPI();
		UpdateTime = 0;
		
		IO.LoadSettings();
		IO.PrepareDB();
		IO.LoadJails();
		IO.LoadPrisoners();
		IO.LoadCells();
		
		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(entityListener, this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(playerPreventListener, this);
		
		timer = new Timer(1000,action);
		timer.start();
				
		commands.put("jail", new JailCommand());
		commands.put("unjail", new UnJailCommand());
		commands.put("jaildelete", new JailDeleteCommand());
		commands.put("jailcreatecells", new JailCreateCellsCommand());
		commands.put("jailtelein", new JailTeleInCommand());
		commands.put("jailteleout", new JailTeleOutCommand());
		commands.put("unjailforce", new UnJailForceCommand());
		commands.put("jailclear", new JailClearCommand());
		commands.put("jailclearforce", new JailClearForceCommand());
		commands.put("jailtransfer", new JailTransferCommand());
		commands.put("jailtransferall", new JailTransferAllCommand());
		commands.put("jailstatus", new JailStatusCommand());
		commands.put("jailcheck", new JailCheckCommand());
		commands.put("jaillist", new JailListCommand());
		commands.put("jailmute", new JailMuteCommand());
		commands.put("jailstop", new JailStopCommand());
		commands.put("jailset", new JailSetCommand());
		commands.put("jailpay", new JailPayCommand());
		commands.put("jailcreate", new JailCreateCommand());
		commands.put("jaildeletecells", new JailDeleteCellsCommand());
		commands.put("jaillistcells", new JailListCellsCommand());
		commands.put("jailstick", new JailStickCommand());
		commands.put("jailcreatewe", new JailCreateWeCommand());
		commands.put("jaildeletecell", new JailDeleteCellCommand());
		commands.put("jailreload", new JailReloadCommand());
		
		IO.initMetrics();

		log.info("[Jail] " + getDescription().getFullName() + " loaded!");
	}
		
	ActionListener action = new ActionListener ()
    {
      public void actionPerformed (ActionEvent event)
      {
    	  if (Jail.timeUpdateRunning) return; 
		  Jail.timeUpdateRunning = true;
    	  if (UpdateTime == 0)
    	  {
    		  getServer().getScheduler().scheduleSyncDelayedTask(Jail.instance, new Runnable() {

    			    public void run() {
    			    	
    	    	    	  for (JailPrisoner prisoner : prisoners.values().toArray(new JailPrisoner[0]))
    	    	    	  {
    	    	    	      Util.debug(prisoner, "Time event");
    	    	    		  Player player = getServer().getPlayerExact(prisoner.getName());
    	    	    		  if (prisoner.getRemainingTime() > 0 && (player != null || (prisoner.getJail() != null && prisoner.getJail().getSettings().getBoolean(Setting.CountdownTimeWhenOffline))))
    	    	    		  {
      	    	    				  Util.debug(prisoner, "Lowering remaining time for prisoner");
    	    	    			  	  prisoner.setRemainingTime(prisoner.getRemainingTime() - 1);
    	    	        			  InputOutput.UpdatePrisoner(prisoner);   	
    	    	
    	    	    		  }
    	    	    		  else if (player != null && prisoner.getRemainingTime() == 0 && prisoner.offlinePending() == false)
	    	        		  {
    	    	    			Util.debug(prisoner, "Releasing prisoner because his time is up and he is online");
  	        					PrisonerManager.UnJail(prisoner, player);
	    	        		  }
    	    	    		  
    	    	    		  if (player != null && prisoner.getJail() != null)
    	    	    		  {
    	    	    			  if (prisoner.getJail().getSettings().getDouble(Setting.MaximumAFKTime) > 0.0)
    	    	    			  {
    	    	    				  prisoner.setAFKTime(prisoner.getAFKTime() + 1);
        	    	    			  if (prisoner.getAFKTimeMinutes() > prisoner.getJail().getSettings().getDouble(Setting.MaximumAFKTime))
        	    	    			  {
        	    	    	    	      Util.debug(prisoner, "Prisoner is AFK. Let's kick him");
        	    	    				  prisoner.setAFKTime(0);
        	    	    				  player.kickPlayer(prisoner.getJail().getSettings().getString(Setting.MessageAFKKick));
        	    	    			  }
    	    	    			  }
    	    	    		  }
    			    }
    			    }
    			    
    			}, 1L);
        		  
    		  UpdateTime++;
    		  
    	  
    	  }
    	  else
    	  {
    		  UpdateTime++;
    		  if (UpdateTime > 9)
    		  {
    			  UpdateTime = 0;
    		  }
    	  }
    	  
		  for (JailPrisoner prisoner : prisoners.values())
    	  {
			  Player player = getServer().getPlayerExact(prisoner.getName());
			  if (player == null || prisoner.getJail() == null || player.getGameMode() == GameMode.CREATIVE) continue;
			  if (!prisoner.getJail().getSettings().getBoolean(Setting.EnableFoodControl)) continue;
    		  int minfood = prisoner.getJail().getSettings().getInt(Setting.FoodControlMinimumFood);
			  int maxfood = prisoner.getJail().getSettings().getInt(Setting.FoodControlMaximumFood);
			 
			  if (player.getFoodLevel() <  minfood || player.getFoodLevel() > maxfood)
			  {
				  player.setFoodLevel((minfood + maxfood) / 2);
			  }
	  		}
    	  
    	Jail.timeUpdateRunning = false;

      }
    };
    
    

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	BaseCommand cmd = commands.get(command.getName().toLowerCase());
    	if (cmd != null) return cmd.execute(sender, args);
    	return false;
    }
    

}

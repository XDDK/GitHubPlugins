package eu.playervisibility.main;

/*import java.util.Arrays;*/
import java.io.File;
import java.util.Date;
import java.util.HashMap;
/*import java.util.List;*/
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerVisibility extends JavaPlugin implements Listener {

	public boolean pluginOn = false;
	Map<String, Long> intervaleAsteptateDeCatreJucatori = new HashMap<String, Long>();
	
	public final Logger logger = Logger.getLogger("Minecraft");
	public static PlayerVisibility plugin;
	
	@Override
	public void onEnable() {
		if(!new File(this.getDataFolder(), "config.yml").exists()){
			this.saveDefaultConfig();
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
	}
	
	@Override
	public void onDisable() {

	}
	
    public void loadConfig()
    {
        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        cfg.options().copyHeader(true);
        saveConfig();
    }
    
    String matON = getConfig().getString("PlayerVisibility.itemMaterialON");
    String matOFF = getConfig().getString("PlayerVisibility.itemMaterialOFF");
    
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (pluginOn = true) {
			if (player.getInventory().contains(makeVanishItem(Material.valueOf(matON) , getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON")))) ||
					player.getInventory().contains(makeVanishItem(Material.valueOf(matOFF) , getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))))) {
				player.getInventory().clear();
				showAllPlayers(player);
				player.getInventory().addItem(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
			} else {
				showAllPlayers(player);
				player.getInventory().addItem(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
			}
		}
	}

	public void hideAllPlayers(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			player.hidePlayer(p);
		}
	}

	public void showAllPlayers(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			player.showPlayer(p);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_RED + "You can only do this as a player!");
			return false;
		}
		Player player = (Player) sender;
        if(label.equalsIgnoreCase("pvenable")){
        	if(player.hasPermission("pv.enable") || player.isOp()){
        		pluginOn = true;
        		player.sendMessage(ChatColor.GREEN + "Ai activat plugin-ul de vizibilitate");
        	} else {
        		player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        	}
        }
        if(label.equalsIgnoreCase("pvdisable")){
        	if(player.hasPermission("pv.disable") || player.isOp()){
        		pluginOn = false;
        		player.sendMessage(ChatColor.RED + "Ai dezactivat plugin-ul de vizibilitate");
        	} else {
        		player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        	}
        }
		if (label.equalsIgnoreCase("pvhide") || label.equalsIgnoreCase("playervisibilityshow")) {
			if (player.hasPermission("pv.hide") || player.isOp()) {
				hideAllPlayers(player);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityDeactivated")));
			} else
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		if (label.equalsIgnoreCase("pvshow") || label.equalsIgnoreCase("playervisibilityshow")) {
			if (player.hasPermission("pv.show") || player.isOp()) {
				showAllPlayers(player);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityActivated")));
			} else
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		if (label.equalsIgnoreCase("pvtoolon") || label.equalsIgnoreCase("playervisibilitytoolon")) {
			if (player.hasPermission("pv.toolon") || player.isOp()) {
				Player p = player.getPlayer();
				p.getInventory().addItem(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));//ChatColor.GOLD + getConfig().getString("PlayerVisibility.displayNameON")));
			} else
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		if (label.equalsIgnoreCase("pvtooloff") || label.equalsIgnoreCase("playervisibilitytooloff")) {
			if (player.hasPermission("pv.tooloff") || player.isOp()) {
				Player p = player.getPlayer();
				p.getInventory().addItem(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))));
			} else
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		if(label.equalsIgnoreCase("pvreload")){
			if (player.hasPermission("pv.reload") || player.isOp()) {
				this.reloadConfig();
			    System.out.println(ChatColor.GREEN + "Configuarion reloaded!");
			    player.sendMessage(ChatColor.GREEN + "Configuarion reloaded!");
			}
		}/*
		if(label.equalsIgnoreCase("setdisplaynameon")){
			if(player.hasPermission("pv.setdisplaynameon") || player.isOp()){
		        String msgOld = args[0];
				this.getConfig().set("PlayerVisibility.displayNameON", msgOld);
				this.saveConfig();
				String msg = getConfig().getString("PlayerVisibility.displayNameON");
				player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
			}
		}
		if(label.equalsIgnoreCase("setdisplaynameoff")){
			if(player.hasPermission("pv.setdisplaynameoff") || player.isOp()){
		        String msgOld = args[0];
				this.getConfig().set("PlayerVisibility.displayNameOFF", msgOld);
				this.saveConfig();
				String msg = getConfig().getString("PlayerVisibility.displayNameOFF");
				player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
			}
		}
		if(label.equalsIgnoreCase("setmessage")){
			if(player.hasPermission("pv.setmessage") || player.isOp()){
				String typeOfMessage = args[0];
		        String msgOld = args[1];
				if(typeOfMessage.equals("visibilityactivated")){
					this.getConfig().set("messages.visibilityActivated", msgOld);
					this.saveConfig();
					String msg = getConfig().getString("messages.visibilityActivated");
					player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
				} else if(typeOfMessage.equals("visibilitydeactivated")){
					this.getConfig().set("messages.visibilityDeactivated", msgOld);
					this.saveConfig();
					String msg = getConfig().getString("messages.visibilityDeactivated");
					player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
				} else if(typeOfMessage.equals("timermessage")){
					this.getConfig().set("messages.timerMessage", msgOld);
					this.saveConfig();
					String msg = getConfig().getString("messages.timerMessage");
					player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
				} else if(typeOfMessage.equals("nopermmessage")){
					this.getConfig().set("messages.noPermMessage", msgOld);
					this.saveConfig();
					String msg = getConfig().getString("messages.noPermMessage");
					player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
				}
			}
		}*/

		return false;
	}

	public ItemStack makeVanishItem(Material material, int amount, int shrt, String displayName/*, List<String> lore*/) {
		ItemStack item = new ItemStack(material, amount, (short) shrt);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		/*meta.setLore(lore);*/
		item.setItemMeta(meta);
		return item;
	}

	public void hide(Player player) {
		hideAllPlayers(player);
		player.getInventory().removeItem(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
		player.setItemInHand(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityDeactivated")));
	}

	public void show(Player player) {
		showAllPlayers(player);
		player.getInventory().removeItem(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))));
		player.setItemInHand(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityActivated")));
	}

	public void asteptare(Player player, boolean hide) {
		if (intervaleAsteptateDeCatreJucatori.containsKey(player.getName())) {
			long atunci = intervaleAsteptateDeCatreJucatori.get(player.getName());
			Date dataDeAcum = new Date();
			long acum = dataDeAcum.getTime();
			int timeDelay = getConfig().getInt("PlayerVisibility.timeDelay") * 1000;
			if ((acum - atunci) > timeDelay) {
				if (hide)
					hide(player);
				else
					show(player);

				intervaleAsteptateDeCatreJucatori.put(player.getName(), acum);
			} else if ((acum - atunci) < timeDelay) {
				String message = getConfig().getString("messages.timerMessage");
				String timeLeftDelay = String.valueOf(timeDelay/1000);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replaceAll("%timeDelay%", timeLeftDelay)));
			}
		} else {
			Date acum = new Date();
			intervaleAsteptateDeCatreJucatori.put(player.getName(), acum.getTime());
			if (hide)
				hide(player);
			else
				show(player);
		}
	}

	@EventHandler
	public void onPlayerClickEvent(PlayerInteractEvent event) {

		final Player player = event.getPlayer();

		if (player.getItemInHand().equals(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.hasPermission("pv.torch.hide") || player.isOp()) {
					if (player.hasPermission("pv.byepass") || player.isOp()) {
						hide(player);
					} else {
						asteptare(player, true);
					}
				} else
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.noPermMessage")));
			}
		} else if (player.getItemInHand().equals(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.hasPermission("pv.torch.show") || player.isOp()) {
					if (player.hasPermission("pv.byepass") || player.isOp()) {
						show(player);
					} else {
						asteptare(player, false);
					}
				} else
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.noPermMessage")));
			}
		}
	}
	
	//EVENTS

	@EventHandler
	public void onPickupEvent(PlayerPickupItemEvent event) {

		Player player = event.getPlayer();
		if (!player.isOp() || !player.hasPermission("pv.pick")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.YELLOW + "You are not permitted to pickup items!");
		}

	}

	@EventHandler
	public void onDropEvent(PlayerDropItemEvent event) {

		Player player = event.getPlayer();
		if (!player.isOp() || !player.hasPermission("pv.drop")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.YELLOW + "You are not permitted to drop items!");
		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		Player player = event.getPlayer();
		if (!player.isOp() || !player.hasPermission("pv.break")) {
			event.setCancelled(true);
		}
	}
}
package eu.playervisibility.main;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
/*import java.util.Arrays;*/
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerVisibility extends JavaPlugin implements Listener {

	public boolean pluginOn = true;
	Map<String, Long> intervaleAsteptateDeCatreJucatori = new HashMap<String, Long>();
	Map<Player, Boolean> isHidingPlayers = new HashMap<>();
	private boolean debug = false;

	public final Logger logger = Logger.getLogger("Minecraft");
	public static PlayerVisibility plugin;

	@Override
	public void onEnable() {
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			this.saveDefaultConfig();
		}

		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
		pluginOn = getConfig().getBoolean("miscellaneous.pluginOn");
		debug = getConfig().getBoolean("miscellaneous.debug");
	}

	@Override
	public void onDisable() {
		loadConfig();
	}

	public void loadConfig() {
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

		String worldFromConfig = getConfig().getString("worldToGetItem.world");
		String currentWorld = player.getWorld().getName();
		if (debug) {
			System.out.println("[DEBUG] World we have set in the config.yml " + worldFromConfig);
			System.out.println("[DEBUG] Current world of the player(on join) " + currentWorld);
		}
		if (worldFromConfig.equals(currentWorld)) {
			if (debug) {
				System.out.println("[DEBUG] The player is in the specified world!");
			}
			if (pluginOn = true) {
				// player.setGameMode(GameMode.ADVENTURE);
				if (player.getInventory().contains(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON")))) || player.getInventory().contains(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))))) {
					player.getInventory().clear();
					showAllPlayers(player);
					player.getInventory().setItem(getConfig().getInt("PlayerVisibility.slot") - 1, makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
				} else {
					showAllPlayers(player);
					player.getInventory().setItem(getConfig().getInt("PlayerVisibility.slot") - 1, makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
				}
			}
		} else {
			if (debug) {
				System.out.println("[DEBUG] Not in the specified world!");
			}
		}

		if (getServer().getOnlinePlayers() != null) {
			for (Player pl : getServer().getOnlinePlayers()) {
				if (isHidingPlayers.get(pl) != null) {
					if (isHidingPlayers.get(pl) == true) {
						hide(pl);
					}
				}
			}
		}
	}

	public void hideAllPlayers(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			boolean playerWithPerm = p.hasPermission("pv.isHideable");
			if (!playerWithPerm) { // if player has pv.isHideable permission,
									// hide player
				player.hidePlayer(p);
			}
		}
	}

	public void showAllPlayers(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			player.showPlayer(p);
		}
	}

	public static String stringFromArguments(String[] argumente, int inceput, int numarArgumenteMinim, Player player) throws InsufficientArgumentsException {
		StringBuilder sb = new StringBuilder();

		if (argumente.length < numarArgumenteMinim) {
			throw new InsufficientArgumentsException();
		} else {

			for (int i = inceput; i < argumente.length; i++) {
				sb.append(argumente[i]);
				sb.append(" ");
			}
		}
		return sb.toString().trim();

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_RED + "You can only do this as a player!");
			return false;
		}
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("pvenable")) {
			if (player.hasPermission("pv.enable") || player.isOp()) {
				pluginOn = true;
				player.sendMessage("§2You activated the Visibility plugin!");
			} else {
				player.sendMessage("§cYou don't have permission to use this command.");
			}
		}
		if (label.equalsIgnoreCase("pvdisable")) {
			if (player.hasPermission("pv.disable") || player.isOp()) {
				pluginOn = false;
				player.sendMessage("§cYou deactivated the Visibility plugin!");
			} else {
				player.sendMessage("§cYou don't have permission to use this command.");
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
				if (!getConfig().getString("messages.visibilityActivated").isEmpty()) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityActivated")));
				}
			} else
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		if (label.equalsIgnoreCase("pvtoolon") || label.equalsIgnoreCase("playervisibilitytoolon")) {
			if (player.hasPermission("pv.toolon") || player.isOp()) {
				Player p = player.getPlayer();
				p.getInventory().addItem(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));// ChatColor.GOLD
																																																																					// +
																																																																					// getConfig().getString("PlayerVisibility.displayNameON")));
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
		if (label.equalsIgnoreCase("pvreload")) {
			if (player.hasPermission("pv.reload") || player.isOp()) {
				this.reloadConfig();
				this.saveConfig();
				System.out.println(ChatColor.GREEN + "Configuarion reloaded!");
				player.sendMessage(ChatColor.GREEN + "Configuarion reloaded!");
			}
		}
		if (label.equalsIgnoreCase("setdisplaynameon")) {
			if (player.hasPermission("pv.setdisplaynameon") || player.isOp()) {
				try {
					this.getConfig().set("PlayerVisibility.displayNameON", stringFromArguments(args, 0, 1, player));
					this.saveConfig();
					String msg = getConfig().getString("PlayerVisibility.displayNameON");
					player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
				} catch (InsufficientArgumentsException e) {
					player.sendMessage(ChatColor.DARK_RED + "You can't set an invalid message!");
				}
			}
		}
		if (label.equalsIgnoreCase("setdisplaynameoff")) {
			if (player.hasPermission("pv.setdisplaynameoff") || player.isOp()) {
				try {
					if (args.length > 1) {
						this.getConfig().set("PlayerVisibility.displayNameOFF", stringFromArguments(args, 0, 1, player));
					} else {
						this.getConfig().set("PlayerVisibility.displayNameOFF", null);
					}
					this.saveConfig();
					String msg = getConfig().getString("PlayerVisibility.displayNameOFF");
					player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
				} catch (InsufficientArgumentsException e) {
					player.sendMessage(ChatColor.DARK_RED + "You can't set an invalid message!");
				}
			}
		}
		if (label.equalsIgnoreCase("setmessage")) {
			if (player.hasPermission("pv.setmessage") || player.isOp()) {
				String typeOfMessage = args[0];
				if (typeOfMessage.equals("visibilityactivated")) {
					try {
						if (args.length > 1) {
							this.getConfig().set("messages.visibilityActivated", stringFromArguments(args, 1, 2, player));
						} else {
							this.getConfig().set("messages.visibilityActivated", null);
						}
						this.saveConfig();
						String msg = getConfig().getString("messages.visibilityActivated");
						player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
					} catch (InsufficientArgumentsException e) {
						player.sendMessage(ChatColor.DARK_RED + "You can't set an invalid message!");
					}
				} else if (typeOfMessage.equals("visibilitydeactivated")) {
					try {
						if (args.length > 1) {
							this.getConfig().set("messages.visibilityDeactivated", stringFromArguments(args, 1, 2, player));
						} else {
							this.getConfig().set("messages.visibilityDeactivated", null);
						}
						this.saveConfig();
						String msg = getConfig().getString("messages.visibilityDeactivated");
						player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
					} catch (InsufficientArgumentsException e) {
						player.sendMessage(ChatColor.DARK_RED + "You can't set an invalid message!");
					}
				} else if (typeOfMessage.equals("timermessage")) {
					try {
						this.getConfig().set("messages.timerMessage", stringFromArguments(args, 1, 2, player));
						this.saveConfig();
						String msg = getConfig().getString("messages.timerMessage");
						player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
					} catch (InsufficientArgumentsException e) {
						player.sendMessage(ChatColor.DARK_RED + "You can't set an invalid message!");
					}
				} else if (typeOfMessage.equals("nopermmessage")) {
					try {
						this.getConfig().set("messages.noPermMessage", stringFromArguments(args, 1, 2, player));
						this.saveConfig();
						String msg = getConfig().getString("messages.noPermMessage");
						player.sendMessage(ChatColor.BLUE + "Message set: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
					} catch (InsufficientArgumentsException e) {
						player.sendMessage(ChatColor.DARK_RED + "You can't set an invalid message!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You typed incorrect the type of the message!");
				}
			}
		}
		return false;
	}

	public ItemStack makeVanishItem(Material material, int amount, int shrt, String displayName/*
																								 * ,
																								 * List
																								 * <
																								 * String
																								 * >
																								 * lore
																								 */) {
		ItemStack item = new ItemStack(material, amount, (short) shrt);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		/* meta.setLore(lore); */
		item.setItemMeta(meta);
		return item;
	}

	public void hide(Player player) {
		hideAllPlayers(player);
		player.getInventory().removeItem(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
		player.setItemInHand(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityDeactivated")));
		if (isHidingPlayers.get(player) == null) {
			isHidingPlayers.put(player, true);
		}
	}

	public void show(Player player) {
		showAllPlayers(player);
		player.getInventory().removeItem(makeVanishItem(Material.valueOf(matOFF), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameOFF"))));
		player.setItemInHand(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.visibilityActivated")));
		if (isHidingPlayers.get(player) == true) {
			isHidingPlayers.put(player, false);
		}
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
				String timeLeftDelay = String.valueOf(timeDelay / 1000);
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
		Player player = event.getPlayer();
		if (player.getItemInHand().equals(makeVanishItem(Material.valueOf(matON), getConfig().getInt("PlayerVisibility.amount"), getConfig().getInt("PlayerVisibility.shrt"), ColorService.replaceCodeWithCorrectColor(getConfig().getString("PlayerVisibility.displayNameON"))))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.hasPermission("pv.torch.hide") || player.isOp()) {
					if (player.hasPermission("pv.bypass") || player.isOp()) {
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
					if (player.hasPermission("pv.bypass") || player.isOp()) {
						show(player);
					} else {
						asteptare(player, false);
					}
				} else
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.noPermMessage")));
			}
		}
	}

	// EVENTS
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		for (String worlds : getConfig().getStringList("enabledWorlds")) {
			if (worlds.equals(player.getWorld().getName())) {
				if (isHidingPlayers.get(player) != null) {
					if (isHidingPlayers.get(player) == true) {
						hide(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPickupEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (!(player.isOp() || player.hasPermission("pv.pick"))) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.YELLOW + "You are not permitted to pickup items!");
		} else {

		}

	}

	@EventHandler
	public void onDropEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!(player.isOp() || player.hasPermission("pv.drop"))) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.YELLOW + "You are not permitted to drop items!");
		} else {

		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!(player.isOp() || player.hasPermission("pv.break"))) {
			event.setCancelled(true);
		} else {

		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!(player.isOp() || player.hasPermission("pv.place"))) {
			event.setCancelled(true);
		} else {

		}
	}
}
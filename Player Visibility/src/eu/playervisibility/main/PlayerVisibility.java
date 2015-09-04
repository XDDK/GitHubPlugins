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

	private Map<String, Double> delay = new HashMap<>();
	private Map<Player, Boolean> isHidingPlayers = new HashMap<>();
	private HashMap<String, String> replacements = new HashMap<>();

	public final Logger logger = Logger.getLogger("Minecraft");
	public static PlayerVisibility plugin;

	@Override
	public void onEnable() {

		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " by xDesireRage has been enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
	}

	@Override
	public void onDisable() {
		loadConfig();
	}

	public void loadConfig() {
		FileConfiguration cfg = getConfig();
		cfg.options().copyDefaults(true);
		cfg.options().copyHeader(true);
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			saveConfig();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		String worldFromConfig = getConfig().getString("worldToGetItem.world");
		String currentWorld = player.getWorld().getName();
		if (worldFromConfig.equals(currentWorld)) {
			if (getConfig().getBoolean("miscellaneous.joinTorch")) {
				if (player.getInventory().contains(makeVanishItem(true)) || player.getInventory().contains(makeVanishItem(false))) {
					player.getInventory().remove(makeVanishItem(true));
					player.getInventory().remove(makeVanishItem(false));
					show(player, true);
					player.getInventory().setItem(getConfig().getInt("PlayerVisibility.slot") - 1, makeVanishItem(true));
				} else {
					show(player, true);
					player.getInventory().setItem(getConfig().getInt("PlayerVisibility.slot") - 1, makeVanishItem(true));
				}
			}
		}

		if (getServer().getOnlinePlayers() != null) {
			for (Player pl : getServer().getOnlinePlayers()) {
				if (isHidingPlayers(pl)) {
					hide(player);
				}
			}
		}

	}

	public static String stringFromArguments(String[] argumente, int inceput, int numarArgumenteMinim) throws InsufficientArgumentsException {
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

	public void sendMessage(String path, CommandSender player) {
		if (player != null) {
			String msg = getConfig().getString("messages." + path);
			if (replacements != null) {
				for (Map.Entry<String, String> a : replacements.entrySet()) {
					msg = msg.replace(a.getKey(), a.getValue());
				}
			}
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sendMessage("consoleDeny", sender);
			return false;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("pv")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("show")) {
					if (player.hasPermission("pv.show") || player.isOp()) {
						show(player, false);
						sendMessage("visibilityActivated", player);
					} else {
						sendMessage("noPerm", player);
					}
				} else if (args[0].equalsIgnoreCase("hide")) {
					if (player.hasPermission("pv.hide") || player.isOp()) {
						hide(player);
						sendMessage("visibilityDeactivated", player);
					} else {
						sendMessage("noPerm", player);
					}
				} else if (args[0].equalsIgnoreCase("toolon")) {
					if (player.hasPermission("pv.tool") || player.isOp()) {
						player.getInventory().setItemInHand(makeVanishItem(true));
					} else {
						sendMessage("noPerm", player);
					}
				} else if (args[0].equalsIgnoreCase("tooloff")) {
					if (player.hasPermission("pv.tool") || player.isOp()) {
						player.getInventory().setItemInHand(makeVanishItem(false));
					} else {
						sendMessage("noPerm", player);
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("pv.reload") || player.isOp()) {
						reloadConfig();
						saveConfig();
						sendMessage("configReloaded", player);
					} else {
						sendMessage("noPerm", player);
					}
				} else {
					sendMessage("invalidCmd", player);
				}
			} else if (args.length > 1) {
				try {
					if (args[0].equalsIgnoreCase("setdisplayname")) {
						if (player.hasPermission("pv.displayname") || player.isOp()) {
							replacements.put("%type%", args[1]);
							String message = stringFromArguments(args, 2, 3);
							replacements.put("%args%", message);
							if (args[1].equalsIgnoreCase("on")) {
								getConfig().set("PlayerVisibility.displayNameON", message);
							} else if (args[1].equalsIgnoreCase("off")) {
								getConfig().set("PlayerVisibility.displayNameOFF", message);
							} else {
								sendMessage("invalidCmd", player);
							}
							sendMessage("displayNameSet", player);
						} else {
							sendMessage("noPerm", player);
						}
					} else if (args[0].equalsIgnoreCase("setmessage")) {
						if (player.hasPermission("pv.setmessage") || player.isOp()) {
							replacements.put("%type%", args[1]);
							String message = stringFromArguments(args, 2, 3);
							replacements.put("%args%", message);
							if (args[1].equalsIgnoreCase("visibilityactivated") || args[1].equalsIgnoreCase("va")) {
								getConfig().set("messages.visibilityActivated", message);
							} else if (args[1].equalsIgnoreCase("visibilitydeactivated") || args[1].equalsIgnoreCase("vd")) {
								getConfig().set("messages.visibilityDeactivated", message);
							} else if (args[1].equalsIgnoreCase("delay") || args[1].equalsIgnoreCase("wait")) {
								getConfig().set("messages.delay", message);
							} else if (args[1].equalsIgnoreCase("noperm") || args[1].equalsIgnoreCase("nopermission")) {
								getConfig().set("messages.noPerm", message);
							} else if (args[1].equalsIgnoreCase("invalidcmd") || args[1].equalsIgnoreCase("invalidcommand")) {
								getConfig().set("messages.invalidCmd", message);
							} else if (args[1].equalsIgnoreCase("configreloaded")) {
								getConfig().set("messages.configReloaded", message);
							} else if (args[1].equalsIgnoreCase("displayNameSet")) {
								getConfig().set("messages.displayNameSet", message);
							} else if (args[1].equalsIgnoreCase("messageSet")) {
								getConfig().set("messages.messageSet", message);
							} else {
								sendMessage("invalidCmd", player);
							}
							saveConfig();
							reloadConfig();
							sendMessage("messageSet", player);
						} else {
							sendMessage("noPerm", player);
						}
					} else {
						sendMessage("invalidCmd", player);
					}
				} catch (Exception e) {
					sendMessage("invalidCmd", player);
				}
			} else {
				sendMessage("invalidCmd", player);
			}
		}
		return false;
	}

	public ItemStack makeVanishItem(boolean toggleOnItem) {
		String[] item;
		String displayNamePath;
		if (toggleOnItem) {
			item = getConfig().getString("PlayerVisibility.itemON").split(" ");
			displayNamePath = "PlayerVisibility.displayNameON";
		} else {
			item = getConfig().getString("PlayerVisibility.itemOFF").split(" ");
			displayNamePath = "PlayerVisibility.displayNameOFF";
		}

		ItemStack vanishItem = new ItemStack(Material.getMaterial(item[0].toUpperCase()), Integer.valueOf(item[1]), Short.valueOf(item[2]));
		ItemMeta meta = vanishItem.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(displayNamePath)));
		/* meta.setLore(lore); */
		vanishItem.setItemMeta(meta);
		return vanishItem;
	}

	public void hide(Player player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.hasPermission("pv.nonHideable")) {
				player.hidePlayer(p);
			}
		}
		player.getInventory().removeItem(makeVanishItem(true));
		player.setItemInHand(makeVanishItem(false));
		sendMessage("visibilityDeactivated", player);
		if (isHidingPlayers.get(player) == null) {
			isHidingPlayers.put(player, true);
		}
	}

	public void show(Player player, boolean join) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			player.showPlayer(p);
		}
		if (!join) {
			player.getInventory().removeItem(makeVanishItem(false));
			player.setItemInHand(makeVanishItem(true));
			sendMessage("visibilityActivated", player);
		}
		if (isHidingPlayers(player)) {
			isHidingPlayers.put(player, false);
		}
	}

	public void wait(Player player, boolean hide) {
		if (delay.containsKey(player.getName())) {
			double then = delay.get(player.getName());
			Date dateNow = new Date();
			double now = dateNow.getTime();
			int timeDelay = getConfig().getInt("PlayerVisibility.timeDelay") * 1000;
			if ((now - then) > timeDelay) {
				if (hide) {
					hide(player);
				} else {
					show(player, false);
				}
				delay.put(player.getName(), now);
			} else if ((now - then) < timeDelay) {
				double timeLeftDelay = timeDelay / 1000;
				double timePast = ((now - then) / 1000);
				double left = timeLeftDelay - timePast;

				String leftFormated = String.format("%.1f", left);

				replacements.put("%time%", leftFormated);
				sendMessage("delay", player);
			}
		} else {
			Date now = new Date();
			delay.put(player.getName(), (double) now.getTime());
			if (hide) {
				hide(player);
			} else {
				show(player, false);
			}
		}
	}

	@EventHandler
	public void onPlayerClickEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().equals(makeVanishItem(true))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.hasPermission("pv.torch.hide") || player.isOp()) {
					if (player.hasPermission("pv.bypass") || player.isOp()) {
						hide(player);
					} else {
						wait(player, true);
					}
				} else
					sendMessage("noPermMessage", player);
			}
		} else if (player.getItemInHand().equals(makeVanishItem(false))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.hasPermission("pv.torch.show") || player.isOp()) {
					if (player.hasPermission("pv.bypass") || player.isOp()) {
						show(player, false);
					} else {
						wait(player, false);
					}
				} else
					sendMessage("noPermMessage", player);
			}
		}
	}

	public boolean isHidingPlayers(Player player) {
		if (isHidingPlayers.get(player) != null) {
			if (isHidingPlayers.get(player)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		for (String worlds : getConfig().getStringList("enabledWorlds")) {
			if (worlds.equals(player.getWorld().getName())) {
				if (isHidingPlayers(player)) {
					hide(player);
				}
			}
		}
	}

	@EventHandler
	public void onPickupEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp() || player.hasPermission("pv.denypick")) {
			event.setCancelled(true);
			player.sendMessage("§8[§6!§8] §cYou are not permitted to pickup items!");
		}
	}

	@EventHandler
	public void onDropEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp() || player.hasPermission("pv.denydrop")) {
			event.setCancelled(true);
			player.sendMessage("§8[§6!§8] §cYou are not permitted to drop items!");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp() || player.hasPermission("pv.denybreak")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.isOp() || player.hasPermission("pv.denyplace")) {
			event.setCancelled(true);
		}
	}
}
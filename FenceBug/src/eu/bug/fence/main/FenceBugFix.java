package eu.bug.fence.main;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FenceBugFix extends JavaPlugin implements Listener {

    @Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void FenceGlitchFail(PlayerInteractEvent event){
    	if(event.getClickedBlock() == null) return;
    	
    	Block block = event.getClickedBlock();
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if (block.getType().equals(Material.FENCE) || block.getType().equals(Material.NETHER_FENCE)) {
    			event.setCancelled(true);
    		}
    	}
    }
}

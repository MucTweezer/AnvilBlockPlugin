package me.MucTweezer.anvilblockplugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilBlockListener implements Listener {
	
	private AnvilBlockPlugin plugin;
	
	public AnvilBlockListener(AnvilBlockPlugin p) {
		plugin = p;
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasBlock() && event.hasItem()) {
			if (plugin.isAnvilBlock(event.getClickedBlock()) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				AnvilRecipe tempRecipe = plugin.findRecipe(event.getItem().getType());
				
				if (tempRecipe != null && event.getPlayer().getInventory().contains(tempRecipe.getMaterial())) {
					plugin.useIngredient(event.getPlayer().getInventory(), tempRecipe.getMaterial());
					ItemStack tempItem = tempRecipe.getResult();
					tempItem.addEnchantments(event.getItem().getEnchantments());
					event.getPlayer().setItemInHand(tempItem);
					event.getPlayer().sendMessage(ChatColor.GOLD + tempRecipe.getResult().getType().name() + " was repaired using one " + tempRecipe.getMaterial().name() + ".");
					event.getPlayer().updateInventory();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBrokenBlock(BlockBreakEvent event) {
		if (plugin.isAnvilBlock(event.getBlock())) {
			plugin.removeAnvilBlock(event.getBlock());
		}
	}
}

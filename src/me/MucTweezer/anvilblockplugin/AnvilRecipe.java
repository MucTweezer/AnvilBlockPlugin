package me.MucTweezer.anvilblockplugin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AnvilRecipe {
	private ItemStack myResult;
	private Material myTool;
	private Material myMaterial;
	
	public AnvilRecipe(Material tempTool, Material tempMaterial) {
		myResult = new ItemStack(tempTool, 1);
		myTool = tempTool;
		myMaterial = tempMaterial;
	}
	
	// Getter methods
	public ItemStack getResult() {
		return myResult;
	}
	public Material getTool() {
		return myTool;
	}
	public Material getMaterial() {
		return myMaterial;
	}
}

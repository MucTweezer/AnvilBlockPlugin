package me.MucTweezer.anvilblockplugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilBlockPlugin extends JavaPlugin {
	
	private final String SAVE_FILE = "anvilblocks.yml";

	private Logger log;
	private ArrayList<AnvilRecipe> recipeList;
	private ArrayList<Location> anvilBlocks;
	private boolean enchantingSupportEnabled;
	
	private File customConfigFile;
    private FileConfiguration customConfig;
	
	public void onEnable() {
		enchantingSupportEnabled = true;
		new AnvilBlockListener(this);
		log = this.getLogger();
		recipeList = new ArrayList<AnvilRecipe>();
		anvilBlocks = new ArrayList<Location>();
		populateRecipeList();
		
		reloadCustomConfig();
		loadAnvilBlockList();
		
		log.info("AnvilBlocks have been enabled.");
	}
	public void onDisable() {
		saveAnvilBlockList();
		saveCustomConfig();
		
		log.info("AnvilBlocks have been disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player && args.length == 1 && cmd.getName().equalsIgnoreCase("anvilrecipes")) {
			if (args[0].equalsIgnoreCase("enableenchanting")) {
				setEnchantingSupport(false);
				sender.sendMessage("Enchanting support for anvil recipes has been disabled.");
				return true;
			} else if (args[0].equalsIgnoreCase("disableenchanting")) {
				setEnchantingSupport(true);
				sender.sendMessage("Enchanting support for anvil recipes has been enabled.");
				return true;
			} else if (args[0].equalsIgnoreCase("set")) {
				if (addAnvilBlock((Player)sender)) {
	            	sender.sendMessage("Anvil block set.");
	            	return true;
	            } else {
	            	sender.sendMessage("Anvil block could not be set.");
	            	return false;
	            }
			} else {
				AnvilRecipe tempRecipe = findRecipe(Material.getMaterial(args[0].toUpperCase()));
				
				if (tempRecipe != null) {
					sender.sendMessage("To repair " + args[0]+ " you need to use " + tempRecipe.getMaterial().name() + ".");
					return true;
				} else {
					sender.sendMessage("That item was not recognized.");
					return false;
				}
			}
		}
		
		return false;
	}
	
	public void populateRecipeList() {
		// Wooden Tools
		recipeList.add(new AnvilRecipe(Material.WOOD_AXE, Material.WOOD));
		recipeList.add(new AnvilRecipe(Material.WOOD_HOE, Material.WOOD));
		recipeList.add(new AnvilRecipe(Material.WOOD_PICKAXE, Material.WOOD));
		recipeList.add(new AnvilRecipe(Material.WOOD_SPADE, Material.WOOD));
		recipeList.add(new AnvilRecipe(Material.WOOD_SWORD, Material.WOOD));
		recipeList.add(new AnvilRecipe(Material.FISHING_ROD, Material.WOOD));
		
		// Stone Tools
		recipeList.add(new AnvilRecipe(Material.STONE_AXE, Material.COBBLESTONE));
		recipeList.add(new AnvilRecipe(Material.STONE_HOE, Material.COBBLESTONE));
		recipeList.add(new AnvilRecipe(Material.STONE_PICKAXE, Material.COBBLESTONE));
		recipeList.add(new AnvilRecipe(Material.STONE_SPADE, Material.COBBLESTONE));
		recipeList.add(new AnvilRecipe(Material.STONE_SWORD, Material.COBBLESTONE));
		
		// Iron Tools
		recipeList.add(new AnvilRecipe(Material.IRON_AXE, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_HOE, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_PICKAXE, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_SPADE, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_SWORD, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.SHEARS, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.FLINT_AND_STEEL, Material.IRON_INGOT));
		
		// Gold Tools
		recipeList.add(new AnvilRecipe(Material.GOLD_AXE, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_HOE, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_PICKAXE, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_SPADE, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_SWORD, Material.GOLD_INGOT));
		
		// Diamond Tools
		recipeList.add(new AnvilRecipe(Material.DIAMOND_AXE, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_HOE, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_PICKAXE, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_SPADE, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_SWORD, Material.DIAMOND));
		
		// Leather Armour
		recipeList.add(new AnvilRecipe(Material.LEATHER_BOOTS, Material.LEATHER));
		recipeList.add(new AnvilRecipe(Material.LEATHER_CHESTPLATE, Material.LEATHER));
		recipeList.add(new AnvilRecipe(Material.LEATHER_HELMET, Material.LEATHER));
		recipeList.add(new AnvilRecipe(Material.LEATHER_LEGGINGS, Material.LEATHER));
		
		// Iron Armour
		recipeList.add(new AnvilRecipe(Material.IRON_BOOTS, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_CHESTPLATE, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_HELMET, Material.IRON_INGOT));
		recipeList.add(new AnvilRecipe(Material.IRON_LEGGINGS, Material.IRON_INGOT));
		
		// Gold Armour
		recipeList.add(new AnvilRecipe(Material.GOLD_BOOTS, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_CHESTPLATE, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_HELMET, Material.GOLD_INGOT));
		recipeList.add(new AnvilRecipe(Material.GOLD_LEGGINGS, Material.GOLD_INGOT));
		
		// Diamond Armour
		recipeList.add(new AnvilRecipe(Material.DIAMOND_BOOTS, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_CHESTPLATE, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_HELMET, Material.DIAMOND));
		recipeList.add(new AnvilRecipe(Material.DIAMOND_LEGGINGS, Material.DIAMOND));
	}
	
	public AnvilRecipe findRecipe(Material tempMaterial) {
		// Returns the AnvilRecipe with the argument Material that is the result of the recipe. Returns null if no match is found.
		
		ListIterator<AnvilRecipe> myIterator = recipeList.listIterator();
		AnvilRecipe tempRecipe = null;
		boolean foundRecipe = false;
		
		while (myIterator.hasNext() && !foundRecipe) {
			tempRecipe = myIterator.next();
			if (tempRecipe.getResult().getType().equals(tempMaterial)) {
				foundRecipe = true;
			}
		}
		
		if (foundRecipe) {
			return tempRecipe;
		} else {
			return null;
		}
	}
	
	public void useIngredient(PlayerInventory myInventory, Material myIngredient) {
		int index = myInventory.first(myIngredient);
		ItemStack ingredientStack = myInventory.getItem(index);
		
		ingredientStack.setAmount(ingredientStack.getAmount()-1);
		
		myInventory.setItem(index, ingredientStack);
	}
	
	public boolean enchantingSupportIsEnabled() {
		return enchantingSupportEnabled;
	}
	public void setEnchantingSupport(boolean myBool) {
		enchantingSupportEnabled = myBool;
	}
	
    private boolean addAnvilBlock(Player tempPlayer) {
        return anvilBlocks.add(tempPlayer.getTargetBlock(null, 100).getLocation());
    }
    public boolean removeAnvilBlock(Block tempBlock) {
        return anvilBlocks.remove(tempBlock.getLocation());
    }

    public boolean isAnvilBlock(Block tempBlock) {
        ListIterator<Location> tempIterator = anvilBlocks.listIterator();
        Location blockLocation = tempBlock.getLocation();
        Location tempLocation = null;

        while (tempIterator.hasNext()) {
        	tempLocation = tempIterator.next();
        	//System.out.println(tempLocation.getBlockX() + " " + blockLocation.getBlockX());
        	//System.out.println(tempLocation.getBlockY() + " " + blockLocation.getBlockY());
        	//System.out.println(tempLocation.getBlockZ() + " " + blockLocation.getBlockZ());
        	if (tempLocation.getBlockX() == blockLocation.getBlockX() && 
        		tempLocation.getBlockY() == blockLocation.getBlockY() &&
        		tempLocation.getBlockZ() == blockLocation.getBlockZ()) {
        		//System.out.println("Anvil block found.");
        		return true;
        	}
        }

        return false;
    }
    
 // Config file methods used to save and load a file that will save which blocks are anvil blocks.
    private void reloadCustomConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(getDataFolder(), SAVE_FILE);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        InputStream configStream = getResource(SAVE_FILE);
        if (configStream != null) {
            YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(configStream);
            customConfig.setDefaults(tempConfig);
        }
    }
    private void saveCustomConfig() {
        if (customConfig != null && customConfigFile != null) {
            try {
                customConfig.save(customConfigFile);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not save the config to " + customConfigFile, e);
            }
        }
    }

    // Methods to save and load the anvilBlocks from the config file.
    private void loadAnvilBlockList() {
        // reads the anvil block locations from configFile and saves them in the anvil blocks list.

        Set<String> keyList = customConfig.getKeys(false);

        Location tempLocation = null;
        World myWorld = (World)getServer().getWorlds().get(0);
        int j = 0;

        for (int i = 0; i < (keyList.size()/3); i++) {
            tempLocation = new Location(myWorld, customConfig.getDouble("Block" + j + "x"), customConfig.getDouble("Block" + j + "y"), customConfig.getDouble("Block" + j + "z"));
            anvilBlocks.add(tempLocation);
            j++;
        }
    }
    private void saveAnvilBlockList() {
        // saves the locations of the blocks in the anvil blocks list to configFile.

        ListIterator<Location> tempIterator = anvilBlocks.listIterator();
        Location tempLocation = null;
        int i = 0;

        while (tempIterator.hasNext()) {
            tempLocation = tempIterator.next();

            customConfig.set("Block" + i + "x", tempLocation.getX());
            customConfig.set("Block" + i + "z", tempLocation.getZ());
            customConfig.set("Block" + i + "y", tempLocation.getY());
            
            i++;
        }
    }
}

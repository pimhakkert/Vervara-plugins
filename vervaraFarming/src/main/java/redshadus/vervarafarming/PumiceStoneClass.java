package redshadus.vervarafarming;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class PumiceStoneClass {
    private Main main;
    private ArrayList<Location> pumiceStoneLocations = new ArrayList<>();
    private ArrayList<Location> adminWaterLocations = new ArrayList<>();
    private ItemStack pumiceStone = new ItemStack(Material.DEAD_BRAIN_CORAL_BLOCK,1);

    public PumiceStoneClass(Main main) {
        this.main = main;
    }

    public ItemStack getPumiceStone() {
        return pumiceStone;
    }

    public ItemStack getPumiceStoneAmount(int amount) {
        ItemStack pumicestoneCopy = pumiceStone;
        pumicestoneCopy.setAmount(amount);
        return pumicestoneCopy;
    }

    public ArrayList<Location> getPSLocations(){
        return pumiceStoneLocations;
    }

    public void setPSLocations(ArrayList<Location> arrayList) {
        pumiceStoneLocations = arrayList;
    }

    public ArrayList<Location> getAWLocations(){
        return adminWaterLocations;
    }

    public void setAWLocations(ArrayList<Location> arrayList) {
        adminWaterLocations = arrayList;
    }

    public void recipe() {

        ItemMeta pumiceStoneMeta = pumiceStone.getItemMeta();

        pumiceStoneMeta.setDisplayName(ChatColor.AQUA+"Pumice Stone");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Water won't seep through this stone...");
        lore.add("Perhaps you can use it to hold water.");
        pumiceStoneMeta.setLore(lore);
        pumiceStoneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pumiceStone.setItemMeta(pumiceStoneMeta);

        ShapedRecipe pumiceRecipe = new ShapedRecipe(new NamespacedKey(main, "Vervara_pumicestone"), pumiceStone);
        pumiceRecipe.shape("LML","MNM","LML");
        //L = lapis, M = magma, N = nautilis
        pumiceRecipe.setIngredient('L',Material.LAPIS_LAZULI);
        pumiceRecipe.setIngredient('M',Material.MAGMA_BLOCK);
        pumiceRecipe.setIngredient('N',Material.NAUTILUS_SHELL);
        main.getServer().addRecipe(pumiceRecipe);
    }
}



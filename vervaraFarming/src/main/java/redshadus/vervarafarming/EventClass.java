package redshadus.vervarafarming;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.ArrayList;
import java.util.List;

public class EventClass implements Listener {
    private Main main;
    private Integer farmlandCounter = 0;

    public EventClass(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getBlockPlaced().getType() == Material.FARMLAND) {
            Block placed = e.getBlockPlaced();
            boolean isWater = false;
            boolean sayError = false;
            for(Block b : getNearbyBlocks(placed.getLocation(), 4)) {
                if(b.getType().equals(Material.WATER)) {
                    isWater = true;
                    String blockData = b.getBlockData().getAsString();
                    if(blockData.contains("level=")){
                        if(!blockData.contains("level=0")&&!blockData.contains("level=1")) {
                            //water is flowing
                            e.setCancelled(true);
                        }
                    } else {
                        System.out.println("Water without water level at "+b.getLocation());
                    }
                }
            }
            if(sayError) {
                chatAnnouncer("You are not allowed to farm using water flowing blocks further away than 1 block from the source block.",e.getPlayer());
            }
            if(!isWater) {
                e.setCancelled(true);
                chatAnnouncer("You need to farm nearby water!",e.getPlayer());
            }
        } else if(e.getBlockPlaced().getType() == Material.DEAD_BRAIN_CORAL_BLOCK) {
            if (!e.getItemInHand().hasItemMeta()) return;
            if (!e.getItemInHand().getItemMeta().hasDisplayName()) return;
            String displayName = e.getItemInHand().getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.AQUA+"Pumice Stone")) {
                ArrayList<Location> pumiceStoneList = main.pumiceStoneClass.getPSLocations();
                pumiceStoneList.add(e.getBlockPlaced().getLocation());
                main.pumiceStoneClass.setPSLocations(pumiceStoneList);
            }
        }
    }

    @EventHandler
    public void onObsidianCreate(BlockFormEvent e) {
        if(e.getNewState().getType()==Material.OBSIDIAN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getBlock().getType()==Material.OBSIDIAN) {
            e.setDropItems(false);
        } else if(main.pumiceStoneClass.getPSLocations().contains(e.getBlock().getLocation())) {
            Location location = e.getBlock().getLocation();
            location.setY(location.getBlockY()+1);
            if(e.getBlock().getWorld().getBlockAt(location).getType()==Material.WATER) {
                e.getBlock().getWorld().getBlockAt(location).setType(Material.AIR);
                chatAnnouncer("The water seeps away after removing the pumice stone...",e.getPlayer());
            } else if (e.getBlock().getWorld().getBlockAt(location).getBlockData() instanceof Waterlogged) {
                e.getBlock().getWorld().getBlockAt(location).setType(Material.AIR);
                chatAnnouncer("The water seeps away after removing the pumice stone...",e.getPlayer());
            } else if (e.getBlock().getWorld().getBlockAt(location).getType()==Material.KELP_PLANT || e.getBlock().getWorld().getBlockAt(location).getType()==Material.KELP || e.getBlock().getWorld().getBlockAt(location).getType()==Material.BUBBLE_COLUMN || e.getBlock().getWorld().getBlockAt(location).getType()==Material.SEAGRASS || e.getBlock().getWorld().getBlockAt(location).getType()==Material.TALL_SEAGRASS || e.getBlock().getWorld().getBlockAt(location).getType()==Material.TUBE_CORAL || e.getBlock().getWorld().getBlockAt(location).getType()==Material.BRAIN_CORAL || e.getBlock().getWorld().getBlockAt(location).getType()==Material. BUBBLE_CORAL || e.getBlock().getWorld().getBlockAt(location).getType()==Material.FIRE_CORAL || e.getBlock().getWorld().getBlockAt(location).getType()==Material.CONDUIT) {
                e.getBlock().getWorld().getBlockAt(location).setType(Material.AIR);
                chatAnnouncer("The water seeps away after removing the pumice stone...",e.getPlayer());
            }
        }
    }

    @EventHandler
    public void placeWater(PlayerBucketEmptyEvent e) {
        Location loc = e.getBlockClicked().getLocation();
        if(!e.getPlayer().hasPermission("pumicestoneOverride")) {

            if(e.getBlockFace()==BlockFace.NORTH) {
                loc.setZ(loc.getZ()-1);
                loc.setY(loc.getY()-1);
            } else if(e.getBlockFace()==BlockFace.SOUTH) {
                loc.setZ(loc.getZ()+1);
                loc.setY(loc.getY()-1);
            } else if(e.getBlockFace()==BlockFace.WEST) {
                loc.setX(loc.getX()-1);
                loc.setY(loc.getY()-1);
            } else if(e.getBlockFace()==BlockFace.EAST) {
                loc.setX(loc.getX()+1);
                loc.setY(loc.getY()-1);
            } else if(e.getBlockFace()==BlockFace.DOWN) {
                loc.setY(loc.getY()-2);
            }
            if(!main.pumiceStoneClass.getPSLocations().contains(loc)&&e.getBucket().equals(Material.WATER_BUCKET)) {
                chatAnnouncer("The water seeps away...",e.getPlayer());
                e.setCancelled(true);
            }
        } else {
            main.pumiceStoneClass.getAWLocations().add(loc);
            main.ioClass.saveAdminWaterLocations(main.pumiceStoneClass.getAWLocations());
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if(main.pumiceStoneClass.getPSLocations().contains(e.getBlock().getLocation())) {
            ArrayList<Location> pumiceStoneList = main.pumiceStoneClass.getPSLocations();
            pumiceStoneList.remove(e.getBlock().getLocation());
            main.pumiceStoneClass.setPSLocations(pumiceStoneList);
            e.setDropItems(false);
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), main.pumiceStoneClass.getPumiceStone());
        } else if(main.pumiceStoneClass.getAWLocations().contains(e.getBlock().getLocation())) {
            ArrayList<Location> AwList = main.pumiceStoneClass.getAWLocations();
            AwList.remove(e.getBlock().getLocation());
            main.pumiceStoneClass.setAWLocations(AwList);
        }
    }

    @EventHandler
    public void checkFarm(MoistureChangeEvent e) {
        if(farmlandCounter==2) {
            Boolean badWater = false;
            for(Block b : getNearbyBlocks(e.getBlock().getLocation(), 4)) {
                if(b.getType().equals(Material.WATER)&&!main.pumiceStoneClass.getAWLocations().contains(b.getLocation())) {
                    String blockData = b.getBlockData().getAsString();
                    if(blockData.contains("level=")){
                        if(!blockData.contains("level=0")&&!blockData.contains("level=1")) {
                            System.out.println("FLOW WATER DETECTED NEAR FARM AT "+b.getLocation());
                            checkFarmland(e.getBlock());
                            badWater = true;
                            break;
                        }
                    } else {
                        System.out.println("Water without water level at "+b.getLocation());
                    }
                }
            }
            e.setCancelled(!badWater);
            farmlandCounter = 0;
        } else {
            farmlandCounter++;
        }
    }

    private void checkFarmland(Block block){
        block.setType(Material.DIRT);
        for(Block b : getNearbyBlocks(block.getLocation(), 1)) {
            if(b.getType().equals(Material.FARMLAND)) {
                for(Block bb : getNearbyBlocks(b.getLocation(), 4)) {
                    if(bb.getType().equals(Material.WATER)&&!main.pumiceStoneClass.getAWLocations().contains(b.getLocation())) {
                        String blockData = bb.getBlockData().getAsString();
                        if(blockData.contains("level=")){
                            if(!blockData.contains("level=0")&&!blockData.contains("level=1")) {
                                //Allow water source blocks and waterflow level 1 blocks to be farm friendly.
                                block.setType(Material.DIRT);
                                checkFarmland(b);
                            }
                        } else {
                            System.out.println("Water without water level at "+bb.getLocation());
                        }
                    }
                }
            }
        }
    }

    private List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int y = location.getBlockY() - 1; y <= location.getBlockY() + 1; y++) {
            for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    //Y has 2 to make sure water above or below doesn't interact.
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    private void chatAnnouncer(String s, Player p) {
        p.sendMessage(ChatColor.GRAY+"["+ChatColor.RED+"Vervara"+ChatColor.GRAY+"]"+ChatColor.WHITE+" "+s);
    }
}

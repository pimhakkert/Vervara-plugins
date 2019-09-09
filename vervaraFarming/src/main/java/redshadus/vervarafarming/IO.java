package redshadus.vervarafarming;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class IO {
    private Main main;

    public IO(Main main) {
        this.main = main;
    }

    public void savePumiceStoneLocations(ArrayList<Location> arrayList) {
        List<String> locations = new ArrayList<>();
        for (Location location:arrayList) {
            String s = getStringFromLocation(location);
            locations.add(s);
        }
        main.getConfig().set("PumiceStone-Locations", locations);
        main.saveConfig();
    }

    public void loadPumiceStoneLocations() {
        List<String> pumiceStoneList = main.getConfig().getStringList("PumiceStone-Locations");
        for (String s:pumiceStoneList) {
            Location location = getLocationFromString(s);
            main.pumiceStoneClass.getPSLocations().add(location);
        }
    }

    public void saveAdminWaterLocations(ArrayList<Location> arrayList) {
        List<String> locations = new ArrayList<>();
        for (Location location:arrayList) {
            String s = getStringFromLocation(location);
            locations.add(s);
        }
        main.getConfig().set("AdminWater-Locations", locations);
        main.saveConfig();
    }

    public void loadAdminWaterLocations() {
        List<String> adminWaterList = main.getConfig().getStringList("AdminWater-Locations");
        for (String s:adminWaterList) {
            Location location = getLocationFromString(s);
            main.pumiceStoneClass.getAWLocations().add(location);
        }
    }

    public void pumiceStoneTimer() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                savePumiceStoneLocations(main.pumiceStoneClass.getPSLocations());
            }
        }, 200L, 6000L);
    }

    private String getStringFromLocation(Location l) {
        if (l == null) {
            return "";
        }
        return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    private Location getLocationFromString(String s) {
        if (s == null || s.trim() == "") {
            return null;
        }
        String[] parts = s.split(":");
        if (parts.length == 4) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }

}

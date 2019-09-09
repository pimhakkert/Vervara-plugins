package redshadus.vervarapvp;

import jdk.tools.jlink.resources.plugins;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PvpManager {
    private Main main;
    private ArrayList<PlayerClass> players = new ArrayList<>();
    public HashMap<PlayerClass,Integer> pvpResourceTimer = new HashMap<>();
    private List<UUID> hgwPlayers;

    public PvpManager (Main main, List<UUID> playerList) {
        this.main = main;
        hgwPlayers = playerList;
    }

    public void updateHGWPlayers() {
        for (PlayerClass player:players) {
            if(hgwPlayers.contains(player.getUUID())) {
                player.setHgwPvp(true);
            }
        }
    }

    public void stopHGW() {
        for (PlayerClass player:players) {
            player.setHgwPvp(false);
        }
    }

    public void addPlayer(UUID playerUUID) {
        PlayerClass player = new PlayerClass(main,playerUUID);
        players.add(player);
    }

    public void emptyList() {
        players = null;
    }

    public void removePlayer(UUID playerUUID) {
        players.remove(getPlayer(playerUUID));
    }

    public PlayerClass getPlayer(UUID playerUUID) {
        for (int i = 0; i<players.size();i++) {
            if (players.get(i).getUUID() == playerUUID) {
                return players.get(i);
            }
        }
        return null;
    }

    public void pvpModeAddResourceTime(PlayerClass player, Integer timeInMinutes) {
        player.allowToggle(false);
        if(player.isAdminCanToggle()) {
            player.setNoToggleReason("You mined resources recently!");
        }
        if(pvpResourceTimer.containsKey(player)) {
            if(pvpResourceTimer.get(player)<26) {
                pvpResourceTimer.put(player,pvpResourceTimer.get(player)+timeInMinutes);
                player.setNoToggleTime(pvpResourceTimer.get(player));
            } else {
                pvpResourceTimer.put(player,30);
                player.setNoToggleTime(30);
            }
        } else {
            pvpResourceTimer.put(player,timeInMinutes);
            player.setNoToggleTime(pvpResourceTimer.get(player));
        }
    }

    public void startPvpResourceTimer() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                blockHit();
            }
        }, 0L, 1200L);
    }

    public void blockHit() {
        for (Map.Entry<PlayerClass, Integer> entry : pvpResourceTimer.entrySet()) {
            if(entry.getValue()<=0) {
                entry.getKey().allowToggle(true);
                if(entry.getKey().isAdminCanToggle()) {
                    chatAnnouncer("You can now toggle pvp again!",Bukkit.getPlayer(entry.getKey().getUUID()));
                }
                removePvpResourcePlayer(entry.getKey().getUUID());
                pvpResourceTimer.remove(entry.getKey());
            } else {
                entry.getKey().setNoToggleTime(entry.getValue()-1);
                pvpResourceTimer.put(entry.getKey(),entry.getValue()-1);
                savePvpResourcePlayer(entry.getKey(),entry.getValue());
            }
        }
    }

    private void savePvpResourcePlayer(PlayerClass player,Integer time) {
        List<String> players = main.getConfig().getStringList("PvpModeResourceTimer.Players");
        for (int i=0;i<pvpResourceTimer.size();i++) {
            if(players.size()>0) {
                if(players.get(i).contains(player.getUUID().toString())) {
                    players.set(i,player.getUUID().toString()+"_"+time);
                } else {
                    String save = player.getUUID().toString()+"_"+time;
                    players.add(save);
                }
            } else {
                String save = player.getUUID().toString()+"_"+time;
                players.add(save);
            }
        }
        main.getConfig().set("PvpModeResourceTimer.Players", players);
        main.saveConfig();
    }

    private void removePvpResourcePlayer(UUID id) {
        List<String> players = main.getConfig().getStringList("PvpModeResourceTimer.Players");
        for (int i=0;i<pvpResourceTimer.size();i++) {
            if(players.size()>0) {
                if(players.get(i).contains(id.toString())) {
                    pvpResourceTimer.remove(getPlayer(id));
                    if(getPlayer(id).isAdminCanToggle()) {
                        getPlayer(id).setNoToggleReason("");
                    }
                    players.remove(i);
                }
            }
        }
        main.getConfig().set("PvpModeResourceTimer.Players", players);
        main.saveConfig();
    }

    public void loadPvpResourcePlayer(UUID id) {
        List<String> players = main.getConfig().getStringList("PvpModeResourceTimer.Players");
        for (String s : players) {
            String[] parts = s.split("_");
            UUID id2 = UUID.fromString(parts[0]);
            if(id.equals(id2)) {
                Integer time = Integer.valueOf(parts[1]);
                if(!pvpResourceTimer.containsKey(getPlayer(id))&&Bukkit.getServer().getPlayer(id)!=null) {
                    int x = (int)Bukkit.getPlayer(id).getLocation().getX();
                    int y = (int)Bukkit.getPlayer(id).getLocation().getY();
                    int z = (int)Bukkit.getPlayer(id).getLocation().getZ();
                    Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"PVP"+ChatColor.GRAY+"]"+ChatColor.WHITE+" "+Bukkit.getPlayer(id).getName()+" has come online in pvp mode at: "+ChatColor.GRAY+"X: "+ChatColor.RED+x+" "+ChatColor.GRAY+"Y: "+ChatColor.RED+y+" "+ChatColor.GRAY+"Z: "+ChatColor.RED+z+ChatColor.WHITE+"!");
                    pvpResourceTimer.put(getPlayer(id),time);
                    getPlayer(id).setPvp("on");
                    getPlayer(id).allowToggle(false);
                    if(getPlayer(id).isAdminCanToggle()) {
                        getPlayer(id).setNoToggleReason("You mined resources recently!");
                    }
                    getPlayer(id).setNoToggleTime(time);
                }
            }
        }
    }

    public void pausePvpResourcePlayer(UUID id) {
        List<String> players = main.getConfig().getStringList("PvpModeResourceTimer.Players");
        for (String s : players) {
            String[] parts = s.split("_");
            UUID id2 = UUID.fromString(parts[0]);
            if(id.equals(id2)) {
                pvpResourceTimer.remove(getPlayer(id));
            }
        }
    }



    private void chatAnnouncer(String s, Player p) {
        p.sendMessage(ChatColor.GRAY+"["+ChatColor.RED+"PVP"+ChatColor.GRAY+"]"+ChatColor.WHITE+" "+s);
    }


}

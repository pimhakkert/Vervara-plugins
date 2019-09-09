package redshadus.vervarapvp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

public class PlayerClass {
    private UUID playerUUID;
    private double modifier = 1.0;
    private long pvpTime = 0;
    private boolean inAttack = false;
    private boolean pvpMode = false;
    private boolean hgwPvp = false;
    private boolean canToggle = true;
    private boolean adminCanToggle = true;
    private String noToggleReason = null;
    private int noToggleTime = 1;
    private int taskID;
    private Main main;

    public PlayerClass(Main main, UUID UUID) {
        this.main = main;
        playerUUID = UUID;
    }

    public UUID getUUID(){
        return playerUUID;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    public double getModifier(){
        return modifier;
    }

    public void startPvp(){
        if(!inAttack) {
            pvpTime = System.currentTimeMillis();
            inAttack = true;
            startTime();
        }
    }

    public void stopPvp(){
        if(inAttack) {
            pvpTime = 0;
            inAttack = false;
            stopTime();
        }
    }

    public boolean isInAttack(){
        return inAttack;
    }

    //sets holy grail war pvp
    public void setHgwPvp(boolean pvp) {
        hgwPvp = pvp;
    }

    //Toggles pvp mode on/off
    public String pvpModeToggle() {
        if(canToggle&&adminCanToggle) {
            if(!hgwPvp) {
                if(!pvpMode) {
                    pvpMode = true;
                    setModifier(main.getConfig().getDouble("modifier"));
                    int x = (int)Bukkit.getPlayer(getUUID()).getLocation().getX();
                    int y = (int)Bukkit.getPlayer(getUUID()).getLocation().getY();
                    int z = (int)Bukkit.getPlayer(getUUID()).getLocation().getZ();
                    Bukkit.broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"PVP"+ChatColor.GRAY+"] "+ChatColor.WHITE+Bukkit.getPlayer(getUUID()).getName()+" has turned on their PVP at "+ChatColor.GRAY+"X: "+ChatColor.RED+x+" "+ChatColor.GRAY+"Y: "+ChatColor.RED+y+" "+ChatColor.GRAY+"Z: "+ChatColor.RED+z+ChatColor.WHITE+"!");
                    return "Toggled PVP on!";
                } else {
                    pvpMode = false;
                    setModifier(1.0);
                    return "Toggled PVP off!";
                }
            } else {
                return "Can't toggle PVP in a Holy Grail War!";
            }
        } else {
            return "You are not allowed to toggle PVP!\nReason:"+ChatColor.GRAY+" "+noToggleReason+ChatColor.WHITE+"\nSee /pvp time for remaining time!";
        }
    }

    //Checks if player in pvp mode
    public boolean isPvpMode() {
        return pvpMode;
    }

    //Allows/disallows player to toggle pvp
    public void allowToggle(boolean toggle) {
        if(toggle) {
            if(!canToggle) {
                canToggle = true;
            }
        } else {
            if(canToggle) {
                canToggle = false;
            }
        }

    }

    public void allowAdminToggle(boolean toggle) {
        adminCanToggle = toggle;
    }

    public void setPvp(String s) {
        if(s.equals("on")) {
            pvpMode = true;
            setModifier(main.getConfig().getDouble("modifier"));
        } else if(s.equals("off")) {
            pvpMode = false;
            setModifier(1.0);
        } else {
            System.out.println("Can't set pvp for player: "+playerUUID);
        }
    }

    public long getTime(){
        return pvpTime;
    }

    public void startTime() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                int time = 20-((int)System.currentTimeMillis()/1000-(int)pvpTime/1000);
                if(time>9) {
                    Bukkit.getPlayer(playerUUID).getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GRAY+"Combat timer: "+ChatColor.RED+time).create());
                } else if(time>4) {
                    Bukkit.getPlayer(playerUUID).getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GRAY+"Combat timer: "+ChatColor.YELLOW+time).create());
                } else if(time>0) {
                    Bukkit.getPlayer(playerUUID).getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GRAY+"Combat timer: "+ChatColor.GREEN+time).create());
                } else if(time<0) {
                    stopPvp();
                }
            }
        }, 0L, 20L);
    }

    public void stopTime() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public boolean isAdminCanToggle() {
        return adminCanToggle;
    }

    public void setNoToggleReason(String reason) {
        noToggleReason = reason;
    }

    public void setNoToggleTime(Integer time) {
        noToggleTime = time;
    }
}

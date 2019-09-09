package redshadus.vervarapvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandClass implements CommandExecutor {
    private PvpManager pvpMan;
    private Main main;

    public CommandClass(Main main){
        this.main = main;
        pvpMan = main.pvpMan;
    }
    //Add player to list
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(sender instanceof Player) {
            player = (Player) sender;
            if(command.getName().equals("pvp")) {
                if(args.length == 1) {
                    if(args[0].equals("toggle")) {
                        togglePvp(player);
                        return true;
                    } else if(args[0].equals("help")) {
                        chatAnnouncer("Pvp has the following commands:\n/pvp toggle: "+ChatColor.GRAY+"Turn on or off pvp mode (2x item drops)\n"+ChatColor.WHITE+"/pvp location PLAYER: "+ChatColor.GRAY+"See the coordinates of a player in PVP\n"+ChatColor.WHITE+"/pvp timer: "+ChatColor.GRAY+"Shows you how many seconds you have left on the pvp timer",player);
                        return true;
                    } else if(args[0].equals("timer")) {
                        if(pvpMan.pvpResourceTimer.containsKey(pvpMan.getPlayer(player.getUniqueId()))) {
                            chatAnnouncer("Forced pvp time left in minutes: "+(pvpMan.pvpResourceTimer.get(pvpMan.getPlayer(player.getUniqueId()))+1),player);
                            return true;
                        } else {
                            chatAnnouncer("No resources mined recently!",player);
                            return true;
                        }
                    } else {
                        chatAnnouncer("Type /pvp help to see all available commands",player);
                        return true;
                    }
                } else if(args.length == 2) {
                    if(args[0].equals("location")) {
                        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if(onlinePlayer.getName().equals(args[1])) {
                                chatAnnouncer(getPlayerLocation(onlinePlayer),player);
                                return true;
                            }
                        }
                        chatAnnouncer("Player not found! Make sure they are online.",player);
                        return true;
                    }
                } else {
                    chatAnnouncer("Type /pvp help to see all available commands",player);
                    return true;
                }

            } else if(command.getName().equals("pvpadmin")) {
                if(player.hasPermission("vervarapvp.admin")) {
                    if(args.length != 0) {
                        if(args.length==2&&args[0].equals("toggle")) {
                            if(args[1].equals("all")) {
                                chatAnnouncer("Toggled all players pvp mode ON.",player);
                                toggleAllPvp();
                                return true;
                            } else {
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    if(onlinePlayer.getName().equals(args[1])) {
                                        if(togglePvp(onlinePlayer)) {
                                            chatAnnouncer("Toggled "+args[1]+"'s pvp mode ON.",player);
                                        } else {
                                            chatAnnouncer("Toggled "+args[1]+"'s pvp mode OFF.",player);
                                        }
                                        return true;
                                    }
                                }
                                chatAnnouncer("Player not found! Make sure they are online.",player);
                                return true;
                            }
                        } else if(args[0].equals("setpvp")) {
                            if(args.length==3) {
                                if(args[1].equals("all")) {
                                    if(args[2].equals("on")) {
                                        setAllPvp("on");
                                        chatAnnouncer("Set ALL players pvp ON",player);
                                        return true;
                                    } else if(args[2].equals("off")) {
                                        chatAnnouncer("Set ALL players pvp OFF",player);
                                        setAllPvp("off");
                                        return true;
                                    } else {
                                        chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                                        return true;
                                    }
                                } else {
                                    for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        if(onlinePlayer.getName().equals(args[1])) {
                                            if(args[2].equals("on")) {
                                                pvpMan.getPlayer(onlinePlayer.getUniqueId()).setPvp("on");
                                                chatAnnouncer("Set "+onlinePlayer.getDisplayName()+"'s pvp mode ON",player);
                                                chatAnnouncer("Your pvp mode has been turned ON!",onlinePlayer);
                                                return true;
                                            } else if(args[2].equals("off")) {
                                                pvpMan.getPlayer(onlinePlayer.getUniqueId()).setPvp("off");
                                                chatAnnouncer("Set "+onlinePlayer.getDisplayName()+"'s pvp mode OFF",player);
                                                chatAnnouncer("Your pvp mode has been turned OFF!",onlinePlayer);
                                                return true;
                                            } else {
                                                chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                                                return true;
                                            }
                                        }
                                    }
                                    chatAnnouncer("Player not found! Make sure they are online.",player);
                                    return true;
                                }
                            } else {
                                chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                                return true;
                            }
                        } else if(args[0].equals("togglemode")){
                            if(args.length == 3) {
                                if(args[1].equals("all")) {
                                    if(args[2].equals("on")) {
                                        togglemodeAll("on");
                                        chatAnnouncer("Turned ON toggle mode for all players",player);
                                        return true;
                                    } else if(args[2].equals("off")) {
                                        togglemodeAll("off");
                                        chatAnnouncer("Turned OFF toggle mode for all players",player);
                                        return true;
                                    } else {
                                        chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                                        return true;
                                    }
                                } else {
                                    for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                        if(onlinePlayer.getName().equals(args[1])) {
                                            if(args[2].equals("on")) {
                                                pvpMan.getPlayer(onlinePlayer.getUniqueId()).allowAdminToggle(true);
                                                chatAnnouncer("Turned ON toggle mode for "+onlinePlayer.getName(),player);
                                                chatAnnouncer("Your pvp toggling has been turned ON!",onlinePlayer);
                                                pvpMan.getPlayer(onlinePlayer.getUniqueId()).setNoToggleReason("");
                                                return true;
                                            } else if(args[2].equals("off")) {
                                                pvpMan.getPlayer(onlinePlayer.getUniqueId()).allowAdminToggle(false);
                                                chatAnnouncer("Turned OFF toggle mode for "+onlinePlayer.getName(),player);
                                                chatAnnouncer("Your pvp toggling has been turned OFF!",onlinePlayer);
                                                pvpMan.getPlayer(onlinePlayer.getUniqueId()).setNoToggleReason("An administrator has disabled your ability to toggle pvp.");
                                                return true;
                                            } else {
                                                chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                                                return true;
                                            }
                                        }
                                    }
                                    chatAnnouncer("Player not found! Make sure they are online.",player);
                                    return true;
                                }
                            } else {
                                chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                                return true;
                            }
                        } else if(args[0].equals("blacklist")) {
                            addBlacklistItem(player.getInventory().getItemInMainHand());
                            chatAnnouncer("Item in hand added to blacklist!",player);
                            return true;
                        } else if(args[0].equals("help")) {
                            chatAnnouncer("Pvpadmin has the following commands:\n/pvpadmin toggle player/all: "+ChatColor.GRAY+"toggle pvp mode for a player or all players\n"+ChatColor.WHITE+"/pvpadmin setpvp player/all on/off: "+ChatColor.GRAY+"Set certain or all players pvp mode to on or off\n"+ChatColor.WHITE+"/pvpadmin togglemode player/all on/off: "+ChatColor.GRAY+"Enables or disables pvp toggling for certain or all players\n"+ChatColor.WHITE+"/pvpadmin blacklist: "+ChatColor.GRAY+"The item in your hand will be blacklisted from extra pvp drops",player);
                            return true;
                        } else {
                            chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                            return true;
                        }
                    } else {
                        chatAnnouncer("Type /pvpadmin help to see all available commands",player);
                        return true;
                    }
                } else {
                    chatAnnouncer("You do not have permission to do admin commands",player);
                    return true;
                }
            }
        } else if(sender instanceof ConsoleCommandSender) {
            if(args[0].equals("updatehgw")) {
                pvpMan.updateHGWPlayers();
                return true;
            } else if(args[0].equals("stophgw")) {
                pvpMan.stopHGW();
                return true;
            }
        }
        return false;
    }

    private void addBlacklistItem(ItemStack item) {
        List<String> items = main.getConfig().getStringList("Item-blacklist.Items");
        if(item.getItemMeta().getDisplayName().equals("")) {
            items.add("MinecraftItem_"+item.getType().toString());
        } else {
            items.add(item.getItemMeta().getDisplayName());
        }
        main.getConfig().set("Item-blacklist.Items", items);
        main.saveConfig();
    }

    private boolean togglePvp(Player p) {
        PlayerClass victimPlayer = pvpMan.getPlayer(p.getUniqueId());
        if(victimPlayer.isInAttack()) {
            if(System.currentTimeMillis()-victimPlayer.getTime()>20000) {
                victimPlayer.stopPvp();
                String s = victimPlayer.pvpModeToggle();
                chatAnnouncer(s,p);
                return s.contains("on");
            } else {
                chatAnnouncer("You can't toggle pvp while in combat mode!",p);
                return false;
            }
        } else {
            String s = victimPlayer.pvpModeToggle();
            chatAnnouncer(s,p);
            return s.contains("on");
        }
    }

    private void togglemodeAll(String s) {
        if(s.equals("on")) {
            for(Player p:main.getServer().getOnlinePlayers()){
                pvpMan.getPlayer(p.getUniqueId()).allowAdminToggle(true);
                chatAnnouncer("Your pvp mode toggling has been turned ON!",p);
                pvpMan.getPlayer(p.getUniqueId()).setNoToggleReason("");
            }
        } else if(s.equals("off")) {
            for(Player p:main.getServer().getOnlinePlayers()){
                pvpMan.getPlayer(p.getUniqueId()).allowAdminToggle(false);
                pvpMan.getPlayer(p.getUniqueId()).setNoToggleReason("An administrator has disabled your ability to toggle pvp.");
                chatAnnouncer("Your pvp mode toggling has been turned OFF!",p);
            }
        }
    }

    private void setAllPvp(String s) {
        if(s.equals("on")) {
            for(Player p:main.getServer().getOnlinePlayers()){
                pvpMan.getPlayer(p.getUniqueId()).setPvp("on");
                chatAnnouncer("Your pvp mode was set to ON!",p);
            }
        } else if(s.equals("off")) {
            for(Player p:main.getServer().getOnlinePlayers()){
                pvpMan.getPlayer(p.getUniqueId()).setPvp("off");
                chatAnnouncer("Your pvp mode was set to OFF!",p);
            }
        }
    }

    private String getPlayerLocation(Player p) {
        if(pvpMan.getPlayer(p.getUniqueId()).isPvpMode()) {
            if(!p.hasPermission("vervarapvp.exemptfromlocation")) {
                int X = (int)p.getLocation().getX();
                int Y = (int)p.getLocation().getY();
                int Z = (int)p.getLocation().getX();
                return "Location of "+p.getDisplayName()+": X:"+X+" Y:"+Y+" Z:"+Z;
            } else {
                return "This player can't be located!";
            }
        } else {
            return "This player is not in pvp mode!";
        }
    }

    private void toggleAllPvp() {
        for(Player p:main.getServer().getOnlinePlayers()){
            String s = pvpMan.getPlayer(p.getUniqueId()).pvpModeToggle();
            if(s.contains("on")) {
                chatAnnouncer("Your pvp mode was toggled ON!",p);
            } else if(s.contains("off")) {
                chatAnnouncer("Your pvp mode was toggled OFF",p);
            }
        }
    }

    private void chatAnnouncer(String s, Player p) {
        p.sendMessage(ChatColor.GRAY+"["+ChatColor.RED+"PVP"+ChatColor.GRAY+"]"+ChatColor.WHITE+" "+s);
    }
}

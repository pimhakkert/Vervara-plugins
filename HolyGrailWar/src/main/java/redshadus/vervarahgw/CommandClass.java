package redshadus.vervarahgw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.UUID;

public class CommandClass implements CommandExecutor {
    private Main main;

    public CommandClass(Main main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(command.getName().equals("hgw")) {
                if(args.length > 0) {
                    if(args[0].equals("stats")) {
                        main.hgwClass.showStats(player);
                        return true;
                    } else if(args[0].equals("help")) {
                        chatAnnouncer("HGW has the following commands:\n/hgw stats: "+ChatColor.GRAY+"View statistics on current HGW teams",player);
                        return true;
                    } else {
                        chatAnnouncer("Type /hgw help to see all available commands",player);
                        return true;
                    }
                } else {
                    chatAnnouncer("Type /hgw help to see all available commands",player);
                    return true;
                }
            } else if(command.getName().equals("hgwadmin")){
                if(player.hasPermission("vervarahgw.admin")) {
                    if(args.length == 1) {
                        if(args[0].equals("start")) {
                            if(!main.hgwClass.isWar()) {
                                main.hgwClass.startHGW();
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                String comman = "pvpadmin updatehgw";
                                Bukkit.dispatchCommand(console, comman);
                                return true;
                            } else {
                                chatAnnouncer("The Holy Grail War has already started!",player);
                                return true;
                            }
                        } else if(args[0].equals("end")){
                            if(main.hgwClass.isWar()) {
                                main.hgwClass.endHGW();
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                String comman = "pvpadmin stophgw";
                                Bukkit.dispatchCommand(console, comman);
                                return true;
                            } else {
                                chatAnnouncer("The Holy Grail War isn't in ongoing at this moment!",player);
                                return true;
                            }
                        } else if(args[0].equals("pause")){
                            if(!main.hgwClass.isPaused()) {
                                main.hgwClass.pauseHGW();
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                String comman = "pvpadmin stophgw";
                                Bukkit.dispatchCommand(console, comman);
                                return true;
                            } else {
                                chatAnnouncer("The Holy Grail War is already paused!",player);
                                return true;
                            }
                        } else if(args[0].equals("unpause")) {
                            if(main.hgwClass.isPaused()) {
                                main.hgwClass.unpauseHGW();
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                String comman = "pvpadmin updatehgw";
                                Bukkit.dispatchCommand(console, comman);
                                return true;
                            } else {
                                chatAnnouncer("The Holy Grail War is not paused!",player);
                                return true;
                            }
                        } else if(args[0].equals("help")) {
                            chatAnnouncer("HGW has the following commands:\n/hgw stats: "+ChatColor.GRAY+"View statistics on current HGW teams\n"+ChatColor.WHITE+"/hgwadmin add teamName player1 player2: "+ChatColor.GRAY+"Adds a team to HGW\n"+ChatColor.WHITE+"/hgwadmin start: "+ChatColor.GRAY+"Starts a HGW\n"+ChatColor.WHITE+"/hgwadmin end: "+ChatColor.GRAY+"Ends a HGW\n"+ChatColor.WHITE+"/hgwadmin pause: "+ChatColor.GRAY+"Pauses a HGW\n"+ChatColor.WHITE+"/hgwadmin unpause: "+ChatColor.GRAY+"Unpauses a HGW\n",player);
                            return true;
                        } else {
                            chatAnnouncer("Type /hgwadmin help to see all available commands",player);
                            return true;
                        }
                    } else if(args.length == 3) {
                        if(args[0].equals("disqualify")) {
                            if(args[1].equals("player")) {
                                Player player1 = Bukkit.getPlayerExact(args[2]);
                                if(player1==null) {
                                    chatAnnouncer("Player not found!",player);
                                    return true;
                                } else {
                                    UUID id = player1.getUniqueId();
                                    HGPlayerClass team = main.hgwClass.getTeamFromPlayer(id);
                                    if(team!=null) {
                                        if(team.getServantID()==id) {
                                            team.setServantDisqualified();
                                            return true;
                                        } else if(team.getMageID()==id) {
                                            team.setDisqualified();
                                            return true;
                                        } else {
                                            chatAnnouncer("System error: disqualify player",player);
                                            return true;
                                        }
                                    } else {
                                        chatAnnouncer("Player not found!",player);
                                        return true;
                                    }
                                }

                            } else if(args[1].equals("team")) {
                                HGPlayerClass team = main.hgwClass.getTeamFromName(args[2]);
                                if(team!=null) {
                                    team.setDisqualified();
                                    return true;
                                } else {
                                    chatAnnouncer("Team not found!",player);
                                    return true;
                                }
                            }
                        }
                    } else if(args.length==4) {
                        if(args[0].equals("add")) {
                            Player player1 = Bukkit.getPlayerExact(args[2]);
                            Player player2 = Bukkit.getPlayerExact(args[3]);
                            if(player1==null||player2==null) {
                                chatAnnouncer("One or both of these players are not online!",player);
                                return true;
                            } else {
                                main.hgwClass.addTeam(args[1],player1,player2);
                                return true;
                            }
                        } else {
                            chatAnnouncer("Type /hgwadmin help to see all available commands",player);
                            return true;
                        }
                    } else {
                        chatAnnouncer("Type /hgwadmin help to see all available commands",player);
                        return true;
                    }
                } else {
                    chatAnnouncer("You do not have permission to do admin commands",player);
                    return true;
                }
            }
        }
        return false;
    }

    private void chatAnnouncer(String s, Player p) {
        p.sendMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.WHITE+" "+s);
    }
}

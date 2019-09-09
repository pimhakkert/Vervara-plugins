package redshadus.vervarahgw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class HolyGrailWar {
    private Main main;
    private CSVController csvController;
    private List<HGPlayerClass> list = new ArrayList<>();
    private boolean isWar;
    private boolean isPaused;

    public HolyGrailWar(Main main, CSVController csvController) {
        this.main = main;
        this.csvController = csvController;
        isWar = main.getConfig().getBoolean("isWar");
        isPaused = main.getConfig().getBoolean("isPaused");
    }

    public List<HGPlayerClass> getList(){
        return list;
    }

    public void setList(List<HGPlayerClass> list) {
        this.list = list;
    }

    public void addTeam(String teamName, Player mage, Player servant) {
        HGPlayerClass temp = new HGPlayerClass(mage.getUniqueId(),servant.getUniqueId(),teamName,0,false,false);
        list.add(temp);
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.WHITE+" Team "+ChatColor.AQUA+teamName+ChatColor.WHITE+" has just signed up for the Holy Grail War with the following players: \n"+"\nMage: "+ChatColor.AQUA+mage.getName()+ChatColor.WHITE+"\nServant: "+ChatColor.AQUA+servant.getName());
        csvController = new CSVController(main.getDataFolder().toString() + File.separator+ "TeamList");
    }

    public String deleteTeam(String teamName) {
        HGPlayerClass team = getTeamFromName(teamName);
        if(team!=null) {
            list.remove(team);
            return "Team removed!";
        }
        return "Team not found!";
    }

    public void startHGW() {
        isWar = true;
        main.getConfig().set("isWar",true);
        main.saveConfig();
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.AQUA+" The Holy Grail War has started!");
    }

    public void endHGW() {
        isWar = false;
        main.getConfig().set("isWar",false);
        main.saveConfig();
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.WHITE+" The Holy Grail War has ended!");
    }

    public void pauseHGW() {
        isPaused = true;
        main.getConfig().set("isPaused",true);
        main.saveConfig();
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.RED+" The Holy Grail War has been paused!");

    }

    public void unpauseHGW() {
        isPaused = false;
        main.getConfig().set("isPaused",false);
        main.saveConfig();
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.GREEN+" The Holy Grail War has been unpaused!");
    }

    public boolean isWar() {
        return isWar;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public String disqualifyPlayer(String teamName, UUID player) {
        HGPlayerClass team = getTeamFromName(teamName);
        if(team.getServantID()==player) {
            team.setServantDisqualified();
        } else if(team.getMageID()==player) {
            team.setDisqualified();
        } else {
            return "Player not found!";
        }
        return "CANT DISQUALIFY";
    }

    public String disqualifyTeam(String teamName) {
        HGPlayerClass team = getTeamFromName(teamName);
        if(team!=null) {
            team.setDisqualified();
        } else {
            return "Team not found!";
        }
        return "CANT DISQUALIFY TEAM";
    }

    public void showStats(Player player) {
        String start = ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.WHITE+" Teams:\n";
        List<String> messageContent = new ArrayList<>();
        for (HGPlayerClass team:list) {
            String teamInfo = ChatColor.GRAY+"\nTeam: "+ChatColor.WHITE+team.getTeamName()+"\n";
            String mageStatus;
            String servantStatus;
            if(team.isDisqualified()) {
                mageStatus = ChatColor.GRAY+"Mage: "+ChatColor.WHITE+Bukkit.getOfflinePlayer(team.getMageID()).getName()+ChatColor.GRAY+" | "+ChatColor.RED+"DISQUALIFIED\n";
                servantStatus = ChatColor.GRAY+"Servant: "+ChatColor.WHITE+Bukkit.getOfflinePlayer(team.getServantID()).getName()+ChatColor.GRAY+" | "+ChatColor.RED+"DISQUALIFIED\n";
            } else if(team.isServantDisqualified()) {
                mageStatus = ChatColor.GRAY+"Mage: "+ChatColor.WHITE+Bukkit.getOfflinePlayer(team.getMageID()).getName()+ChatColor.GRAY+" | "+ChatColor.GREEN+"PARTICIPATING\n";
                servantStatus = ChatColor.GRAY+"Servant: "+ChatColor.WHITE+Bukkit.getOfflinePlayer(team.getServantID()).getName()+ChatColor.GRAY+" | "+ChatColor.RED+"DISQUALIFIED\n";
            } else {
                mageStatus = ChatColor.GRAY+"Mage: "+ChatColor.WHITE+Bukkit.getOfflinePlayer(team.getMageID()).getName()+ChatColor.GRAY+" | "+ChatColor.GREEN+"PARTICIPATING\n";
                servantStatus = ChatColor.GRAY+"Servant: "+ChatColor.WHITE+Bukkit.getOfflinePlayer(team.getServantID()).getName()+ChatColor.GRAY+" | "+ChatColor.GREEN+"PARTICIPATING\n";
            }
            messageContent.add(teamInfo+mageStatus+servantStatus);
        }
        messageContent.add(0,start);
        String[] finalMessage = new String[messageContent.size()];
        finalMessage = messageContent.toArray(finalMessage);
        player.sendMessage(finalMessage);
    }

    public HGPlayerClass getTeamFromPlayer(UUID playerID) {
        for (HGPlayerClass team:list) {
            if(team.getMageID().equals(playerID)||team.getServantID().equals(playerID)) {
                return team;
            }
        }
        return null;
    }

    public HGPlayerClass getTeamFromName(String teamName) {
        for (HGPlayerClass team:list) {
            if(team.getTeamName().equals(teamName)) {
                return team;
            }
        }
        return null;
    }



}

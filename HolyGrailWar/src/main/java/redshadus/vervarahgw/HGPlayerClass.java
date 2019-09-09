package redshadus.vervarahgw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HGPlayerClass
{
    private UUID Mage;
    private UUID Servant;
    private String teamName;
    private boolean isDisqualified;
    private boolean isServantDisqualified;
    private Integer points;

    public HGPlayerClass(UUID mage, UUID servant, String teamName, Integer points, boolean isDisqualified, boolean isServantDisqualified) {
        Mage = mage;
        Servant = servant;
        this.teamName = teamName;
        this.points = points;
        this.isDisqualified = isDisqualified;
        this.isServantDisqualified = isServantDisqualified;
    }

    public String getMage()
    {
        return Mage.toString();
    }

    public UUID getMageID() {
        return Mage;
    }

    public String getServant()
    {
        return Servant.toString();
    }

    public UUID getServantID() {
        return Servant;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setDisqualified() {
        isDisqualified = true;
        isServantDisqualified = true;
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.WHITE+" Team "+ChatColor.AQUA+getTeamName()+ChatColor.WHITE+" has been disqualified from the Holy Grail War!");
    }

    public boolean isDisqualified() {
        return isDisqualified;
    }

    public boolean isServantDisqualified() {
        return isServantDisqualified;
    }

    public void setServantDisqualified() {
        isServantDisqualified = true;
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY+"["+ChatColor.RED+"Holy Grail War"+ChatColor.GRAY+"]"+ChatColor.WHITE+" Team "+ChatColor.AQUA+getTeamName()+ChatColor.WHITE+" has has lost their servant!");
    }

    @Override
    public String toString()
    {
        return getMage() + "," + getServant() + "," + getTeamName() + "," + getPoints() + "," + isDisqualified() + "," + isServantDisqualified();
    }
}

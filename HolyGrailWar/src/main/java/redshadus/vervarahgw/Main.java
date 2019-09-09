package redshadus.vervarahgw;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private CSVController csvController;

    HolyGrailWar hgwClass;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EventClass(this), this);
        CommandClass cmdClass = new CommandClass(this);
        getCommand("hgw").setExecutor(cmdClass);
        getCommand("hgwadmin").setExecutor(cmdClass);
        csvController = new CSVController(this.getDataFolder().toString() +File.separator+ "TeamList");
        hgwClass = new HolyGrailWar(this,csvController);
        hgwClass.setList(csvController.convertFromCsv(this.getDataFolder().toString() +File.separator+ "TeamList.csv"));
    }

    @Override
    public void onDisable() {
        csvController.ConvertToCsv(hgwClass.getList());
    }

    public List<UUID> getHGWPlayers() {
        List<HGPlayerClass> list = new ArrayList<>();
        if(this.getConfig().getBoolean("isWar")&&!this.getConfig().getBoolean("isPause")) {
            list = hgwClass.getList();
        }
        List<UUID> returnList = new ArrayList<>();
        for (HGPlayerClass team:list) {
            if(!team.isDisqualified()&&!team.isServantDisqualified()) {
                UUID p1 = team.getMageID();
                returnList.add(p1);
                UUID p2 = team.getServantID();
                returnList.add(p2);
            }
            else if(!team.isDisqualified()) {
                UUID p1 = team.getMageID();
                returnList.add(p1);
            }
        }
        return returnList;
    }
}

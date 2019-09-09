package redshadus.vervarapvp;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    public PvpManager pvpMan;
    public CommandClass cmdClass;
    private redshadus.vervarahgw.Main api = (redshadus.vervarahgw.Main) Bukkit.getServer().getPluginManager().getPlugin("VervaraHGW");

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        List<UUID> test = api.getHGWPlayers();
        pvpMan = new PvpManager(this,test);
        cmdClass = new CommandClass(this);
        getServer().getPluginManager().registerEvents(new EventClass(this), this);
        getCommand("pvp").setExecutor(cmdClass);
        getCommand("pvpadmin").setExecutor(cmdClass);
        if(Bukkit.getOnlinePlayers().size()>0) {
            loadPlayers();
        }
        pvpMan.startPvpResourceTimer();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        pvpMan.emptyList();
    }

    //Reload playerlist
    private void loadPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            pvpMan.addPlayer(player.getUniqueId());
        }
    }
}

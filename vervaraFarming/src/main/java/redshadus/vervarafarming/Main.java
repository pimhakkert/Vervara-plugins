package redshadus.vervarafarming;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    PumiceStoneClass pumiceStoneClass;
    IO ioClass;
    CommandClass cmdClass;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EventClass(this), this);
        pumiceStoneClass = new PumiceStoneClass(this);
        cmdClass = new CommandClass(this);
        pumiceStoneClass.recipe();
        ioClass = new IO(this);
        ioClass.loadPumiceStoneLocations();
        ioClass.loadAdminWaterLocations();
        ioClass.pumiceStoneTimer();
        getCommand("getpumicestone").setExecutor(cmdClass);
    }

    @Override
    public void onDisable() {
        ioClass.savePumiceStoneLocations(pumiceStoneClass.getPSLocations());
        ioClass.saveAdminWaterLocations(pumiceStoneClass.getAWLocations());
    }
}

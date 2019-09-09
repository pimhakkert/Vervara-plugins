package redshadus.vervarafarming;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandClass  implements CommandExecutor {
    private Main main;

    public CommandClass(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("getpumicestone")) {
                player.getInventory().addItem(main.pumiceStoneClass.getPumiceStoneAmount(64));
            }
        }
        return false;
    }
}

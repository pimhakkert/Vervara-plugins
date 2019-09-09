package redshadus.vervarapvp;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.war.eventwar.War;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import javax.swing.text.html.HTMLDocument;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class EventClass implements Listener {
    private Main main;
    private PvpManager pvpMan;


    public EventClass(Main main){
        this.main = main;
        pvpMan = main.pvpMan;
    }



    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        main.pvpMan.addPlayer(e.getPlayer().getUniqueId());
        pvpMan.loadPvpResourcePlayer(e.getPlayer().getUniqueId());
        pvpMan.updateHGWPlayers();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(main.pvpMan.getPlayer(e.getPlayer().getUniqueId()).isInAttack()&&System.currentTimeMillis()-main.pvpMan.getPlayer(e.getPlayer().getUniqueId()).getTime()<20000&&!e.getPlayer().hasPermission("vervarapvp.exemptfromdeath")) {
            e.getPlayer().setHealth(0.0);
            main.pvpMan.getPlayer(e.getPlayer().getUniqueId()).stopPvp();
        }
        main.pvpMan.pausePvpResourcePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDies(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(pvpMan.getPlayer(p.getUniqueId()).isInAttack()) {
            pvpMan.getPlayer(p.getUniqueId()).stopPvp();
//            if(e.getDeathMessage().contains("was slain by")) {
//                e.setDeathMessage(p.getName()+" quit while in combat time and died!");
//            }
        }
    }

    @EventHandler
    public void onMobkill(EntityDeathEvent e) {
        if(e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            if(pvpMan.getPlayer(killer.getUniqueId()).isPvpMode()&&killer.getGameMode()!=GameMode.CREATIVE) {
                if(!(e.getEntity() instanceof Player)&&!(e.getEntity() instanceof Wither)) {
                    List<ItemStack> dropList = e.getDrops();
                    List<String> items = main.getConfig().getStringList("Item-blacklist.Items");
                    for(int i=0;i<dropList.size();i++) {
                        if(!items.contains(dropList.get(i).getItemMeta().getDisplayName())&&!items.contains("Minecraftitem_"+dropList.get(i).getType().toString())) {
                            int totalAmount = (int) Math.rint(dropList.get(i).getAmount()*pvpMan.getPlayer(killer.getUniqueId()).getModifier());
                            dropList.get(i).setAmount(totalAmount);
                            e.getDrops().set(i,dropList.get(i));
                        }
                    }
                    dropList = null;
                    items = null;
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(pvpMan.getPlayer(player.getUniqueId()).isPvpMode()&&player.getGameMode()!=GameMode.CREATIVE) {
            List<String> blacklist = main.getConfig().getStringList("Item-blacklist.Items");
            Collection<ItemStack> dropList = e.getBlock().getDrops();
            HashMap<ItemStack,Float> dropCounts = new HashMap<>();

            for (Iterator iterator = dropList.iterator(); iterator.hasNext();) {
                ItemStack type = (ItemStack) iterator.next();
                if(!blacklist.contains(type.getItemMeta().getDisplayName())&&!blacklist.contains("Minecraftitem_"+type.getType().toString())) {
                    String extraMat = type.getType().toString();
                    ItemStack extra = new ItemStack(Material.getMaterial(extraMat));
                    if(dropCounts.containsKey(extra)) {
                        dropCounts.put(extra, (float) (type.getAmount()*pvpMan.getPlayer(player.getUniqueId()).getModifier() + dropCounts.get(extra)));
                    } else {
                        dropCounts.put(extra, (float) (type.getAmount()*pvpMan.getPlayer(player.getUniqueId()).getModifier()));
                    }
                    if(type.getType()==Material.COAL_ORE||type.getType()==Material.IRON_ORE||type.getType()==Material.GOLD_ORE||type.getType()==Material.DIAMOND_ORE||type.getType()==Material.EMERALD_ORE||type.getType()==Material.REDSTONE_ORE) {
                        pvpMan.pvpModeAddResourceTime(pvpMan.getPlayer(player.getUniqueId()),5);
                        pvpMan.blockHit();
                    }
                }
            }
            e.setDropItems(false);
            for (Map.Entry<ItemStack, Float> entry : dropCounts.entrySet()) {
                ItemStack item = entry.getKey();
                item.setAmount((int) Math.rint(entry.getValue()));
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item);
            }
            blacklist = null;
            dropCounts = null;
            dropList = null;
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if(pvpMan.getPlayer(e.getPlayer().getUniqueId()).isInAttack()) {
            if(!main.getConfig().getBoolean("combatTeleport")) {
                chatAnnouncer("You can't teleport while under attack!",e.getPlayer());
                e.setCancelled(true);
            }
        } else if(pvpMan.getPlayer(e.getPlayer().getUniqueId()).isPvpMode()) {
            if(!main.getConfig().getBoolean("pvpModeTeleport")) {
                chatAnnouncer("You can't teleport while in PVP mode!",e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        boolean cancelProjectile = false;
        if(e.getDamager().getType() == EntityType.SPLASH_POTION && e.getEntity() instanceof Player) {
            cancelProjectile = true;
        } else if(e.getDamager().getType() == EntityType.TRIDENT && e.getEntity() instanceof Player) {
            cancelProjectile = true;
        }
        if (cancelProjectile) {
            Player victim = (Player)e.getEntity();
            PlayerClass victimPlayer = pvpMan.getPlayer(victim.getUniqueId());
            if(!victimPlayer.isPvpMode()) {
                e.setCancelled(true);
                return;
            }
        }
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player attacker = (Player)e.getDamager();
            Player victim = (Player)e.getEntity();
            PlayerClass attackerPlayer = pvpMan.getPlayer(attacker.getUniqueId());
            PlayerClass victimPlayer = pvpMan.getPlayer(victim.getUniqueId());
            if(attackerPlayer.isPvpMode()) {
                if(victimPlayer.isInAttack()) {
                    if(System.currentTimeMillis()-victimPlayer.getTime()>20000) {
                        victimPlayer.stopPvp();
                        e.setCancelled(true);
                    } else {
                        attackerPlayer.stopPvp();
                        attackerPlayer.startPvp();
                        victimPlayer.stopPvp();
                        victimPlayer.startPvp();
                    }
                } else {
                    if(victimPlayer.isPvpMode()) {
                        attackerPlayer.startPvp();
                        victimPlayer.startPvp();
                    } else {
                        chatAnnouncer("This player is not in PVP mode!",attacker);
                        e.setCancelled(true);
                    }
                }
            } else {
                chatAnnouncer("You are not in PVP mode!",attacker);
                e.setCancelled(true);
            }
        }
    }

    private void chatAnnouncer(String s, Player p) {
        p.sendMessage(ChatColor.GRAY+"["+ChatColor.RED+"PVP"+ChatColor.GRAY+"]"+ChatColor.WHITE+" "+s);
    }

}

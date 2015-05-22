package me.jimfutsu.HypixelAbilities.Ability;

import me.jimfutsu.HypixelAbilities.Main;
import me.jimfutsu.HypixelAbilities.utl.CircleCreator;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class FlameCircle implements Listener {

    private Main plugin;

    public FlameCircle(Main plugin)
    {
        this.plugin = plugin;
    }

    public HashMap<Integer, String> FlameCircleUsage = new HashMap<Integer, String>();
    public ArrayList<String> FlameCircleUsers = new ArrayList<String>();
    public ArrayList<String> AlreadyDamaged = new ArrayList<String>();

    @EventHandler
    public void onClick(PlayerInteractEvent e)
    {
        final Player p = e.getPlayer();
        if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) &&
                p.getItemInHand().getType() == Material.GLOWSTONE_DUST &&
                p.getItemInHand().hasItemMeta() &&
                p.getItemInHand().getItemMeta().getLore().contains("Ring_Of_Fire")) {
            if (plugin.energy.get(p.getName()) >= 15) {
                if(!FlameCircleUsers.contains(p.getName())) {
                    Integer energybefore = plugin.energy.get(p.getName());
                    Integer energyafter = energybefore - 15;
                    plugin.updateEnergy(p.getName());
                    plugin.energy.remove(p.getName());
                    plugin.energy.put(p.getName(), energyafter);
                    final Location l = p.getLocation().add(0, 0.5, 0);
                    final World world = p.getWorld();
                    final double height = 0.1;
                    final int playflame = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            CircleCreator cc = new CircleCreator();
                            ArrayList<Location> locationtpe = cc.getCircle(l, 5, 100);
                            for(Location loc: locationtpe){
                                for(Player online : Bukkit.getOnlinePlayers()){
                                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles("flame", (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1));
                                }
                            }
                        }
                    }, 0, 2);
                    FlameCircleUsers.add(p.getName());
                    FlameCircleUsage.put(playflame, p.getName());
                    final int flamedamage = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            for(Player playerspe: Bukkit.getOnlinePlayers()){
                                if(playerspe.getLocation().distance(l) <= 5.0){
                                    if(playerspe.getDisplayName() != p.getDisplayName()){
                                        if(!AlreadyDamaged.contains(playerspe.getName())){
                                            final Player online = p;
                                            p.sendMessage(ChatColor.GREEN + "You hit " + ChatColor.AQUA + online.getName() + ChatColor.GREEN + " using Ring Of Fire for" + ChatColor.RED + " 50 Damage.");
                                            online.sendMessage(ChatColor.GREEN + "You were hit by " + ChatColor.AQUA + p.getName() + ChatColor.GREEN + " using Ring Of Fire for" + ChatColor.RED + " 50 Damage.");
                                            Integer phealth = plugin.health.get(online.getName());
                                            Integer newphealth = phealth - 50;
                                            plugin.health.remove(online.getName());
                                            plugin.health.put(online.getName(), newphealth);
                                            plugin.updateHealthBoard(online.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }, 0, 2);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                    {
                        public void run()
                        {
                            final String players = FlameCircleUsage.get(playflame);
                            FlameCircleUsers.remove(players);
                            FlameCircleUsage.remove(playflame);
                            Bukkit.getScheduler().cancelTask(playflame);
                            Bukkit.getScheduler().cancelTask(flamedamage);
                        }
                    }, 20*8L);
                }
                else{
                    p.sendMessage(ChatColor.YELLOW + "You already have a ring of fire!");
                }
            }
            else {
                p.sendMessage(ChatColor.YELLOW + "You do not have enough energy!");
            }
        }
    }

    public void addtoalreadydamaged(final String p){
        AlreadyDamaged.add(p);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                AlreadyDamaged.remove(p);
            }
        }, 20L);
    }
}

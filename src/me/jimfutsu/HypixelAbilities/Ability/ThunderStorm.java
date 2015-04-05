package me.jimfutsu.HypixelAbilities.Ability;

import me.jimfutsu.HypixelAbilities.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;

public class ThunderStorm implements Listener {

    private Main plugin;

    public ThunderStorm(Main plugin)
    {
        this.plugin = plugin;
    }

    boolean hasPlayer = false;

    @EventHandler
    @SuppressWarnings("unused")
    public void onClick (PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().getType() == Material.IRON_SWORD){
                if(p.getItemInHand().hasItemMeta()){
                    if (p.getItemInHand().getItemMeta().getLore().contains("ThunderStorm")) {
                        if(plugin.energy.get(p.getName()) >= 60){
                        //Strike 1
                        final Location lstrike = p.getLocation();
                        double x = p.getLocation().getX();
                        double z = p.getLocation().getZ();
                        int xint = p.getLocation().getBlockX();
                        int zint = p.getLocation().getBlockZ();
                        double y = p.getLocation().getWorld().getHighestBlockYAt(xint, zint);
                        World pworld = p.getWorld();
                        Location damageloc = new Location(pworld, x, y,z);
                            for (Player player : Bukkit.getOnlinePlayers()){
                                if (player != p && p.getWorld() == p.getWorld() && damageloc.distance(player.getLocation()) <= 6) {
                                    hasPlayer = true;
                                }
                            }
                            if(hasPlayer){
                                for (Player player : Bukkit.getOnlinePlayers()){
                                    StrikeThunderStorm(lstrike, 6, p.getWorld());
                                    if (player != p && p.getWorld() == p.getWorld() && damageloc.distance(player.getLocation()) <= 6) {
                                        if(!player.isDead()){
                                            Integer energybefore = plugin.energy.get(p.getName());
                                            Integer energyafter = energybefore - 60;
                                            plugin.energy.remove(p.getName());
                                            plugin.energy.put(p.getName(), energyafter);
                                            plugin.updateEnergy(p.getName());
                                            p.sendMessage(ChatColor.GREEN + "You hit " + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " using ThunderStorm for" + ChatColor.RED + " 175 Damage.");
                                            player.sendMessage(ChatColor.GREEN + "You were hit by " + ChatColor.AQUA + p.getName() + ChatColor.GREEN + " using ThunderStorm for" + ChatColor.RED + " 175 Damage.");
                                            Integer phealth = plugin.health.get(player.getName());
                                            Integer newphealth = phealth - 175;
                                            plugin. health.remove(player.getName());
                                            plugin.health.put(player.getName(), newphealth);
                                            plugin.updateHealthBoard(player.getName());
                                            hasPlayer = false;
                                        }
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                            public void run() {
                                                StrikeThunderStorm(lstrike, 6, p.getWorld());
                                            }
                                        }, 10L);
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                            public void run() {
                                                StrikeThunderStorm(lstrike, 6, p.getWorld());
                                            }
                                        }, 20L);
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                            public void run() {
                                                StrikeThunderStorm(lstrike, 6, p.getWorld());
                                            }
                                        }, 30L);
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                            public void run() {
                                                StrikeThunderStorm(lstrike, 6, p.getWorld());
                                            }
                                        }, 40L);
                                    }
                                }
                            }
                            else{
                                p.sendMessage(ChatColor.YELLOW + "No player in range");
                            }
                        }
                        else{
                            p.sendMessage(ChatColor.YELLOW + "You do not have enough energy!");
                        }
                    }
                }
            }
        }
    }

    //Methods
    public void StrikeThunderStorm(Location centerloc, Integer radius, World world){
        Location circlecenter = centerloc; //Center of circle
        Random rand = new Random();
        double angle = rand.nextDouble()*360; //Generate a random angle
        double x = circlecenter.getX() + (rand.nextDouble()*radius*Math.cos(Math.toRadians(angle))); // x
        double z = circlecenter.getZ() + (rand.nextDouble()*radius*Math.sin(Math.toRadians(angle))); // z
        double y = world.getHighestBlockYAt((int)x, (int)z);
        Location newloc = new Location(world, x, y, z);
        world.strikeLightningEffect(newloc);
    }
}

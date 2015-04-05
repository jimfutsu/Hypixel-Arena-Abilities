package me.jimfutsu.HypixelAbilities.Ability;

import java.util.ArrayList;
import me.jimfutsu.HypixelAbilities.Main;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Beam
        implements Listener
{
    private Main plugin;

    public Beam(Main plugin)
    {
        this.plugin = plugin;
    }

    public ArrayList<String> beamcheck = new ArrayList<String>();

    @EventHandler
    public void onClick(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) &&
                p.getItemInHand().getType() == Material.IRON_SWORD &&
                p.getItemInHand().hasItemMeta() &&
                p.getItemInHand().getItemMeta().getLore().contains("Beam")) {
            if(plugin.energy.get(p.getName()) >= 15){
                playBeam(p.getLocation(), p, 500);
                Integer energybefore = plugin.energy.get(p.getName());
                Integer energyafter = energybefore - 15;
                plugin.updateEnergy(p.getName());
                plugin.energy.remove(p.getName());
                plugin.energy.put(p.getName(), energyafter);
            }
            else{
                p.sendMessage(ChatColor.YELLOW + "You do not have enough energy!");
            }
        }
    }

    public void playBeam(Location loc, final Player p, int particles)
    {
        loc = loc.add(0.0D, 1.2D, 0.0D);
        for (double x = 0.0D; x < 100.0D; x += 0.1D)
        {
            Location end = p.getTargetBlock(null, 1000).getLocation();
            if (end == null) {
                return;
            }
            Vector line = end.toVector().subtract(loc.toVector());
            float length = (float)line.length();
            line.normalize();

            float ratio = length / particles;
            Vector v = line.multiply(ratio);

            Location counted = loc.clone().subtract(v);
            for (int i = 0; i < 2; i++)
            {
                loc.add(v);
                for (Player online : Bukkit.getOnlinePlayers())
                {
                    ((CraftPlayer)online).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles("reddust", (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.0F, 0.0F, 0.0F, 0.0F, 1));
                    if ((online.getLocation().getBlockX() == loc.getBlockX()) && (online.getLocation().getBlockZ() == loc.getBlockZ())) {
                        if (((online.getLocation().getBlockY() == loc.getBlockY()) || (online.getLocation().getBlockY() - 1 == loc.getBlockY()) || (online.getLocation().getBlockY() + 1 == loc.getBlockY())) &&
                                (online.getUniqueId() != p.getUniqueId())) {
                            if ((!online.isDead()) &&
                                    (!beamcheck.contains(p.getName())))
                            {
                                p.sendMessage(ChatColor.GREEN + "You hit " + ChatColor.AQUA + online.getName() + ChatColor.GREEN + " using Beam for" + ChatColor.RED + " 41 Damage.");
                                online.sendMessage(ChatColor.GREEN + "You were hit by " + ChatColor.AQUA + p.getName() + ChatColor.GREEN + " using Beam for" + ChatColor.RED + " 41 Damage.");
                                Integer phealth = plugin.health.get(online.getName());
                                Integer newphealth = phealth - 41;
                                plugin.health.remove(online.getName());
                                plugin.health.put(online.getName(), newphealth);
                                plugin.updateHealthBoard(online.getName());
                                beamcheck.add(p.getName());
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                                {
                                    public void run()
                                    {
                                        Beam.this.beamcheck.remove(p.getName());
                                    }
                                }, 2L);
                            }
                        }
                    }
                }
            }
        }
    }
}

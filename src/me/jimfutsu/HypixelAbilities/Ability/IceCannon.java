package me.jimfutsu.HypixelAbilities.Ability;

import me.jimfutsu.HypixelAbilities.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;


public class IceCannon implements Listener{

    HashMap<UUID, String> playertouuid = new HashMap<UUID, String>();;

    int task;
    int task2;

    private Main plugin;

    public IceCannon(Main plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().getType() == Material.IRON_SWORD){
                if(p.getItemInHand().hasItemMeta()){
                    if (p.getItemInHand().getItemMeta().getLore().contains("IceCannon")) {
                        if(plugin.energy.get(p.getName()) >= 80) {
                            FallingBlock fbice = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.ICE, (byte) 0);
                            fbice.setDropItem(false);
                            Vector vector = p.getEyeLocation().getDirection().multiply(1.75);
                            fbice.setVelocity(vector);
                            UUID blockuuid = fbice.getUniqueId();
                            playertouuid.put(blockuuid, p.getName());
                            Integer energybefore = plugin.energy.get(p.getName());
                            Integer energyafter = energybefore - 80;
                            plugin.energy.remove(p.getName());
                            plugin.energy.put(p.getName(), energyafter);
                            plugin.updateEnergy(p.getName());
                        }
                        else{
                            p.sendMessage(ChatColor.YELLOW + "You do not have enough energy!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent e){
        if(e.getTo() == Material.ICE){
            e.setCancelled(true);
            Block block = e.getBlock();
            Location blockloc = block.getLocation();
            block.getWorld().createExplosion(blockloc, 0);
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.getLocation().distance(blockloc) <= 4){
                    UUID id = e.getEntity().getUniqueId();
                    if(playertouuid.containsKey(id)){
                        Player p = Bukkit.getPlayer(playertouuid.get(id));
                        if(player != p){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 2));
                            p.sendMessage(ChatColor.GREEN + "You hit " + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " using Ice Cannon for" + ChatColor.RED + " 180 Damage.");
                            player.sendMessage(ChatColor.GREEN + "You were hit by " + ChatColor.AQUA + p.getName() + ChatColor.GREEN + " using Ice Cannon for" + ChatColor.RED + " 180 Damage.");
                            Integer phealth = plugin.health.get(player.getName());
                            Integer newphealth = phealth - 180;
                            plugin.health.remove(player.getName());
                            plugin.health.put(player.getName(), newphealth);
                            plugin.updateHealthBoard(player.getName());
                        }
                    }
                }
            }
        }
    }
}


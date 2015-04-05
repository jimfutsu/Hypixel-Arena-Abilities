package me.jimfutsu.HypixelAbilities;

import me.jimfutsu.HypixelAbilities.Ability.Beam;
import me.jimfutsu.HypixelAbilities.Ability.IceCannon;
import me.jimfutsu.HypixelAbilities.Ability.ThunderStorm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable(){
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(new Beam(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ThunderStorm(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new IceCannon(this), this);
    }

    @Override
    public void onDisable(){

    }

    public HashMap<String, Integer> health = new HashMap<String, Integer>();
    public HashMap<String, Integer> energy = new HashMap<String, Integer>();
    public ArrayList<String> scoreboardcontain = new ArrayList<String>();

    //tempCommand to give items
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("GiveLore")) {
                p.getInventory().addItem(setNameAndLore(Material.IRON_SWORD, 1, "Iron Sword", args[0]));
            }
            if (cmd.getName().equalsIgnoreCase("FullHealth")) {
                health.remove(p.getName());
                health.put(p.getName(), 2000);
                updateHealthBoard(p.getName());
            }
            if (cmd.getName().equalsIgnoreCase("ViewHealth")) {
                p.sendMessage(Integer.toString(health.get(p.getName())));
            }
            if (cmd.getName().equalsIgnoreCase("StartEXP")) {
                startEnergyCount();
            }
            if (cmd.getName().equalsIgnoreCase("ClearEXP")) {
                p.setLevel(0);
            }
        }
        return true;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin (PlayerJoinEvent e){
        Player p = e.getPlayer();
        //Health Setter
        if(!health.containsKey(p.getName())){
            health.put(p.getName(), 2000);
        }
        if(!scoreboardcontain.contains(p.getName())){
                updateHealthBoard(p.getName());
        }
    }

    //Player DMG
    @EventHandler
    @SuppressWarnings("unused")
    public void onHit (EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player || e.getDamager() instanceof  Player){
            e.setCancelled(true);
            Player p = (Player) e.getEntity();
            Player dp = (Player) e.getDamager();
            if(dp.getItemInHand().getType() == Material.IRON_SWORD){
                //Melee
                Integer phealth = health.get(p.getName());
                if(phealth >= 11){
                    p.damage(0.000001);
                    Integer newhealth = phealth - 10;
                    health.remove(p.getName());
                    health.put(p.getName(), newhealth);
                    updateHealthBoard(p.getName());
                }
                else {
                    Integer newhealthdead = 0;
                    health.remove(p.getName());
                    health.put(p.getName(), newhealthdead);
                    updateHealthBoard(p.getName());
                }
            }
        }
    }

    //Methods

    public void updateHealthBoard(String name){
        if(health.get(name) <= 0){
            Bukkit.getServer().getPlayer(name).setHealth(0);
        }
        else{
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            Objective objective = board.registerNewObjective("health", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.RED + "Arena Brawl Health");
            if(Bukkit.getOnlinePlayers().length <= 16){
                for(Player player : Bukkit.getOnlinePlayers()){
                    Score playerscore = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + player.getName().toString()));
                    playerscore.setScore(health.get(player.getName()));
                }
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.setScoreboard(board);
                }
            }
        }
    }

    public void startEnergyCount(){
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(energy.containsKey(p.getName())){
                        if(p.getLevel() < 100){
                            Integer newpenergy = energy.get(p.getName()) + 1;
                            energy.remove(p.getName());
                            energy.put(p.getName(), newpenergy);
                            p.setLevel(newpenergy);
                        }
                    }
                    else{
                        energy.put(p.getName(), 0);
                    }
                }
            }
        }, 0, 2);
    }

    public void updateEnergy(String name){
        Integer XP = energy.get(name);
        Player p = Bukkit.getServer().getPlayer(name);
        p.setLevel(XP);
    }

    public ItemStack setNameAndLore(Material material, int amount, String name, String... lore)
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        ArrayList<String> lorez = new ArrayList<String>();
        for(String mylores : lore)
        {
            lorez.add(ChatColor.translateAlternateColorCodes('&', mylores));
        }
        meta.setLore(lorez);
        item.setItemMeta(meta);
        return item;
    }

}

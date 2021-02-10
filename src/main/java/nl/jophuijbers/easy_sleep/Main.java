package nl.jophuijbers.easy_sleep;

import commands.Sleep;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private final FileConfiguration config = getConfig();
    private int percentage;
    private int fixed;

    @Override
    public void onEnable() {
        getLogger().info("Sleep plugin enabled");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("sleep").setExecutor(new Sleep(this));

        saveDefaultConfig();

        percentage = config.getInt("percentage");
        fixed = config.getInt("fixed");
    }

    @Override
    public void onDisable() {
        getLogger().info("Sleep plugin disabled");
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        int total = getOnlinePlayersWorld();
        int sleeping = getSleepingPlayers();

        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            sleeping++;
            if (fixed != 0) {
                Bukkit.broadcastMessage(player.getName() + ChatColor.YELLOW + " is sleeping (" + sleeping + "/" + fixed + ").");
                if (sleeping >= fixed) {
                    skipNight(player);
                }
            } else {
                double min = Math.round(Math.ceil((percentage / 100.0) * total));
                Bukkit.broadcastMessage(player.getName() + ChatColor.YELLOW + " is sleeping (" + sleeping + "/" + (int)min + ").");
                if (sleeping >= (int)min) {
                    skipNight(player);
                }
            }
        }
    }
    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        int total = getOnlinePlayersWorld();
        int sleeping = getSleepingPlayers();
        World world = event.getPlayer().getWorld();

        if (!isDay(world.getTime()) || world.hasStorm()) {
            if (fixed != 0) {
                Bukkit.broadcastMessage(player.getName() + ChatColor.RED + " left its bed (" + sleeping + "/" + fixed + ").");
            }else {
                double min = Math.round(Math.ceil((percentage / 100.0) * total));
                Bukkit.broadcastMessage(player.getName() + ChatColor.RED + " left its bed (" + sleeping + "/" + (int)min + ").");
            }
        }
    }

    public boolean isDay(long time) {
        return time < 12300 || time > 23850;
    }
    public void savePercentage(int percentage) {
        this.percentage = percentage;
        config.set("percentage", percentage);
        saveConfig();
    }
    public void saveFixed(int fixed) {
        this.fixed = fixed;
        config.set("fixed", fixed);
        saveConfig();
    }
    private int getOnlinePlayersWorld() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                count++;
            }
        }
        return count;
    }
    private int getSleepingPlayers() {
        int counter = 0;
        for(HumanEntity p : Bukkit.getOnlinePlayers()){
            if(p.isSleeping()) {
                counter++;
            }
        }
        return counter;
    }
    private void skipNight(Player player) {
        long time = player.getWorld().getFullTime();
        int days = (int)time / 24000;
        player.getLocation().getWorld().setTime(days*24000);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Night skipped.");
    }

    public int getPercentage() {
        return this.percentage;
    }
    public int getFixed() {
        return this.fixed;
    }
}

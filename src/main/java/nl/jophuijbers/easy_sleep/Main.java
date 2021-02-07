package nl.jophuijbers.easy_sleep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    private final FileConfiguration config = getConfig();
    private int percentage;
    private int fixed;

    @Override
    public void onEnable() {
        getLogger().info("Sleep plugin enabled");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("sleep").setExecutor(this);

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
        int total = Bukkit.getOnlinePlayers().size();
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("sleep")) {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "percentage":
                            try {
                                savePercentage(Integer.parseInt(args[1]));
                                player.sendMessage(ChatColor.YELLOW + "Percentage of players to sleep is now set to: " + percentage + "%");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                getLogger().info(e.getMessage());
                                player.sendMessage(ChatColor.YELLOW + "Percentage of players to sleep is: " + percentage + "%");
                            }
                            return true;
                        case "fixed":
                            try {
                                saveFixed(Integer.parseInt(args[1]));
                                player.sendMessage(ChatColor.YELLOW + "Minimum amount of players to sleep is now set to: " + fixed);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                getLogger().info(e.getMessage());
                                player.sendMessage(ChatColor.YELLOW + "Fixed amount of players to sleep is: " + fixed);
                            }
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private void savePercentage(int percentage) {
        this.percentage = percentage;
        config.set("percentage", percentage);
        saveConfig();
    }
    private void saveFixed(int fixed) {
        this.fixed = fixed;
        config.set("fixed", fixed);
        saveConfig();
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
}

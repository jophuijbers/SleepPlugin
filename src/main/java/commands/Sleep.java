package commands;

import nl.jophuijbers.easy_sleep.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sleep implements CommandExecutor {

    private final Main main;

    public Sleep(Main main) {
        this.main = main;
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
                                main.savePercentage(Integer.parseInt(args[1]));
                                player.sendMessage(ChatColor.YELLOW + "Percentage of players to sleep is now set to: " + main.getPercentage() + "%");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                main.getLogger().info(e.getMessage());
                                player.sendMessage(ChatColor.YELLOW + "Percentage of players to sleep is: " + main.getPercentage() + "%");
                            }
                            return true;
                        case "fixed":
                            try {
                                main.saveFixed(Integer.parseInt(args[1]));
                                player.sendMessage(ChatColor.YELLOW + "Minimum amount of players to sleep is now set to: " + main.getFixed());
                            } catch (ArrayIndexOutOfBoundsException e) {
                                main.getLogger().info(e.getMessage());
                                player.sendMessage(ChatColor.YELLOW + "Fixed amount of players to sleep is: " + main.getFixed());
                            }
                            return true;
                    }
                }
            }
        }
        return false;
    }
}

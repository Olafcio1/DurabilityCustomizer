package pl.olafcio.durabilitycustomizer;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DurabilityCustomizer extends JavaPlugin implements Listener {
    FileConfiguration config;

    @Override
    public void onEnable() {
        config = getConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("durabilitycustomizer")) {
            if (args.length >= 1 && args[0].equals("reload")) {
                if (sender.hasPermission("durabilitycustomizer.reload")) {
                    reloadConfig();
                    config = getConfig();

                    sender.sendMessage(Component.text("§3[DurabilityCustomizer]§7 Reloaded the configuration."));
                } else {
                    sender.sendMessage(Component.text("§3[DurabilityCustomizer]§cError:§4 No permission."));
                }
            } else {
                sender.sendMessage(Component.text("§3[DurabilityCustomizer]§7 Made by §aOlafcio§7 with §blove"));
            }

            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (config.getBoolean("item-durability"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onAnvilDamaged(AnvilDamagedEvent event) {
        if (config.getBoolean("anvil-damage"))
            event.setCancelled(true);
    }
}

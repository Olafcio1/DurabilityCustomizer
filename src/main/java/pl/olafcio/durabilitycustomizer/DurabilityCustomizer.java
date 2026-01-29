package pl.olafcio.durabilitycustomizer;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DurabilityCustomizer extends JavaPlugin implements Listener {
    FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
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
                    sender.sendMessage(Component.text("§3[DurabilityCustomizer]§c Error:§4 No permission."));
                }
            } else {
                sender.sendMessage(Component.text("§3[DurabilityCustomizer]§7 Made by §aOlafcio§7 with §blove"));
            }

            return true;
        }

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (config.getBoolean("durability.item-durability"))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilDamaged(AnvilDamagedEvent event) {
        if (config.getBoolean("durability.anvil-damage"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config.getBoolean("unsafe.force-unbreakable-tag.enabled")) {
            var player = event.getPlayer();
            var items = player.getInventory();

            var want = config.getBoolean("unsafe.force-unbreakable-tag.value");
            for (var i = 0; i < items.getSize(); i++) {
                var item = items.getItem(i);
                if (item != null) {
                    var meta = item.getItemMeta();
                    var found = meta.isUnbreakable();

                    if (want != found) {
                        meta.setUnbreakable(want);

                        item.setItemMeta(meta);
                        items.setItem(i, item);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPurchase(PlayerPurchaseEvent event) {
        if (config.getBoolean("unsafe.force-unbreakable-tag.enabled")) {
            var trade = event.getTrade();
            var item = trade.getResult();

            var want = config.getBoolean("unsafe.force-unbreakable-tag.value");
            var meta = item.getItemMeta();

            if (meta.isUnbreakable() != want) {
                meta.setUnbreakable(want);

                item.setItemMeta(meta);
                event.setTrade(trade);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        if (config.getBoolean("unsafe.force-unbreakable-tag.enabled")) {
            var item = event.getItem();

            var stack = item.getItemStack();
            var meta = stack.getItemMeta();

            var want = config.getBoolean("unsafe.force-unbreakable-tag.value");
            if (meta.isUnbreakable() != want) {
                meta.setUnbreakable(want);

                stack.setItemMeta(meta);
                item.setItemStack(stack);
            }
        }
    }
}

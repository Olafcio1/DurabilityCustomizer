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

import java.util.stream.Stream;

public final class DurabilityCustomizer extends JavaPlugin implements Listener {
    FileConfiguration config;

    boolean itemDurability;
    boolean anvilDamage;

    boolean futEnabled;
    boolean futValue;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void reloadConfig() {
        super.reloadConfig();
        config = getConfig();

        if (Stream.of(
                config.getString("durability.item-durability"),
                config.getString("durability.anvil-damage")
        ).anyMatch(el -> el == null || !(
                el.equalsIgnoreCase("disabled") ||
                el.equalsIgnoreCase("enabled")
        )))
            throw new RuntimeException("Durability configuration section is corrupted; please delete your configuration" +
                                       " and rewrite it after regeneration.");

        if (Stream.of(
                config.getString("unsafe.force-unbreakable-tag.enabled"),
                config.getString("unsafe.force-unbreakable-tag.value")
        ).anyMatch(el -> el == null || !(
                el.equals("true") ||
                el.equals("false")
        )))
            throw new RuntimeException("Unsafe configuration section is corrupted; please delete your configuration" +
                                       " and rewrite it after regeneration.");

        itemDurability = config.getString("durability.item-durability").equals("enabled");
        anvilDamage = config.getString("durability.anvil-damage").equals("enabled");

        futEnabled = config.getBoolean("unsafe.force-unbreakable-tag.enabled");
        futValue = config.getBoolean("unsafe.force-unbreakable-tag.value");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("durabilitycustomizer")) {
            if (args.length >= 1 && args[0].equals("reload")) {
                if (sender.hasPermission("durabilitycustomizer.reload")) {
                    reloadConfig();
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
        if (!itemDurability)
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilDamaged(AnvilDamagedEvent event) {
        if (!anvilDamage)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (futEnabled) {
            var player = event.getPlayer();
            var items = player.getInventory();

            var want = futValue;
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
        if (futEnabled) {
            var trade = event.getTrade();
            var item = trade.getResult();

            var want = futValue;
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
        if (futEnabled) {
            var item = event.getItem();

            var stack = item.getItemStack();
            var meta = stack.getItemMeta();

            final var want = futValue;
            if (meta.isUnbreakable() != want) {
                meta.setUnbreakable(want);

                stack.setItemMeta(meta);
                item.setItemStack(stack);
            }
        }
    }
}

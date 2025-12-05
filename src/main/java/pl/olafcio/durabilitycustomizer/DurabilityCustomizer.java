package pl.olafcio.durabilitycustomizer;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public final class DurabilityCustomizer extends JavaPlugin implements Listener {
    FileConfiguration config;

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        config = getConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Commands.literal("durabilitycustomizer")
                            .executes(ctx -> {
                                ctx.getSource().getSender().sendMessage(Component.text("§3[DurabilityCustomizer]§7 Made by §aOlafcio§7 with §blove"));
                                return SINGLE_SUCCESS;
                            })
                            .then(
                                Commands.literal("reload")
                                .executes(ctx -> {
                                    reloadConfig();
                                    config = getConfig();

                                    ctx.getSource().getSender().sendMessage(Component.text("§3[DurabilityCustomizer]§7 Reloaded the configuration."));

                                    return SINGLE_SUCCESS;
                                })
                            )
            .build());
        });
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

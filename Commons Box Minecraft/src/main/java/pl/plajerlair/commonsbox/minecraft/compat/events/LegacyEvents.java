package pl.plajerlair.commonsbox.minecraft.compat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBEntityPickupItemEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerPickupArrow;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.02.2021
 */
public class LegacyEvents implements Listener {

  private final JavaPlugin plugin;

  public LegacyEvents(JavaPlugin plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onEntityPickupItem(PlayerPickupItemEvent event) {
    CBEntityPickupItemEvent cbEvent = new CBEntityPickupItemEvent(event.getPlayer(), event.getItem(), event.getRemaining());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerPickupArrow(PlayerPickupItemEvent event) {
    if(!(event.getItem() instanceof Projectile)) {
      return;
    }
    CBPlayerPickupArrow cbEvent = new CBPlayerPickupArrow(event.getPlayer(), event.getItem(), (Projectile) event.getItem(), event.getRemaining(), event.getFlyAtPlayer());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

}

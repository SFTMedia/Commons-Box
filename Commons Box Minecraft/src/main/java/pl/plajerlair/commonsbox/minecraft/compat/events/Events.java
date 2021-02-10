package pl.plajerlair.commonsbox.minecraft.compat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBEntityPickupItemEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerPickupArrow;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerSwapHandItemsEvent;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.02.2021
 */
public class Events implements Listener {

  public Events(JavaPlugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
    CBPlayerSwapHandItemsEvent cbEvent = new CBPlayerSwapHandItemsEvent(event.getPlayer(), event.getMainHandItem(), event.getOffHandItem());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityPickupItem(EntityPickupItemEvent event) {
    CBEntityPickupItemEvent cbEvent = new CBEntityPickupItemEvent(event.getEntity(), event.getItem(), event.getRemaining());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
    CBPlayerPickupArrow cbEvent = new CBPlayerPickupArrow(event.getPlayer(), event.getItem(), (Projectile) event.getArrow(), event.getRemaining(), event.getFlyAtPlayer());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }
}

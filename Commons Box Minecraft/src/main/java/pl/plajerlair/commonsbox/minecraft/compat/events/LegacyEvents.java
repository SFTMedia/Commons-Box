package pl.plajerlair.commonsbox.minecraft.compat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBEntityPickupItemEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBInventoryClickEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEntityEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import pl.plajerlair.commonsbox.minecraft.compat.events.api.CBPlayerPickupArrow;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.02.2021
 */
public class LegacyEvents implements Listener {

  public LegacyEvents(JavaPlugin plugin) {
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

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    CBPlayerInteractEvent cbEvent = new CBPlayerInteractEvent(event.getPlayer(), event.getItem(), null, event.getAction(), event.getBlockFace(), event.getClickedBlock(), event.getPlayer().getLocation(), event.getMaterial());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
    CBPlayerInteractEntityEvent cbEvent = new CBPlayerInteractEntityEvent(event.getPlayer(), null, event.getRightClicked());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event) {
    CBInventoryClickEvent cbEvent = new CBInventoryClickEvent(event.getClick(), event.getCurrentItem(), event.getClickedInventory(), event.getCursor(), event.getHotbarButton(), event.getAction(), event.getRawSlot(), event.getSlot(), event.getSlotType(), event.isLeftClick(), event.isRightClick(), event.isShiftClick(), event.getView());
    Bukkit.getPluginManager().callEvent(cbEvent);
    if(cbEvent.isCancelled()) {
      event.setCancelled(true);
    }
  }

}

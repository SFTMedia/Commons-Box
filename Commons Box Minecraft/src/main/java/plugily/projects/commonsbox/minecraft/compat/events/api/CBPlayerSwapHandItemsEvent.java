package plugily.projects.commonsbox.minecraft.compat.events.api;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CBPlayerSwapHandItemsEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final ItemStack mainHandItem;
  private final ItemStack offHandItem;
  private final Player player;

  public CBPlayerSwapHandItemsEvent(Player player, ItemStack mainHandItem, ItemStack offHandItem) {
    super(false);
    this.player = player;
    this.mainHandItem = mainHandItem;
    this.offHandItem = offHandItem;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public ItemStack getMainHandItem() {
    return mainHandItem;
  }

  public ItemStack getOffHandItem() {
    return offHandItem;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }



}

package plugily.projects.commonsbox.minecraft.compat.events.api;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public class CBEntityPickupItemEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final LivingEntity entity;
  private final Item item;
  private final int remaining;

  public CBEntityPickupItemEvent(LivingEntity entity, Item item, int remaining) {
    super(false);
    this.entity = entity;
    this.item = item;
    this.remaining = remaining;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public LivingEntity getEntity() {
    return entity;
  }

  public Item getItem() {
    return item;
  }

  public int getRemaining() {
    return remaining;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }


}

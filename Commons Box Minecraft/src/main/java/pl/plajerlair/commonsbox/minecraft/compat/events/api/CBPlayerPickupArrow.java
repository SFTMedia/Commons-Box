package pl.plajerlair.commonsbox.minecraft.compat.events.api;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;

public class CBPlayerPickupArrow extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Item item;
  private final Projectile arrow;
  private final Player player;
  private final int remaining;
  private final boolean flyAtPlayer;

  public CBPlayerPickupArrow(Player player, Item item, Projectile arrow, int remaining, boolean flyAtPlayer) {
    super(false);
    this.player = player;
    this.item = item;
    this.arrow = arrow;
    this.remaining = remaining;
    this.flyAtPlayer = flyAtPlayer;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public int getRemaining() {
    return remaining;
  }

  public Projectile getArrow() {
    return arrow;
  }

  public Item getItem() {
    return item;
  }

  public boolean isFlyAtPlayer() {
    return flyAtPlayer;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }


}

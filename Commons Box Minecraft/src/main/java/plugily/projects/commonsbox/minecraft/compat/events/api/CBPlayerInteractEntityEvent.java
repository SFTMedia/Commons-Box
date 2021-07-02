package plugily.projects.commonsbox.minecraft.compat.events.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

public class CBPlayerInteractEntityEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final EquipmentSlot equipmentSlot;
  private final Entity rightClicked;


  public CBPlayerInteractEntityEvent(Player player, EquipmentSlot equipmentSlot, Entity rightClicked) {
    super(false);
    this.player = player;
    this.equipmentSlot = equipmentSlot;
    this.rightClicked = rightClicked;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public EquipmentSlot getHand() {
    return equipmentSlot;
  }

  public Entity getRightClicked() {
    return rightClicked;
  }
}

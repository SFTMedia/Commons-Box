package pl.plajerlair.commonsbox.minecraft.compat.events.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CBPlayerInteractEvent extends VersionEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final ItemStack itemStack;
  private final EquipmentSlot equipmentSlot;
  private final Action action;
  private final BlockFace blockFace;
  private final Block clickedBlock;
  private final Material material;
  private final boolean hasItem;
  private final boolean hasBlock;

  public CBPlayerInteractEvent(Player player, ItemStack itemStack, EquipmentSlot equipmentSlot, Action action, BlockFace blockFace, Block clickedBlock, Material material, boolean hasItem, boolean hasBlock) {
    super(false);
    this.player = player;
    this.itemStack = itemStack;
    this.equipmentSlot = equipmentSlot;
    this.action = action;
    this.blockFace = blockFace;
    this.clickedBlock = clickedBlock;
    this.material = material;
    this.hasItem = hasItem;
    this.hasBlock = hasBlock;
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

  public ItemStack getItem() {
    return itemStack;
  }

  public EquipmentSlot getHand() {
    return equipmentSlot;
  }

  public Action getAction() {
    return action;
  }

  public BlockFace getBlockFace() {
    return blockFace;
  }

  public Block getClickedBlock() {
    return clickedBlock;
  }

  public Material getMaterial() {
    return material;
  }

  public boolean hasItem() {
    return hasItem;
  }

  public boolean hasBlock() {
    return hasBlock;
  }
}

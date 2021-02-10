package pl.plajerlair.commonsbox.minecraft.compat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;

import static pl.plajerlair.commonsbox.minecraft.compat.PacketUtils.getNMSClass;
import static pl.plajerlair.commonsbox.minecraft.compat.PacketUtils.sendPacket;

public class VersionUtils {

  public static void sendTextComponent(CommandSender sender, TextComponent component) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      if(sender instanceof Player) {
        ((Player)sender).spigot().sendMessage(component);
      } else {
        sender.sendMessage(component.getText());
      }
    } else {
      sender.spigot().sendMessage(component);
    }
  }

  public static void setGlowing(Player player, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      player.setGlowing(value);
    }
  }

  public static void setCollidable(Player player, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      player.spigot().setCollidesWithEntities(value);
    } else {
      player.setCollidable(value);
    }
  }

  public static double getHealth(Player player) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return player.getMaxHealth();
    }

    if (MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).isPresent()) {
      return MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).get().getValue();
    }

    return 0D;
  }

  public static void setMaxHealth(Player player, double health) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      player.setMaxHealth(health);
    } else {
      MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> ai.setBaseValue(health));
    }
  }

  public static ItemStack getItemInHand(Player player) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return player.getItemInHand();
    }
    return player.getInventory().getItemInMainHand();
  }

  public static void sendActionBar(Player player, String message) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      try {
        Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);

        Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
        Object packet = constructor.newInstance(icbc, (byte) 2);
        sendPacket(player, packet);
      } catch(ReflectiveOperationException e) {
        e.printStackTrace();
      }
    } else if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R3)) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, player.getUniqueId(), new ComponentBuilder(message).create());
    } else {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
  }

  public static void sendTitles(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      sendTitle(player, title, fadeInTime, showTime, fadeOutTime);
      sendSubTitle(player, subtitle, fadeInTime, showTime, fadeOutTime);
    } else {
      player.sendTitle(title, subtitle, fadeInTime, showTime, fadeOutTime);
    }
  }

  public static void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      try {
        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");

        Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);
        sendPacket(player, packet);
      } catch(Exception ignored) {
      }
    } else {
      player.sendTitle(text, null, fadeInTime, showTime, fadeOutTime);
    }
  }

  public static void sendSubTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      try {
        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");

        Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);
        sendPacket(player, packet);
      } catch(Exception ignored) {
      }
    } else {
      player.sendTitle(null, text, fadeInTime, showTime, fadeOutTime);
    }
  }

}

package pl.plajerlair.commonsbox.minecraft.compat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XParticle;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XParticleLegacy;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;

import java.lang.reflect.Constructor;

import static pl.plajerlair.commonsbox.minecraft.compat.PacketUtils.getNMSClass;
import static pl.plajerlair.commonsbox.minecraft.compat.PacketUtils.sendPacket;

public class VersionUtils {

  public static void sendParticles(String particle, Player player, Location location, int count) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      XParticle.getParticle(particle).builder().location(location).count(count).spawn();
    } else {
      try {
        XParticleLegacy.valueOf(particle).sendToPlayer(player, location, 0, 0, 0, 0, count);
      } catch(Exception ignored) {
      }
    }
  }


  public static void updateNameTagsVisibility(JavaPlugin plugin, Player player, Player other, String tag, boolean remove) {
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_11_R1)) {
      Scoreboard scoreboard = other.getScoreboard();
      if(scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
      }
      Team team = scoreboard.getTeam(tag);
      if(team == null) {
        team = scoreboard.registerNewTeam(tag);
      }
      team.setCanSeeFriendlyInvisibles(false);
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
      if(!remove) {
        team.addEntry(player.getName());
      } else {
        team.removeEntry(player.getName());
      }
      other.setScoreboard(scoreboard);
    } else {
      if(remove) {
        Entity entity = player.getPassenger();
        if(entity != null) {
          if(entity.hasMetadata(tag)) {
            entity.remove();
          }
        }
      } else {
        //todo fix amorstand hit box prevents sword throw
        Entity entity = player.getPassenger();
        if(entity != null) {
          if(entity.hasMetadata(tag)) {
            return;
          }
        }
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setSmall(true);
        stand.setMarker(false);
        stand.setMetadata(tag, new FixedMetadataValue(plugin, true)); //Optional
        player.setPassenger(stand);
      }
    }
  }

  public static void sendTextComponent(CommandSender sender, TextComponent component) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      if(sender instanceof Player) {
        ((Player) sender).spigot().sendMessage(component);
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

  public static void setCollidable(ArmorStand stand, boolean value) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      //stand.spigot().setCollidesWithEntities(value);
    } else {
      stand.setCollidable(value);
    }
  }

  public static double getHealth(Player player) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_R3)) {
      return player.getMaxHealth();
    }

    if(MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).isPresent()) {
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
    } else if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R3)) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, player.getUniqueId(), new ComponentBuilder(message).create());
    } else {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
  }

  public static void sendTitles(Player player, String title, String subtitle, int fadeInTime, int showTime,
                                int fadeOutTime) {
    //avoid null on title
    if(title == null) {
      title = "";
    }
    if(subtitle == null) {
      subtitle = "";
    }
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

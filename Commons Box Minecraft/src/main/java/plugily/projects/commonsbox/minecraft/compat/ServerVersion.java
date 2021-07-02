package plugily.projects.commonsbox.minecraft.compat;

import org.bukkit.Bukkit;

public class ServerVersion {

  public Version getVersion() {
    return Version.getCurrent();
  }

  public enum Version {
    v0_0_R0,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_10_R2,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_14_R2,
    v1_15_R1,
    v1_15_R2,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_17_R2,
    v1_18_R1,
    v1_18_R2;

    private final int value;

    private static String[] packageVersion;
    private static Version current;

    Version() {
      value = Integer.valueOf(name().replaceAll("[^\\d.]", ""));
    }

    public int getValue() {
      return value;
    }

    public static String[] getPackageVersion() {
      if (packageVersion == null) {
        packageVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
      }

      return packageVersion;
    }

    public static Version getCurrent() {
      if(current != null)
        return current;

      String[] v = getPackageVersion();
      String vv = v[v.length - 1];
      for(Version one : values()) {
        if(one.name().equalsIgnoreCase(vv)) {
          current = one;
          break;
        }
      }

      if (current == null) { // If we forgot to add new version to enum
        current = Version.v0_0_R0;
      }

      return current;
    }

    public boolean isLower(Version version) {
      return value < version.getValue();
    }

    public boolean isHigher(Version version) {
      return value > version.getValue();
    }

    public boolean isEqual(Version version) {
      return value == version.getValue();
    }

    public boolean isEqualOrLower(Version version) {
      return value <= version.getValue();
    }

    public boolean isEqualOrHigher(Version version) {
      return value >= version.getValue();
    }

    public static boolean isCurrentEqualOrHigher(Version v) {
      return getCurrent().getValue() >= v.getValue();
    }

    public static boolean isCurrentHigher(Version v) {
      return getCurrent().getValue() > v.getValue();
    }

    public static boolean isCurrentLower(Version v) {
      return getCurrent().getValue() < v.getValue();
    }

    public static boolean isCurrentEqualOrLower(Version v) {
      return getCurrent().getValue() <= v.getValue();
    }

    public static boolean isCurrentEqual(Version v) {
      return getCurrent().getValue() == v.getValue();
    }
  }
}
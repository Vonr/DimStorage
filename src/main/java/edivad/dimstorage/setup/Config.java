package edivad.dimstorage.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import edivad.dimstorage.DimStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

  public static void registerConfig(ModContainer container) {
    var SERVER_BUILDER = new ModConfigSpec.Builder();
    SERVER_BUILDER.comment(DimStorage.MODNAME + "'s config");

    DimBlock.registerServerConfig(SERVER_BUILDER);
    DimTablet.registerServerConfig(SERVER_BUILDER);

    container.registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
  }

  public static class DimBlock {

    public static ModConfigSpec.BooleanValue ALLOW_CONFIG;
    public static ModConfigSpec.BooleanValue ALLOW_PRIVATE_NETWORK;

    public static void registerServerConfig(ModConfigSpec.Builder SERVER_BUILDER) {
      SERVER_BUILDER.push("DimChest/DimTank");

      ALLOW_CONFIG = SERVER_BUILDER
          .comment("Allow players to change the DimChest/DimTank's frequency, default: true")
          .define("allowFrequency", true);

      ALLOW_PRIVATE_NETWORK = SERVER_BUILDER
          .comment("Allow players to make DimChest/DimTank private, default: true")
          .define("allowPrivateNetwork", true);

      SERVER_BUILDER.pop();
    }
  }

  public static class DimTablet {

    public static ModConfigSpec.ConfigValue<List<? extends String>> ALLOW_LIST;

    public static void registerServerConfig(ModConfigSpec.Builder SERVER_BUILDER) {
      SERVER_BUILDER.push("DimTablet");

      ALLOW_LIST = SERVER_BUILDER
          .comment(
              "A list of blocks that the DimTablet takes and transfers to the connected DimChest",
              "[/dimstorage add] adds the item you have in the main hand to this list")
          .defineList("allow_list", allowList(),
              o -> ResourceLocation.tryParse(o.toString()) != null);

      SERVER_BUILDER.pop();
    }

    public static boolean containItem(Item item) {
      return ALLOW_LIST.get().contains(getResourceLocation(item));
    }

    public static boolean addItem(Item item) {
      if (item.equals(Items.AIR)) {
        return false;
      }
      if (containItem(item)) {
        return false;
      }

      var newList = new ArrayList<String>(ALLOW_LIST.get());
      newList.add(getResourceLocation(item));
      ALLOW_LIST.set(newList);
      return true;
    }

    public static boolean removeItem(Item item) {
      if (item.equals(Items.AIR)) {
        return false;
      }
      if (!containItem(item)) {
        return false;
      }

      var newList = new ArrayList<String>(ALLOW_LIST.get());
      newList.remove(getResourceLocation(item));
      ALLOW_LIST.set(newList);
      return true;
    }

    private static String getResourceLocation(Item item) {
      return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).toString();
    }

    private static List<String> allowList() {
      return Stream.of(Items.DIRT,
              Items.GRAVEL,
              Items.COBBLESTONE,
              Items.GRANITE,
              Items.DIORITE,
              Items.ANDESITE,
              Items.SAND,
              Items.SANDSTONE,
              Items.NETHERRACK,
              Items.END_STONE)
          .map(DimTablet::getResourceLocation)
          .toList();
    }
  }
}

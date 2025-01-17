package edivad.dimstorage.items;

import java.util.List;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import edivad.dimstorage.items.components.DimStorageComponents;
import edivad.dimstorage.items.components.FrequencyTabletComponent;
import edivad.dimstorage.manager.DimStorageManager;
import edivad.dimstorage.menu.DimTabletMenu;
import edivad.dimstorage.setup.Config;
import edivad.dimstorage.storage.DimChestStorage;
import edivad.dimstorage.tools.InventoryUtils;
import edivad.dimstorage.tools.Translations;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class DimTablet extends Item implements MenuProvider {

  public DimTablet(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    var level = context.getLevel();
    var player = context.getPlayer();
    var pos = context.getClickedPos();

    if (level.isClientSide) {
      return InteractionResult.PASS;
    }
    if (!player.isCrouching()) {
      return InteractionResult.PASS;
    }

    ItemStack device = player.getItemInHand(context.getHand());
    if (level.getBlockEntity(pos) instanceof BlockEntityDimChest dimChest) {
      if (dimChest.canAccess(player)) {
        device.set(DimStorageComponents.FREQUENCY_TABLET,
            new FrequencyTabletComponent(dimChest.getFrequency(), true, false));

        player.displayClientMessage(
            Component.literal("Linked to chest").withStyle(ChatFormatting.GREEN), false);
        return InteractionResult.SUCCESS;
      }
      player.displayClientMessage(Component.literal("Access Denied!")
              .withStyle(ChatFormatting.RED), false);
      return InteractionResult.PASS;
    }
    var frequencyComponent = device.get(DimStorageComponents.FREQUENCY_TABLET);
    if (frequencyComponent != null) {
      var updatedFrequency = new FrequencyTabletComponent(frequencyComponent.frequency(),
          frequencyComponent.bound(), !frequencyComponent.autocollect());
      device.set(DimStorageComponents.FREQUENCY_TABLET, updatedFrequency);
      if (updatedFrequency.autocollect()) {
        player.displayClientMessage(
            Component.literal("Enabled autocollect").withStyle(ChatFormatting.GREEN), false);
      } else {
        player.displayClientMessage(
            Component.literal("Disabled autocollect").withStyle(ChatFormatting.RED), false);
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    if (player.isCrouching()) {
      return super.use(level, player, hand);
    }
    var stack = player.getItemInHand(hand);
    var frequencyComponent = stack.get(DimStorageComponents.FREQUENCY_TABLET);
    if (frequencyComponent == null || !frequencyComponent.bound()) {
      if (level.isClientSide()) {
        player.displayClientMessage(
            Component.literal("Dimensional Tablet not connected to any DimChest")
                .withStyle(ChatFormatting.RED), false);
      }
      return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    if (player instanceof ServerPlayer serverPlayer && hand == InteractionHand.MAIN_HAND) {
      if (frequencyComponent.frequency().canAccess(player)) {
        serverPlayer.openMenu(this);
      }
    }
    return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), stack);
  }

  @Override
  public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot,
      boolean isSelected) {
    if (level.isClientSide) {
      return;
    }
    var frequencyComponent = stack.get(DimStorageComponents.FREQUENCY_TABLET);
    if (frequencyComponent == null) {
      return;
    }

    if (frequencyComponent.autocollect() && frequencyComponent.bound()) {
      if (entity instanceof Player player) {
        var f = frequencyComponent.frequency();
        var chestInventory = new InvWrapper(getStorage(level, f));

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
          var item = player.getInventory().getItem(i).getItem();
          if (Config.DimTablet.containItem(item)) {
            InventoryUtils.mergeItemStack(player.getInventory().getItem(i),
                0, getStorage(level, f).getContainerSize(), chestInventory);
          }
        }
      }
    }
  }

  private DimChestStorage getStorage(Level level, Frequency frequency) {
    return (DimChestStorage) DimStorageManager.instance(level)
        .getStorage(level.registryAccess(), frequency, "item");
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context,
      List<Component> tooltip, TooltipFlag tooltipFlag) {
    var ADVICE_TO_LINK = Component.translatable(Translations.PRESS)
        .withStyle(ChatFormatting.GRAY)
        .append(" ")
        .append(Component.literal("Shift").withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA))
        .append(Component.literal(" + ").withStyle(ChatFormatting.GRAY))
        .append(Component.translatable(Translations.BIND_DIMCHEST).withStyle(ChatFormatting.GRAY));

    var frequencyComponent = stack.get(DimStorageComponents.FREQUENCY_TABLET);
    if (frequencyComponent == null || !frequencyComponent.bound()) {
      tooltip.add(ADVICE_TO_LINK);
      return;
    }
    frequencyComponent.addToTooltip(context, tooltip::add, tooltipFlag);
  }

  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    return new DimTabletMenu(id, inventory, player.level());
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(this.getDescriptionId());
  }
}

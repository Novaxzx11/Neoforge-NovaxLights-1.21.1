package com.novaxzx.novaxlights.item;

import com.novaxzx.novaxlights.Config;
import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.client.ClientConfig;
import com.novaxzx.novaxlights.screen.custom.FlashlightMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NovaxLights.MODID);

    public static class LightEditorItem extends Item {
        public LightEditorItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.translatable("tooltip.novaxlights.light_editor.tooltip"));
            tooltipComponents.add(Component.empty());
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    public static class FlashLightItem extends Item {

        public FlashLightItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

            ItemStack stack = player.getItemInHand(hand);

            if(player.isShiftKeyDown() && ClientConfig.FLASHLIGHT_CHANGE_COLOR) {

                if(!level.isClientSide()) {

                    player.openMenu(
                            new SimpleMenuProvider(
                                    (id, inv, p) ->
                                            new FlashlightMenu(id, inv),
                                    Component.literal("Flashlight")
                            )
                    );
                }

                return InteractionResultHolder.success(stack);
            }

            if(!level.isClientSide()) {

                boolean on = isOn(stack);

                setOn(stack, !on);
            }

            return InteractionResultHolder.success(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.translatable("tooltip.novaxlights.flashlight.tooltip"));
            tooltipComponents.add(Component.empty());
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }

        public static boolean isOn(ItemStack stack) {

            return stack.getOrDefault(
                    DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.EMPTY
            ).copyTag().getBoolean("FlashlightOn");
        }

        public static void setOn(ItemStack stack, boolean value) {

            var tag = stack.getOrDefault(
                    DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.EMPTY
            ).copyTag();

            tag.putBoolean("FlashlightOn", value);

            stack.set(
                    DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag)
            );
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {

            super.inventoryTick(stack, level, entity, slot, selected);

            if(level.isClientSide()) {
                return;
            }

            if(!(entity instanceof Player player)) {
                return;
            }

            if(!isOn(stack)) {
                return;
            }

            // a cada 20 ticks = 1 segundo
            if(level.getGameTime() % ClientConfig.FLASHLIGHT_DRAIN_RATE == 0) {

                stack.hurtAndBreak(ClientConfig.FLASHLIGHT_DAMAGE, player, player.getEquipmentSlotForItem(stack));

                if(stack.getDamageValue() >= stack.getMaxDamage() - 1) {
                    setOn(stack, false);
                }
            }
        }
    }

    public static final DeferredItem<Item> LIGHTEDITOR = ITEMS.register("light_editor",
            () -> new LightEditorItem(new Item.Properties()));

    public static final DeferredItem<Item> FLASHLIGHT = ITEMS.register("flashlight",
            () -> new FlashLightItem(new Item.Properties()
                    .stacksTo(1)
                    .component(
                            DataComponents.DYED_COLOR,
                            new DyedItemColor(
                            0xFFF5CC, false
                            )
                    )
                    .durability(1000)
            ));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

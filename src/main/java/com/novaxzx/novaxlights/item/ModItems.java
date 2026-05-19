package com.novaxzx.novaxlights.item;

import com.novaxzx.novaxlights.NovaxLights;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NovaxLights.MODID);

    public static class PointLightEditorItem extends Item {
        public PointLightEditorItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.novaxlights.point_light_editor.tooltip"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    public static class AreaLightEditorItem extends Item {
        public AreaLightEditorItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.novaxlights.area_light_editor.tooltip"));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    public static final DeferredItem<Item> POINTLIGHTEDITOR = ITEMS.register("point_light_editor",
            () -> new PointLightEditorItem(new Item.Properties()));

    public static final DeferredItem<Item> AREALIGHTEDITOR = ITEMS.register("area_light_editor",
            () -> new AreaLightEditorItem(new Item.Properties()));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

package com.novaxzx.novaxlights.screen;

import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.screen.custom.AreaMenu;
import com.novaxzx.novaxlights.screen.custom.FlashlightMenu;
import com.novaxzx.novaxlights.screen.custom.PointMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, NovaxLights.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<PointMenu>> POINT_MENU =
            registerMenuType("point_menu", PointMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FlashlightMenu>> FLASHLIGHT_MENU =
            registerMenuType("flashlight_menu", FlashlightMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<AreaMenu>> AREA_MENU =
            registerMenuType("area_menu", AreaMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
            String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
package com.novaxzx.novaxlights.client;

import com.novaxzx.novaxlights.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ModItemColors {

    public static void register(
            RegisterColorHandlersEvent.Item event
    ) {

        event.register(

                (stack, tintIndex) -> {

                    if(tintIndex != 0) {
                        return -1;
                    }

                    DyedItemColor dyed =
                            stack.get(DataComponents.DYED_COLOR);

                    return dyed != null
                            ? 0xFF000000 | dyed.rgb()
                            : 0xFFFFF5CC;
                },

                ModItems.FLASHLIGHT.get()
        );
    }
}
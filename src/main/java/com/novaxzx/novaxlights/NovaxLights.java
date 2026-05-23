package com.novaxzx.novaxlights;

import com.novaxzx.novaxlights.block.ModBlocks;
import com.novaxzx.novaxlights.client.ModItemColors;
import com.novaxzx.novaxlights.entity.ModBlockEntities;
import com.novaxzx.novaxlights.item.ModCreativeModeTabs;
import com.novaxzx.novaxlights.item.ModItems;
import com.novaxzx.novaxlights.network.AreaLightPacket;
import com.novaxzx.novaxlights.network.FlashlightColorPacket;
import com.novaxzx.novaxlights.network.PointLightPacket;
import com.novaxzx.novaxlights.network.SyncConfigPacket;
import com.novaxzx.novaxlights.screen.ModMenuTypes;
import com.novaxzx.novaxlights.screen.custom.AreaScreen;
import com.novaxzx.novaxlights.screen.custom.FlashlightScreen;
import com.novaxzx.novaxlights.screen.custom.PointScreen;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NovaxLights.MODID)
public class NovaxLights {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "novaxlights";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public NovaxLights(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::onConfigReload);

        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModMenuTypes.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    public void registerPayloads(RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                PointLightPacket.TYPE,
                PointLightPacket.STREAM_CODEC,
                PointLightPacket::handle
        );

        registrar.playToServer(
                AreaLightPacket.TYPE,
                AreaLightPacket.STREAM_CODEC,
                AreaLightPacket::handle
        );

        registrar.playToServer(
                FlashlightColorPacket.TYPE,
                FlashlightColorPacket.STREAM_CODEC,
                FlashlightColorPacket::handle
        );

        registrar.playToClient(
                SyncConfigPacket.TYPE,
                SyncConfigPacket.STREAM_CODEC,
                SyncConfigPacket::handle
        );
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {

            event.register(ModMenuTypes.POINT_MENU.get(), PointScreen::new);
            event.register(ModMenuTypes.AREA_MENU.get(), AreaScreen::new);
            event.register(ModMenuTypes.FLASHLIGHT_MENU.get(), FlashlightScreen::new);
        }

        @SubscribeEvent
        public static void registerItemColors(
                RegisterColorHandlersEvent.Item event
        ) {

            ModItemColors.register(event);
        }
    }

    private static SyncConfigPacket createConfigPacket() {

        return new SyncConfigPacket(

                Config.MAX_DISTANCE.get(),
                Config.MAX_BRIGHTNESS.get(),

                Config.MAX_X_SIZE.get(),
                Config.MAX_Y_SIZE.get(),

                Config.MAX_ANGLE.get(),

                Config.FLASHLIGHT_CHANGE_COLOR.get(),

                Config.FLASHLIGHT_BRIGHTNESS.get(),
                Config.FLASHLIGHT_DISTANCE.get(),
                Config.FLASHLIGHT_ANGLE.get(),
                Config.FLASHLIGHT_SIZE.get(),

                Config.FLASHLIGHT_DRAIN_RATE.get(),
                Config.FLASHLIGHT_DAMAGE.get()
        );
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {

        if(event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, createConfigPacket());
        }
    }

    public void onConfigReload(ModConfigEvent.Reloading event) {
        if(event.getConfig().getSpec() != Config.SERVER_SPEC) {
            return;
        }
        for(ServerPlayer player : net.neoforged.neoforge.server.ServerLifecycleHooks
                .getCurrentServer()
                .getPlayerList()
                .getPlayers()) {
            PacketDistributor.sendToPlayer(
                    player,
                    createConfigPacket()
            );
        }
    }
}

package me.ichun.mods.ichunutil.common;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.util.EventCalendar;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(iChunUtil.MOD_ID)
public class iChunUtil
{
    public static final String MOD_ID = "ichunutil";
    public static final String MOD_NAME = "iChunUtil";

    public static final Logger LOGGER = LogManager.getLogger(); //TODO should we add Markers?

    private static ModLoadingStage loadingStage = ModLoadingStage.ERROR; //TODO spider fox easter egg

    public static ConfigClient configClient;

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient; //TODO if we have a packet channel we should only need it if a mod dep needs it.

    public iChunUtil()
    {
        loadingStage = ModLoadingStage.CONSTRUCT;
        ObfHelper.detectDevEnvironment();
        EventCalendar.checkDate();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::finishLoading);

        MinecraftForge.EVENT_BUS.register(eventHandlerServer = new EventHandlerServer());

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ResourceHelper.init();
            configClient = new ConfigClient().init();
            EntityHelper.injectMinecraftPlayerGameProfile();

            bus.addListener(this::setupClient);

            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> EventHandlerClient::getConfigGui);
        });
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        loadingStage = ModLoadingStage.COMMON_SETUP;
        //TODO do I wanna check for mod updates
    }

    private void setupClient(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());
    }

    private void finishLoading(FMLLoadCompleteEvent event)
    {
        loadingStage = ModLoadingStage.COMPLETE;
        ConfigBase.CONFIGS.forEach(c -> {
            if(!c.hasInit()) throw new RuntimeException("Config class created but never initialized: " + c.getConfigName());
        });
    }

    public static ModLoadingStage getLoadingStage()
    {
        return loadingStage;
    }
}

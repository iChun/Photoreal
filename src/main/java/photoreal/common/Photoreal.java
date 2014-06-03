package photoreal.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.relauncher.Side;
import ichun.common.core.config.Config;
import ichun.common.core.config.ConfigHandler;
import ichun.common.core.config.IConfigUser;
import ichun.common.core.network.ChannelHandler;
import ichun.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import photoreal.common.core.CommonProxy;
import photoreal.common.packet.PacketTakeSnapshot;

import java.util.EnumMap;

@Mod(modid = "Photoreal", name = "Photoreal",
			version = Photoreal.version,
            dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".0.0,)",
            acceptableRemoteVersions = "[" + iChunUtil.versionMC +".0.0," + iChunUtil.versionMC + ".1.0)"
				)
public class Photoreal
	implements IConfigUser
{
    public static final String version = iChunUtil.versionMC +".0.0";

    private static Logger logger = LogManager.getLogger("Photoreal");

    public static Config config;

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    @Instance("Photoreal")
	public static Photoreal instance;
	
	@SidedProxy(clientSide = "photoreal.client.core.ClientProxy", serverSide = "photoreal.common.core.CommonProxy")
	public static CommonProxy proxy;
	
	public static Item itemCamera;
	
	@Override
	public boolean onConfigChange(Config cfg, Property prop) { return true; }

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "photoreal", "Photoreal", logger, instance);

        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            config.setCurrentCategory("clientOnly", "photoreal.config.cat.clientOnly.name", "photoreal.config.cat.clientOnly.comment");
            config.createIntProperty("cameraFreq", "photoreal.config.prop.cameraFreq.name", "photoreal.config.prop.cameraFreq.comment", true, false, 20, 0, 20);
            iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindAttack);
            iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
        }

        config.setCurrentCategory("gameplay", "photoreal.config.cat.gameplay.name", "photoreal.config.cat.gameplay.comment");
		config.createIntProperty("cameraRecharge", "photoreal.config.prop.cameraRecharge.name", "photoreal.config.prop.cameraRecharge.comment", true, false, 100, 0, Integer.MAX_VALUE);
		config.createIntProperty("photorealDuration", "photoreal.config.prop.photorealDuration.name", "photoreal.config.prop.photorealDuration.comment", true, false, 400, 0, Integer.MAX_VALUE);
		config.createIntProperty("cameraRarity", "photoreal.config.prop.cameraRarity.name", "photoreal.config.prop.cameraRarity.comment", false, false, 1, 0, 100);


		MinecraftForge.EVENT_BUS.register(new photoreal.common.core.EventHandler());

        proxy.initMod();

        channels = ChannelHandler.getChannelHandlers("Photoreal", PacketTakeSnapshot.class);
	}
	
    public static void console(String s, boolean warning)
    {
    	StringBuilder sb = new StringBuilder();
    	logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}

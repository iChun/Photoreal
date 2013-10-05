package photoreal.common;

import ichun.core.LoggerHelper;
import ichun.core.config.Config;
import ichun.core.config.ConfigHandler;
import ichun.core.config.IConfigUser;

import java.util.logging.Logger;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import photoreal.common.core.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "Photoreal", name = "Photoreal",
			version = Photoreal.version,
			dependencies = "required-after:iChunUtil@[2.2.0,)"
				)
@NetworkMod(clientSideRequired = true,
			serverSideRequired = false
				)
public class Photoreal 
	implements IConfigUser
{
	public static final String version = "2.0.0";
	
	@Instance("Photoreal")
	public static Photoreal instance;
	
	@SidedProxy(clientSide = "photoreal.client.core.ClientProxy", serverSide = "photoreal.common.core.CommonProxy")
	public static CommonProxy proxy;
	
	public static Config config;
	
	private static final Logger logger = LoggerHelper.createLogger("Photoreal");
	
	public static Item itemCamera;
	
	@Override
	public boolean onConfigChange(Config cfg, Property prop) { return true; }

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "photoreal", "Photoreal", logger, instance);
		
		config.createOrUpdateItemIDProperty("itemID", "Item IDs", "cameraID", "Camera Item ID", "Item ID for the camera", 13610);

		MinecraftForge.EVENT_BUS.register(new photoreal.common.core.EventHandler());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.initMod();
	}
	
	@EventHandler
	public void postLoad(FMLPostInitializationEvent event)
	{
	}
	
}

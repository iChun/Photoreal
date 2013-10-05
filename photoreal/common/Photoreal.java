package photoreal.common;

import ichun.core.LoggerHelper;

import java.util.logging.Logger;

import net.minecraftforge.common.MinecraftForge;
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
{
	public static final String version = "2.0.0";
	
	@Instance("Photoreal")
	public static Photoreal instance;
	
	@SidedProxy(clientSide = "photoreal.client.core.ClientProxy", serverSide = "photoreal.common.core.CommonProxy")
	public static CommonProxy proxy;
	
	private static final Logger logger = LoggerHelper.createLogger("Photoreal");

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
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

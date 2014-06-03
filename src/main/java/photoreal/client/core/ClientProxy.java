package photoreal.client.core;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import photoreal.client.render.RenderCameraItem;
import photoreal.client.render.RenderPhotoreal;
import photoreal.common.Photoreal;
import photoreal.common.core.CommonProxy;
import photoreal.common.entity.EntityPhotoreal;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void initMod()
	{
		super.initMod();
		
		tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);

		MinecraftForgeClient.registerItemRenderer(Photoreal.itemCamera, (IItemRenderer)new RenderCameraItem());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityPhotoreal.class, new RenderPhotoreal());
	}
}

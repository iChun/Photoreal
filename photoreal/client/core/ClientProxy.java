package photoreal.client.core;

import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import photoreal.client.render.RenderCameraItem;
import photoreal.common.Photoreal;
import photoreal.common.core.CommonProxy;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void initMod()
	{
		super.initMod();
		
		tickHandlerClient = new TickHandlerClient();
		TickRegistry.registerTickHandler(tickHandlerClient, Side.CLIENT);

		MinecraftForgeClient.registerItemRenderer(Photoreal.itemCamera.itemID, (IItemRenderer)new RenderCameraItem());
	}
}

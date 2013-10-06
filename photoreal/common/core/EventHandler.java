package photoreal.common.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import photoreal.common.item.ItemCamera;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandler 
{

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		
	}
	
	@ForgeSubscribe
	public void onInteract(EntityInteractEvent event)
	{
		if(holdingCamera(event.entityPlayer))
		{
			event.setCanceled(true);
		}
	}
	
	public boolean holdingCamera(EntityPlayer player)
	{
		ItemStack is = player.getCurrentEquippedItem();
		if(is != null && is.getItem() instanceof ItemCamera)
		{
			return true;
		}
		return false;
	}
}

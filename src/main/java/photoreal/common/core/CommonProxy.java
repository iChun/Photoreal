package photoreal.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import photoreal.client.core.TickHandlerClient;
import photoreal.common.Photoreal;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy 
{
	public void initMod() 
	{
		Photoreal.itemCamera = (new ItemCamera(Photoreal.config.getInt("cameraID"))).setFull3D().setUnlocalizedName("PhotorealCamera").setCreativeTab(CreativeTabs.tabMisc);
		
		EntityRegistry.registerModEntity(EntityPhotoreal.class, "photoreal", 63, Photoreal.instance, 200, Integer.MAX_VALUE, false);
		
		LanguageRegistry.instance().addName(Photoreal.itemCamera, EnumChatFormatting.AQUA + "Camera");
		
		ItemStack is = new ItemStack(Photoreal.itemCamera, 1);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("recharge", 0);
		is.setTagCompound(tag);
		
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(is, 1, 1, Photoreal.config.getInt("cameraRarity")));
		
		tickHandlerServer = new TickHandlerServer();
		TickRegistry.registerTickHandler(tickHandlerServer, Side.SERVER);
	}
	
	public TickHandlerClient tickHandlerClient;
	public TickHandlerServer tickHandlerServer;
}

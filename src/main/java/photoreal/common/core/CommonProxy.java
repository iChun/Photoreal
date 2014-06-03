package photoreal.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
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

public class CommonProxy 
{
	public void initMod() 
	{
		Photoreal.itemCamera = GameRegistry.registerItem((new ItemCamera()).setFull3D().setUnlocalizedName("PhotorealCamera").setCreativeTab(CreativeTabs.tabMisc), "PhotorealCamera", "Photoreal");

		EntityRegistry.registerModEntity(EntityPhotoreal.class, "photoreal", 63, Photoreal.instance, 200, Integer.MAX_VALUE, false);

        //TODO remove this.
		LanguageRegistry.instance().addName(Photoreal.itemCamera, EnumChatFormatting.AQUA + "Camera");
		
		ItemStack is = new ItemStack(Photoreal.itemCamera, 1);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("recharge", 0);
		is.setTagCompound(tag);

		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(is, 1, 1, Photoreal.config.getInt("cameraRarity")));
		
		tickHandlerServer = new TickHandlerServer();
        FMLCommonHandler.instance().bus().register(tickHandlerServer);
	}
	
	public TickHandlerClient tickHandlerClient;
	public TickHandlerServer tickHandlerServer;
}

package photoreal.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumChatFormatting;
import photoreal.common.Photoreal;
import photoreal.common.item.ItemCamera;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy 
{
	public void initMod() 
	{
		Photoreal.itemCamera = (new ItemCamera(Photoreal.config.getInt("cameraID"))).setFull3D().setUnlocalizedName("PhotorealCamera").setCreativeTab(CreativeTabs.tabMisc);
		
		LanguageRegistry.instance().addName(Photoreal.itemCamera, EnumChatFormatting.AQUA + "Camera");
	}
}

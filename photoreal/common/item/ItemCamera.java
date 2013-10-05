package photoreal.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCamera extends Item 
{
	public ItemCamera(int i)
	{
		super(i);
        maxStackSize = 1;
        setMaxDamage(0);
        setHasSubtypes(true);
	}
	
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int i, int j, int k, EntityPlayer player) 
    {
	    return true;
    }
    
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) 
    {
        return true;
    }
    
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) 
    {
    	return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister reg)
    {
    	itemIcon = reg.registerIcon("photoreal:camera");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List list, boolean flag)
    {
    	list.add("Photoreal!");
    }
    
//    @Override
//    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List itemList)
//    {
//    	if (this.itemID != Item.potion.itemID && this.itemID != Item.monsterPlacer.itemID)
//    	{
//    		for(int i = 0; i <= 4 ; i++)
//    		{
//    			ItemStack is = new ItemStack(this, 1, i);
//    			NBTTagCompound tag = new NBTTagCompound();
//    			is.setTagCompound(tag);
//				itemList.add(is);
//    		}
//    	}
//    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

}

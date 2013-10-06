package photoreal.common.core;

import java.util.EnumSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerServer implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
        if (type.equals(EnumSet.of(TickType.WORLD)))
        {
        	worldTick((World)tickData[0]);
        }
        else if (type.equals(EnumSet.of(TickType.PLAYER)))
        {
        	playerTick((World)((EntityPlayer)tickData[0]).worldObj, (EntityPlayerMP)tickData[0]);
        }
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() 
	{
		return "TickHandlerServerPhotoreal";
	}

	public void worldTick(World world)
	{
		for(int i = 0; i < world.loadedEntityList.size(); i++)
		{
			Entity ent = (Entity)world.loadedEntityList.get(i);
			if(ent instanceof EntityZombie)
			{
				EntityZombie zombie = (EntityZombie)ent;
				boolean currentItemIsCamera = zombie.getHeldItem() != null && zombie.getHeldItem().getItem() instanceof ItemCamera;
				if(currentItemIsCamera)
				{
					if(zombie.getRNG().nextFloat() < 0.003F)
					{
						EntityPhotoreal photo = new EntityPhotoreal(world);
						photo.setLocationAndAngles(zombie.posX, zombie.posY + 1.62D, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);
						photo.setup(zombie.posX, zombie.posY + 1.62D, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch, 860, 480);
						
						world.spawnEntityInWorld(photo);
					}
				}
			}
		}
	}
	
	public void playerTick(World world, EntityPlayerMP player)
	{
	}
	
}

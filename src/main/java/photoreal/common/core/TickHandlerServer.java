package photoreal.common.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;

public class TickHandlerServer
{
    @SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event)
	{
        if(event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            for(int i = 0; i < event.world.loadedEntityList.size(); i++)
            {
                Entity ent = (Entity)event.world.loadedEntityList.get(i);
                if(ent instanceof EntityZombie)
                {
                    //TODO find a better way to see if a zombie is ticking.
                    EntityZombie zombie = (EntityZombie)ent;
                    boolean currentItemIsCamera = zombie.getHeldItem() != null && zombie.getHeldItem().getItem() instanceof ItemCamera;
                    if(currentItemIsCamera)
                    {
                        if(zombie.getRNG().nextFloat() < 0.003F)
                        {
                            EntityPhotoreal photo = new EntityPhotoreal(event.world);
                            photo.setLocationAndAngles(zombie.posX, zombie.posY + 1.62D, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);
                            photo.setup(zombie.posX, zombie.posY + 1.62D, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch, 860, 480);

                            event.world.spawnEntityInWorld(photo);
                        }
                    }
                }
            }
        }
	}
}

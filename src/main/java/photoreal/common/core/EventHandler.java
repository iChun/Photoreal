package photoreal.common.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.keybind.KeyEvent;
import ichun.common.core.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import photoreal.common.Photoreal;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;
import photoreal.common.packet.PacketTakeSnapshot;

public class EventHandler 
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyEvent(KeyEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen == null && !Photoreal.proxy.tickHandlerClient.hasScreen)
        {
            ItemStack currentInv = mc.thePlayer.inventory.getCurrentItem();
            if(currentInv != null && currentInv.getItem() == Photoreal.itemCamera)
            {
                if(event.keyBind.isPressed() && event.keyBind.isMinecraftBind())
                {
                    if(event.keyBind.keyIndex == mc.gameSettings.keyBindAttack.getKeyCode())
                    {
                        if(Photoreal.proxy.tickHandlerClient.renderCameraOverlay && Photoreal.proxy.tickHandlerClient.lookingDownCameraTimer == 10)
                        {
                            mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("photoreal", "shutter"), 0.3F, 1.0F, (float)mc.thePlayer.posX, (float)(mc.thePlayer.posY), (float)mc.thePlayer.posZ));
                            if(currentInv.getTagCompound() != null && currentInv.getTagCompound().getInteger("recharge") <= 0)
                            {
                                mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("photoreal", "flash"), 0.3F, 1.0F, (float)mc.thePlayer.posX, (float)(mc.thePlayer.posY), (float)mc.thePlayer.posZ));
                                Photoreal.proxy.tickHandlerClient.flashTimeout = 3;

                                PacketHandler.sendToServer(Photoreal.channels, new PacketTakeSnapshot(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.displayWidth, mc.displayHeight));
                            }
                        }
                    }
                    if(event.keyBind.keyIndex == mc.gameSettings.keyBindUseItem.getKeyCode())
                    {
                        Photoreal.proxy.tickHandlerClient.shouldLookDownCamera = !Photoreal.proxy.tickHandlerClient.shouldLookDownCamera;
                        Photoreal.proxy.tickHandlerClient.renderCameraOverlay = false;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie)event.entityLiving;
            boolean currentItemIsCamera = zombie.getHeldItem() != null && zombie.getHeldItem().getItem() instanceof ItemCamera;
            if(currentItemIsCamera)
            {
                if(zombie.getRNG().nextFloat() < 0.003F)
                {
                    EntityPhotoreal photo = new EntityPhotoreal(zombie.worldObj);
                    photo.setLocationAndAngles(zombie.posX, zombie.posY + 1.62D, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);
                    photo.setup(zombie.posX, zombie.posY + 1.62D, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch, 860, 480);

                    event.entityLiving.worldObj.spawnEntityInWorld(photo);
                }
            }
        }
    }

	@SubscribeEvent
	public void onInteract(EntityInteractEvent event)
	{
        if(holdingCamera(event.entityPlayer))
		{
			event.setCanceled(true);
		}
	}

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if(holdingCamera(event.getPlayer()))
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

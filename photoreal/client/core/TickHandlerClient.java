package photoreal.client.core;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import ichun.client.render.RendererHelper;
import ichun.core.ObfHelper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import photoreal.client.render.TextureRender;
import photoreal.common.Photoreal;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class TickHandlerClient 
	implements ITickHandler 
{

	public TickHandlerClient()
	{
		try
		{
			cameraPoV = new TextureRender();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Photoreal.console("Your graphics card does not support this mod!", true);
			cameraPoV = null;
		}
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		if (type.equals(EnumSet.of(TickType.RENDER)))
		{
			if(Minecraft.getMinecraft().theWorld != null)
			{
				preRenderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, (Float)tickData[0]); //only ingame
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
        if (type.equals(EnumSet.of(TickType.CLIENT)))
        {
        	if(Minecraft.getMinecraft().theWorld != null)
        	{      		
        		worldTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld);
        	}
        }
        else if (type.equals(EnumSet.of(TickType.PLAYER)))
        {
        	playerTick((World)((EntityPlayer)tickData[0]).worldObj, (EntityPlayer)tickData[0]);
        }
        else if (type.equals(EnumSet.of(TickType.RENDER)))
        {
        	if(Minecraft.getMinecraft().theWorld != null)
        	{
        		renderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, (Float)tickData[0]); //only ingame
        	}
        }
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT, TickType.RENDER);
	}

	@Override
	public String getLabel() 
	{
		return "TickHandlerClientPhotoreal";
	}

	public void worldTick(Minecraft mc, WorldClient world)
	{
		if(cameraPoV != null && world.getWorldTime() % ((20 - Photoreal.config.getInt("cameraFreq")) + 1) == 0)
		{
            glPushMatrix();
            glLoadIdentity();
            
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, cameraPoV.fbo);
            
            glClear(GL_STENCIL_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            
            glColor3f(1, 0, 0);
            
            EntityLivingBase viewEntity = (EntityLivingBase) mc.renderViewEntity;
            
            double posX = viewEntity.posX;
            double posY = viewEntity.posY;
            double posZ = viewEntity.posZ;
            float rotYaw = viewEntity.rotationYaw;

            float progress = 1.0F - (lookingDownCameraTimer / 10F);
            
            viewEntity.posX -= (double)(MathHelper.cos(viewEntity.rotationYaw / 180.0F * (float)Math.PI) * 0.18F) * progress;
            viewEntity.posY -= 0.10D * progress;
            viewEntity.posZ -= (double)(MathHelper.sin(viewEntity.rotationYaw / 180.0F * (float)Math.PI) * 0.18F) * progress;
            
            viewEntity.posX += (double)(MathHelper.sin(viewEntity.rotationYaw / 180.0F * (float)Math.PI) * -0.2F) * progress;
            viewEntity.posZ -= (double)(MathHelper.cos(viewEntity.rotationYaw / 180.0F * (float)Math.PI) * -0.2F) * progress;

            viewEntity.rotationYaw -= 10F * progress;
            
            boolean hideGui = mc.gameSettings.hideGUI;
            mc.gameSettings.hideGUI = true;
            
            int tp = mc.gameSettings.thirdPersonView;
            
            mc.gameSettings.thirdPersonView = 0;
            
            mc.entityRenderer.renderWorld(1.0F, 0L);
            
            mc.gameSettings.thirdPersonView = tp;
            
            mc.gameSettings.hideGUI = hideGui;
            
            viewEntity.posX = posX;
            viewEntity.posY = posY;
            viewEntity.posZ = posZ;
            viewEntity.rotationYaw = rotYaw;
            
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
            
            glPopMatrix();
		}
		
		if(clock != mc.theWorld.getWorldTime() || !world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
		{
			clock = world.getWorldTime();

			if(shouldLookDownCamera)
			{
				lookingDownCameraTimer++;
				if(lookingDownCameraTimer == 10)
				{
					renderCameraOverlay = true;
				}
				if(lookingDownCameraTimer > 10)
				{
					lookingDownCameraTimer = 10;
				}
			}
			else
			{
				lookingDownCameraTimer--;
				if(lookingDownCameraTimer < 0)
				{
					lookingDownCameraTimer = 0;
				}
			}
			if(flashTimeout > 0)
			{
				flashTimeout--;
			}
		}
		
		if(mc.currentScreen == null && !hasScreen)
		{
			ItemStack currentInv = mc.thePlayer.inventory.getCurrentItem();
			if(currentInv != null && currentInv.getItem() instanceof ItemCamera)
			{
				if(!primaryKeyDown && isPressed(mc.gameSettings.keyBindAttack.keyCode))
				{
					if(renderCameraOverlay && lookingDownCameraTimer == 10)
					{
						mc.sndManager.playSound("photoreal:shutter", (float)mc.thePlayer.posX, (float)(mc.thePlayer.posY), (float)mc.thePlayer.posZ, 0.3F, 1.0F);
						if(currentInv.getTagCompound() != null && currentInv.getTagCompound().getInteger("recharge") <= 0)
						{
							mc.sndManager.playSound("photoreal:flash", (float)mc.thePlayer.posX, (float)(mc.thePlayer.posY), (float)mc.thePlayer.posZ, 0.3F, 1.0F);
							flashTimeout = 3;
							
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							DataOutputStream stream = new DataOutputStream(bytes);
							try
							{
								stream.writeDouble(mc.thePlayer.posX);
								stream.writeDouble(mc.thePlayer.posY);
								stream.writeDouble(mc.thePlayer.posZ);
								
								stream.writeFloat(mc.thePlayer.rotationYaw);
								stream.writeFloat(mc.thePlayer.rotationPitch);
								
								stream.writeInt(mc.displayWidth);
								stream.writeInt(mc.displayHeight);
								
								PacketDispatcher.sendPacketToServer(new Packet131MapData((short)Photoreal.getNetId(), (short)0, bytes.toByteArray()));
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				if(!secondaryKeyDown && isPressed(mc.gameSettings.keyBindUseItem.keyCode))
				{
					shouldLookDownCamera = !shouldLookDownCamera;
					renderCameraOverlay = false;
				}
			}
		}
		hasScreen = mc.currentScreen != null;
		
		primaryKeyDown = isPressed(mc.gameSettings.keyBindAttack.keyCode);
		secondaryKeyDown = isPressed(mc.gameSettings.keyBindUseItem.keyCode);
	}
	
	public void playerTick(World world, EntityPlayer player)
	{
	}
	
	public void preRenderTick(Minecraft mc, World world, float renderTick)
	{
		this.renderTick = renderTick;
        if (cameraPoV != null && (screenWidth != mc.displayWidth || screenHeight != mc.displayHeight))
        {
            screenWidth = mc.displayWidth;
            screenHeight = mc.displayHeight;
            cameraPoV.updateResolution(screenWidth, screenHeight);
        }
        
        for (int i = pendingRenders.size() - 1; i >= 0; i--) 
        {
			EntityPhotoreal photo = pendingRenders.get(i);
			if(photo.worldObj != world)
        	{
				pendingRenders.remove(i);
				continue;
        	}
			if(photo.tex == null)
			{
				photo.rendered = true;
				pendingRenders.remove(i);
				continue;
			}
            glPushMatrix();
            glLoadIdentity();
            
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, photo.tex.fbo);
            
            glClear(GL_STENCIL_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            
            glColor3f(1, 0, 0);
            
            EntityLivingBase viewEntity = (EntityLivingBase) mc.renderViewEntity;
            
            double posX = viewEntity.posX;
            double posY = viewEntity.posY;
            double posZ = viewEntity.posZ;
            float rotYaw = viewEntity.rotationYaw;
            float rotPitch = viewEntity.rotationPitch;

            float progress = 1.0F - (lookingDownCameraTimer / 10F);
            
            viewEntity.posX = (double)photo.getDataWatcher().getWatchableObjectFloat(16);
            viewEntity.posY = (double)photo.getDataWatcher().getWatchableObjectFloat(17);
            viewEntity.posZ = (double)photo.getDataWatcher().getWatchableObjectFloat(18);
            
            viewEntity.rotationYaw = photo.getDataWatcher().getWatchableObjectFloat(19);
            viewEntity.rotationPitch = photo.getDataWatcher().getWatchableObjectFloat(20);
            
            boolean hideGui = mc.gameSettings.hideGUI;
            mc.gameSettings.hideGUI = true;
            
            int tp = mc.gameSettings.thirdPersonView;
            
            mc.gameSettings.thirdPersonView = 0;
            
            mc.entityRenderer.renderWorld(1.0F, 0L);
            
            if(photo.fogR == 1.0D && photo.fogG == 1.0D && photo.fogB == 1.0D)
            {
                try
                {
                	photo.fogR = (Float)ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, mc.entityRenderer, ObfHelper.fogColorRed);
                	photo.fogG = (Float)ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, mc.entityRenderer, ObfHelper.fogColorGreen);
                	photo.fogB = (Float)ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, mc.entityRenderer, ObfHelper.fogColorBlue);
                }
                catch(Exception e)
                {
                	e.printStackTrace();
                	ObfHelper.obfWarning();
                	Vec3 fog = Minecraft.getMinecraft().theWorld.getFogColor(1.0F); 
                	photo.fogR = fog.xCoord;
                	photo.fogG = fog.yCoord;
                	photo.fogB = fog.zCoord;
                }
            }
            
            mc.gameSettings.thirdPersonView = tp;
            
            mc.gameSettings.hideGUI = hideGui;
            
            viewEntity.posX = posX;
            viewEntity.posY = posY;
            viewEntity.posZ = posZ;
            viewEntity.rotationYaw = rotYaw;
            viewEntity.rotationPitch = rotPitch;
            
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
            
            glPopMatrix();
            
            photo.rendered = true;
            if(photo.getDataWatcher().getWatchableObjectInt(23) == 0)
            {
            	photo.flashTimeout = 3;
            }
            pendingRenders.remove(i);
		}
        
		ItemStack currentInv = mc.thePlayer.getCurrentEquippedItem();
		if(currentInv != null)
		{
			if(currentInv.getItem() instanceof ItemCamera)
			{
				mc.playerController.resetBlockRemoving();
				if(prevCurItem == mc.thePlayer.inventory.currentItem)
				{
					try
					{
						ObfuscationReflectionHelper.setPrivateValue(ItemRenderer.class, mc.entityRenderer.itemRenderer, 1.0F, ObfHelper.equippedProgress);
						ObfuscationReflectionHelper.setPrivateValue(ItemRenderer.class, mc.entityRenderer.itemRenderer, 1.0F, ObfHelper.prevEquippedProgress);
						ObfuscationReflectionHelper.setPrivateValue(ItemRenderer.class, mc.entityRenderer.itemRenderer, mc.thePlayer.inventory.getCurrentItem(), ObfHelper.itemToRender);
			            ReflectionHelper.setPrivateValue(ItemRenderer.class, mc.entityRenderer.itemRenderer, mc.thePlayer.inventory.currentItem, ObfHelper.equippedItemSlot);
			            
			            int showName = (Integer)ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, mc.ingameGUI, ObfHelper.showNameTime);
			            if(showName == 0)
			            {
			            	hasShownTooltip = true;
			            }
			            if(hasShownTooltip)
			            {
			            	ReflectionHelper.setPrivateValue(GuiIngame.class, mc.ingameGUI, 0, ObfHelper.showNameTime);
			            }
					}
					catch(Exception e)
					{
						ObfHelper.obfWarning();
						e.printStackTrace();
					}
				}
				mc.thePlayer.isSwingInProgress = false;
				mc.thePlayer.swingProgressInt = 0;
				mc.thePlayer.swingProgress = 0;
			}
		}
		currentItemIsCamera = currentInv != null && currentInv.getItem() instanceof ItemCamera;
		if(prevCurItem != mc.thePlayer.inventory.currentItem)
		{
			if(mc.thePlayer.inventory.currentItem >= 0 && mc.thePlayer.inventory.currentItem <= 9)
			{
				try
				{
					if((Float)(ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, mc.entityRenderer.itemRenderer, ObfHelper.equippedProgress)) >= 1.0F)
					{
						prevCurItem = mc.thePlayer.inventory.currentItem;
					}
				}
				catch(Exception e)
				{
					ObfHelper.obfWarning();
					e.printStackTrace();
				}
			}
			currentItemIsCamera = false;
		}
		if(!currentItemIsCamera)
		{
			shouldLookDownCamera = false;
			renderCameraOverlay = false;
			lookingDownCameraTimer = 0;
		}
	}
	
	public void renderTick(Minecraft mc, World world, float renderTick)
	{
    	if(!(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) && mc.gameSettings.thirdPersonView == 0)
    	{
    		if(renderCameraOverlay)
    		{
		        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		        int width = scaledresolution.getScaledWidth();
		        int height = scaledresolution.getScaledHeight();
		        
		        double size = 48D;
		        
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		        
		        GL11.glDisable(GL11.GL_DEPTH_TEST);
		        GL11.glDepthMask(false);
		        GL11.glDisable(GL11.GL_ALPHA_TEST);
		        
		        RendererHelper.drawColourOnScreen(0x00802A, 35, 0, 0, width, height, 90D);
	
		        RendererHelper.drawTextureOnScreen(texCamVignette, 0, 0, width, height, 90D);
		        
		        float flashProg = (float)Math.pow((flashTimeout - renderTick) / 3F, 2D);
		        if(flashProg < 0.0F || flashTimeout == 0)
		        {
		        	flashProg = 0.0F;
		        }
		        
		        RendererHelper.drawColourOnScreen(0xffffff, (int)(240F * flashProg), 0, 0, width, height, 90D);
		        
		        float brightness = 0.3F;
		        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
		        RendererHelper.drawTextureOnScreen(texCamTop, size / 2D, 0, width - size, size, 90D);
		        RendererHelper.drawTextureOnScreen(texCamBottom, size / 2D, height - size, width - size, size, 90D);
		        RendererHelper.drawTextureOnScreen(texCamLeft, 0, size / 2D, size, height - size, 90D);
		        RendererHelper.drawTextureOnScreen(texCamRight, width - size, size / 2D, size, height - size, 90D);
		        
		        RendererHelper.drawTextureOnScreen(texCamTopLeft, 0, 0, size, size, 90D);
		        RendererHelper.drawTextureOnScreen(texCamBottomLeft, 0, height - size, size, size, 90D);
		        RendererHelper.drawTextureOnScreen(texCamTopRight, width - size, 0, size, size, 90D);
		        RendererHelper.drawTextureOnScreen(texCamBottomRight, width - size, height - size, size, size, 90D);
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        
		        double centralSize = 48D;
		        RendererHelper.drawTextureOnScreen(texCamCentral, width / 2 - centralSize / 2, height / 2 - centralSize / 2, centralSize, centralSize, 90D);
		        
		        String reso = Block.planks.getIcon(0, 0).getIconHeight() + "x";
		        int resoSize = mc.fontRenderer.getStringWidth(reso); 
		        
		        double vertAlignReso = 27D;
		        double horiAlignReso = 0D;
		        
		        RendererHelper.drawColourOnScreen(0x817e72, 255, size + horiAlignReso - 6, height - size - vertAlignReso - 2.5, resoSize + 12, 13, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso - 5, height - size - vertAlignReso - 1.5, 3D, 2, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso - 5, height - size - vertAlignReso + 1.5, 3D, 2, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso - 5, height - size - vertAlignReso + 4.5, 3D, 2, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso - 5, height - size - vertAlignReso + 7.5, 3D, 2, 90D);
		        
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso + resoSize + 2, height - size - vertAlignReso - 1.5, 3D, 2, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso + resoSize + 2, height - size - vertAlignReso + 1.5, 3D, 2, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso + resoSize + 2, height - size - vertAlignReso + 4.5, 3D, 2, 90D);
		        RendererHelper.drawColourOnScreen(0, 255, size + horiAlignReso + resoSize + 2, height - size - vertAlignReso + 7.5, 3D, 2, 90D);
		        
		        mc.fontRenderer.drawString(reso, (int)(size + horiAlignReso), (int)(height - size - vertAlignReso), 0x313131); // width height colour
		        
		        RendererHelper.drawColourOnScreen(0x817e72, 255, size + horiAlignReso - 6, height - size - vertAlignReso + 15 - 2.5, mc.fontRenderer.getStringWidth("Auto") + 12, 13, 90D);
		        mc.fontRenderer.drawString("Auto", (int)(size + horiAlignReso), (int)(height - size - vertAlignReso + 15), 0x313131); // width height colour
		        
		        mc.fontRenderer.drawString(mc.theWorld.getWorldTime() % 24000L < 12000L ? "F2.9" : "F2.4", (int)(size + horiAlignReso - 6), (int)(height - size - vertAlignReso + 29), 0x817e72); // width height colour
		        
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        
		        ItemStack is = mc.thePlayer.getCurrentEquippedItem();
		        if(is != null && is.getItem() instanceof ItemCamera && is.getTagCompound() != null && (is.getTagCompound().getInteger("recharge") > 0 && mc.thePlayer.ticksExisted % 20L < 10L || is.getTagCompound().getInteger("recharge") == 0))
		        {
		        	if(is.getTagCompound().getInteger("recharge") > 0)
		        	{
		        		GL11.glColor4f(1.0F, 0.2F, 0.2F, 1.0F);
		        	}
		        	RendererHelper.drawTextureOnScreen(texCamFlash, size + horiAlignReso + 20, height - size - vertAlignReso + 29, 7, 7, 90D);
		        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        }
		        
		        GL11.glEnable(GL11.GL_ALPHA_TEST);
		        GL11.glEnable(GL11.GL_DEPTH_TEST);
		        GL11.glDepthMask(true);
		        
		        GL11.glDisable(GL11.GL_BLEND);
    		}
    	}
	}
	
    public void sendKeybind(int i, boolean pressed)
    {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try
		{
			stream.writeByte(i);
			stream.writeBoolean(pressed);
			
			PacketDispatcher.sendPacketToServer(new Packet131MapData((short)Photoreal.getNetId(), (short)0, bytes.toByteArray()));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
    }
	
    public static boolean isPressed(int key)
    {
    	if(key < 0)
    	{
    		return Mouse.isButtonDown(key + 100);
    	}
    	return Keyboard.isKeyDown(key);
    }
    
	private boolean hasShownTooltip;
    public int prevCurItem;
    public boolean currentItemIsCamera;
    
    public boolean renderCameraOverlay;
    
    public float renderTick;
    public boolean shouldLookDownCamera;
    public int lookingDownCameraTimer;
    public int flashTimeout;
    
    public boolean hasScreen;
    
    public boolean primaryKeyDown;
    public boolean secondaryKeyDown;
    
    public long clock;
    
    public static final ResourceLocation texCamTopLeft 		= new ResourceLocation("photoreal", "textures/camera/topleft.png");
    public static final ResourceLocation texCamTop 			= new ResourceLocation("photoreal", "textures/camera/top.png");
    public static final ResourceLocation texCamTopRight 	= new ResourceLocation("photoreal", "textures/camera/topright.png");
    public static final ResourceLocation texCamRight 		= new ResourceLocation("photoreal", "textures/camera/right.png");
    public static final ResourceLocation texCamBottomRight 	= new ResourceLocation("photoreal", "textures/camera/bottomright.png");
    public static final ResourceLocation texCamBottom 		= new ResourceLocation("photoreal", "textures/camera/bottom.png");
    public static final ResourceLocation texCamBottomLeft 	= new ResourceLocation("photoreal", "textures/camera/bottomleft.png");
    public static final ResourceLocation texCamLeft 		= new ResourceLocation("photoreal", "textures/camera/left.png");
    public static final ResourceLocation texCamCentral 		= new ResourceLocation("photoreal", "textures/camera/central.png");
    public static final ResourceLocation texCamVignette 	= new ResourceLocation("photoreal", "textures/camera/vignette.png");
    public static final ResourceLocation texCamFlash	 	= new ResourceLocation("photoreal", "textures/camera/flash.png");
    
    public int screenWidth = Minecraft.getMinecraft().displayWidth;
    public int screenHeight = Minecraft.getMinecraft().displayHeight;
    
    public TextureRender cameraPoV;
    
    public ArrayList<EntityPhotoreal> pendingRenders = new ArrayList<EntityPhotoreal>();
}

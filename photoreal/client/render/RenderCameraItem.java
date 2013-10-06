package photoreal.client.render;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import photoreal.client.model.ModelCamera;


public class RenderCameraItem implements IItemRenderer 
{

	private static final ResourceLocation texCamera = new ResourceLocation("photoreal", "textures/model/camera.png");
	
	private ModelCamera modelCamera;
	private Random rand;
	
	public RenderCameraItem()
	{
		modelCamera = new ModelCamera();
		rand = new Random();
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) 
	{
		switch(type)
		{
		case ENTITY:
		case EQUIPPED_FIRST_PERSON:
		case EQUIPPED:
		case INVENTORY: return true;
		default: return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) 
	{
		switch(helper)
		{
		case ENTITY_ROTATION:
		case ENTITY_BOBBING: return true;
		default: return false;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
	{
		int renderType = 0;
		switch(type)
		{
			case ENTITY:
			{
				renderType = 2;
			}
			case INVENTORY:
			{
				data = new Object[] { data[0], null } ;
				if(renderType == 0)
				{
					renderType = 1;
				}
			}
			case EQUIPPED_FIRST_PERSON:
			case EQUIPPED:
			{
				boolean isFirstPerson = false;
				
				RenderBlocks renderBlocks = (RenderBlocks)data[0];
				
				if(data[1] instanceof EntityPlayer && ((EntityPlayer)data[1] == Minecraft.getMinecraft().renderViewEntity && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) && RenderManager.instance.playerViewY == 180.0F)))
				{
					isFirstPerson = true;
				}
//				if(data[1] instanceof EntityLiving)
//				{
//					item = ((EntityLiving)data[1]).getHeldItem();
//					if(item == null)
//					{
//						return;
//					}
//				}
				
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

				GL11.glPushMatrix();
				
				GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		        
		        GL11.glRotatef(-10F, 1.0F, 0.0F, 0.0F);
		        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		        GL11.glRotatef(40F, 0.0F, 0.0F, 1.0F);
		        
		        GL11.glTranslatef(-0.85F, 0.18F, 0.15F);
		        
		        if(isFirstPerson)
		        {
			        GL11.glRotatef(-10F, 0.0F, 0.0F, 1.0F);
			        GL11.glRotatef(20F, 0.0F, 1.0F, 0.0F);
			        
			        GL11.glTranslatef(0.06F, -0.1F, -0.15F);
		        }
		        else
		        {
		        }
		        //translate here;
		        
				float scale1 = isFirstPerson ? 1.5F : (renderType == 1 ? 23F : renderType == 2 ? 1.5F : 0.8F);
				if(renderType == 1)
				{
					GL11.glScalef(-scale1, -scale1, -scale1);
					
			        GL11.glRotatef(-160F, 0.0F, 1.0F, 0.0F);
			        GL11.glRotatef(40F, 0.0F, 0.0F, 1.0F);
			        GL11.glRotatef(-20F, 1.0F, 0.0F, 0.0F);
			        GL11.glRotatef(40F, 0.0F, 1.0F, 0.0F);

					GL11.glTranslatef(0.08F, 0.55F, -0.4F);
				}
				else
				{
					if(renderType == 2)
					{
						GL11.glTranslatef(0.2F, -0.5F, 0.0F);
					}
					GL11.glScalef(scale1, scale1, scale1);
				}
				
				if(isFirstPerson)
				{
					Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
					modelCamera.renderArms(0.0625F);
				}
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(texCamera);
				
				modelCamera.renderCamera(0.0625F, isFirstPerson);
				
				GL11.glDisable(GL11.GL_BLEND);
				
				if(renderType == 1)
				{
					GL11.glScalef(-scale1, -scale1, -scale1);
				}
				
				GL11.glPopMatrix();
				
				break;
			}
			default:{}
		}
	}
}

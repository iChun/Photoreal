package photoreal.client.model;

import org.lwjgl.opengl.GL11;

import photoreal.common.Photoreal;
import ichun.client.render.RendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class ModelCamera extends ModelBase
{
	public ModelRenderer objective;
	public ModelRenderer buttonRight;
	public ModelRenderer buttonLeft;
	public ModelRenderer connector;
	public ModelRenderer camera;
	public ModelRenderer leftArm;
	public ModelRenderer rightArm;

	public ModelCamera()
	{
		textureWidth = 64;
		textureHeight = 32;

		objective = new ModelRenderer(this, 3, 15);
		objective.addBox(-6F, -2.5F, -2F, 4, 4, 4);
		setRotation(objective, 0F, 0F, 0F);
		buttonRight = new ModelRenderer(this, 0, 12);
		buttonRight.addBox(-0.5F, -4F, 1F, 2, 1, 2);
		setRotation(buttonRight, 0F, 0F, 0F);
		buttonLeft = new ModelRenderer(this, 0, 12);
		buttonLeft.addBox(-0.5F, -4F, -3F, 2, 1, 2);
		setRotation(buttonLeft, 0F, 0F, 0F);
		connector = new ModelRenderer(this, 20, 0);
		connector.addBox(-2F, -2F, -1.5F, 1, 3, 3);
		setRotation(connector, 0F, 0F, 0F);
		camera = new ModelRenderer(this, 0, 0);
		camera.addBox(-1F, -3F, -3.5F, 3, 5, 7);
		setRotation(camera, 0F, 0F, 0F);
		leftArm = new ModelRenderer(this, 40, 16);
		leftArm.addBox(0F, 0F, -4F, 4, 12, 4);
		leftArm.setRotationPoint(-1.5F, 9.5F, -9F);
		leftArm.mirror = true;
		setRotation(leftArm, 2.944545F, 0F, 0F);
		rightArm = new ModelRenderer(this, 40, 16);
		rightArm.addBox(0F, 0F, 0F, 4, 12, 4);
		rightArm.setRotationPoint(-1.5F, 9.5F, 9F);
		setRotation(rightArm, -2.944546F, 0F, 0F);
	}

	public void renderArms(float f5)
	{
		leftArm.render(f5);
		rightArm.render(f5);
	}

	public void renderCamera(float f5, boolean isFirstPerson)
	{
		objective.render(f5);
		if(isFirstPerson)
		{
			buttonRight.render(f5);
		}
		else
		{
			buttonLeft.render(f5);
		}
		connector.render(f5);
		camera.render(f5);
		
		if(isFirstPerson && Photoreal.proxy.tickHandlerClient.cameraPoV != null)
		{
			double posX = 0.12725D;
			double posY = -0.16D;
			double posZ = -0.19D;
			
			double width = 0.38D;
			double height = 0.255D;
			
	        Tessellator tessellator = Tessellator.instance;
			
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3 fog = Minecraft.getMinecraft().theWorld.getFogColor(1.0F);
            GL11.glColor3f((float)fog.xCoord, (float)fog.yCoord, (float)fog.zCoord);
            tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(posX, posY + height	, posZ		  , 0.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY + height	, posZ + width, 1.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ + width, 1.0D, 1.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ		  , 0.0D, 1.0D);
            tessellator.draw();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, Photoreal.proxy.tickHandlerClient.cameraPoV.texture);
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(posX, posY + height	, posZ		  , 0.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY + height	, posZ + width, 1.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ + width, 1.0D, 1.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ		  , 0.0D, 1.0D);
	        tessellator.draw();
	        
	        int r = (0x00802A >> 16 & 0xff);
	        int g = (0x00802A >> 8 & 0xff);
	        int b = (0x00802A & 0xff);
	        
			GL11.glDisable(GL11.GL_TEXTURE_2D);
	        tessellator.startDrawingQuads();
	        tessellator.setColorRGBA(r, g, b, 45);
	        tessellator.addVertexWithUV(posX, posY + height	, posZ		  , 0.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY + height	, posZ + width, 1.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ + width, 1.0D, 1.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ		  , 0.0D, 1.0D);
	        tessellator.draw();
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        
			Minecraft.getMinecraft().getTextureManager().bindTexture(texCameraViewfinder);
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(posX, posY + height	, posZ		  , 0.0D, 1.0D);
	        tessellator.addVertexWithUV(posX, posY + height	, posZ + width, 1.0D, 1.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ + width, 1.0D, 0.0D);
	        tessellator.addVertexWithUV(posX, posY			, posZ		  , 0.0D, 0.0D);
	        tessellator.draw();
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	private static final ResourceLocation texCameraViewfinder = new ResourceLocation("photoreal", "textures/model/cameraViewfinder.png");
}

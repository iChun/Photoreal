package photoreal.client.render;

import ichun.common.core.util.ObfHelper;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import photoreal.common.Photoreal;
import photoreal.common.entity.EntityPhotoreal;

public class RenderPhotoreal extends Render
{

    public void renderPhotoreal(EntityPhotoreal photo, double d, double d1, double d2, float f, float f1)
    {
        if(photo.tex != null)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(photo.rendered)
            {
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPushMatrix();

                GL11.glTranslated(d, d1, d2);
                GL11.glRotatef(180F - photo.getDataWatcher().getWatchableObjectFloat(19), 0.0F, 1.0F, 0.0F); //yaw
                GL11.glRotatef(-photo.getDataWatcher().getWatchableObjectFloat(20), 1.0F, 0.0F, 0.0F); //pitch

                GL11.glTranslated(0.0D, 0.0D, -3.0D);

                //			double width = photo.getDataWatcher().getWatchableObjectInt(21);
                //			double height = photo.getDataWatcher().getWatchableObjectInt(22);

                double width = 860D;
                double height = 480D;

                //			System.out.println(width);
                //			System.out.println(height);

                double mag = 0.009D;

                width *= mag;
                height *= mag;

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                Tessellator tessellator = Tessellator.instance;

                GL11.glDisable(GL11.GL_TEXTURE_2D);

                float flashProg = (float)Math.pow((photo.flashTimeout - f1) / 3F, 2D);
                if(flashProg < 0.0F || photo.flashTimeout == 0)
                {
                    flashProg = 0.0F;
                }

                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(255, 255, 255, (int)(240 * flashProg));
                tessellator.addVertexWithUV(-(width / 2D) - 0.05D, (height / 2D) + 0.05D, -0.05D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(-(width / 2D) - 0.05D, -(height / 2D) - 0.05D, -0.05D, 0.0D, 0.0D);
                tessellator.addVertexWithUV((width / 2D) + 0.05D, -(height / 2D) - 0.05D, -0.05D, 1.0D, 0.0D);
                tessellator.addVertexWithUV((width / 2D) + 0.05D, (height / 2D) + 0.05D, -0.05D, 1.0D, 1.0D);
                tessellator.draw();

                if(photo.fogR == 1.0D && photo.fogG == 1.0D && photo.fogB == 1.0D)
                {
                    photo.fogR = mc.entityRenderer.fogColorRed;
                    photo.fogG = mc.entityRenderer.fogColorGreen;
                    photo.fogB = mc.entityRenderer.fogColorBlue;
                }
                GL11.glColor3f((float)photo.fogR, (float)photo.fogG, (float)photo.fogB);
                tessellator.startDrawingQuads();
                tessellator.addVertexWithUV(-(width / 2D), (height / 2D), 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(-(width / 2D), -(height / 2D), 0.0D, 0.0D, 0.0D);
                tessellator.addVertexWithUV((width / 2D), -(height / 2D), 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV((width / 2D), (height / 2D), 0.0D, 1.0D, 1.0D);
                tessellator.draw();
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_TEXTURE_2D);

                photo.tex.bindFramebufferTexture();

                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(255, 255, 255, 255);
                tessellator.addVertexWithUV(-(width / 2D), (height / 2D), 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(-(width / 2D), -(height / 2D), 0.0D, 0.0D, 0.0D);
                tessellator.addVertexWithUV((width / 2D), -(height / 2D), 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV((width / 2D), (height / 2D), 0.0D, 1.0D, 1.0D);
                tessellator.draw();

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);

                GL11.glPopMatrix();
            }
            else if(!Photoreal.proxy.tickHandlerClient.pendingRenders.contains(photo) && photo.getDataWatcher().getWatchableObjectInt(23) >= 0)
            {
                Photoreal.proxy.tickHandlerClient.pendingRenders.add(photo);
            }
        }
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
    {
        renderPhotoreal((EntityPhotoreal)entity, d0, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return AbstractClientPlayer.locationStevePng;
    }

}

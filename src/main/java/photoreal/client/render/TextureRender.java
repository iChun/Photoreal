package photoreal.client.render;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenRenderbuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glRenderbufferStorageEXT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.ARBFramebufferObject;

public class TextureRender
{
    //TODO move this into iChunUtil and update to follow MC 1.7's framebuffer
    public TextureRender()
    {
        fbo = glGenFramebuffersEXT();                                         // create a new framebuffer
        texture = glGenTextures();                                               // and a new texture used as a color buffer
        depth = glGenRenderbuffersEXT();                                  // a new depthbuffer
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);                        // switch to the new framebuffer
        // initialize color texture
        glBindTexture(GL_TEXTURE_2D, texture);                                   // Bind the colorbuffer texture
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);               // make it linear filterd
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null); // Create the texture data
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture, 0); // attach it to the framebuffer
        // initialize depth renderbuffer
        glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depth);                // bind the depth renderbuffer
        glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, ARBFramebufferObject.GL_DEPTH24_STENCIL8, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight); // get the data space for it
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, ARBFramebufferObject.GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER_EXT, depth); // bind it to the framebuffer
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void updateResolution(int width, int height)
    {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);                        // switch to the new framebuffer
        glBindTexture(GL_TEXTURE_2D, texture);                                   // Bind the colorbuffer texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null); // Create the texture data
        glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depth);                // bind the depth renderbuffer
        glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, ARBFramebufferObject.GL_DEPTH24_STENCIL8, width, height); // get the data space for it
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public int texture;
    public int fbo;
    public int depth;
}

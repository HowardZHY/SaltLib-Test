package me.Deex.SaltLib.Mixin;

import java.nio.ByteBuffer;
import java.util.List;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Mixin(BufferRenderer.class)
public class BufferRendererMixin 
{
    @Overwrite
    public void draw(BufferBuilder builder) 
    {
        if (builder.getVertexCount() > 0) 
        {
            int l;
            int j;
            VertexFormat vertexFormat = builder.method_9754();
            int stride = vertexFormat.method_10343();
            ByteBuffer byteBuffer = builder.getByteBuffer();
            List<VertexFormatElement> list = vertexFormat.getElements();

            block12: for (j = 0; j < list.size(); ++j) 
            {
                VertexFormatElement vertexFormatElement = list.get(j);
                VertexFormatElement.Type type = vertexFormatElement.getType();
                int k = vertexFormatElement.getFormat().getSize();
                l = vertexFormatElement.getIndex();
                byteBuffer.position(vertexFormat.method_10340(j));

                switch (type) 
                {
                    case POSITION: 
                    {
                        int startPos = byteBuffer.position();

                        //Will move back over to GPU once shaders will get implemented
                        for (int i = 0; i < builder.getVertexCount(); i++)
                        {
                            byteBuffer.position(i * stride);
                            float x = byteBuffer.getFloat();
                            float y = byteBuffer.getFloat();
                            float z = byteBuffer.getFloat();
                            
                            byteBuffer.position(i * stride);
                            byteBuffer.putFloat(x);
                            byteBuffer.putFloat(y);
                            byteBuffer.putFloat(z);
                        }

                        byteBuffer.position(startPos);
                        GL11.glVertexPointer(vertexFormatElement.getCount(), k, stride, byteBuffer);
                        GL11.glEnableClientState(32884);
                        continue block12;
                    }
                    case UV: 
                    {
                        GLX.gl13ClientActiveTexture(GLX.textureUnit + l);
                        GL11.glTexCoordPointer(vertexFormatElement.getCount(), k, stride, byteBuffer);
                        GL11.glEnableClientState(32888);
                        GLX.gl13ClientActiveTexture(GLX.textureUnit);
                        continue block12;
                    }
                    case COLOR: 
                    {
                        GL11.glColorPointer(vertexFormatElement.getCount(), k, stride, byteBuffer);
                        GL11.glEnableClientState(32886);
                        continue block12;
                    }
                    case NORMAL: 
                    {
                        GL11.glNormalPointer(k, stride, byteBuffer);
                        GL11.glEnableClientState(32885);
                    }
                }
            }

            GL11.glDrawArrays(builder.method_9756(), 0, builder.getVertexCount());
            int m = list.size();

            block13: for (j = 0; j < m; ++j) 
            {
                VertexFormatElement vertexFormatElement2 = list.get(j);
                VertexFormatElement.Type type2 = vertexFormatElement2.getType();
                l = vertexFormatElement2.getIndex();

                switch (type2) 
                {
                    case POSITION: 
                    {
                        GL11.glDisableClientState(32884);
                        continue block13;
                    }
                    case UV: 
                    {
                        GLX.gl13ClientActiveTexture(GLX.textureUnit + l);
                        GL11.glDisableClientState(32888);
                        GLX.gl13ClientActiveTexture(GLX.textureUnit);
                        continue block13;
                    }
                    case COLOR: 
                    {
                        GL11.glDisableClientState(32886);
                        GlStateManager.clearColor();
                        continue block13;
                    }
                    case NORMAL: 
                    {
                        GL11.glDisableClientState(32885);
                    }
                }
            }
        }
        
        builder.reset();
    }
}

package com.dimension.a3dimension.graphics.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;


public class TexturedSurfaceShape {
    private FloatBuffer colorBuffer;
    Bitmap mTexture;
    private FloatBuffer texBuffer;
    private FloatBuffer vertexBuffer;
    float[] colors = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    float[] texCoords = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    int[] textureIDs = new int[1];

    public TexturedSurfaceShape(float[] fArr, Bitmap bitmap) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        this.vertexBuffer = asFloatBuffer;
        asFloatBuffer.put(fArr);
        this.vertexBuffer.position(0);
        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(this.colors.length * 4);
        allocateDirect2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
        this.colorBuffer = asFloatBuffer2;
        asFloatBuffer2.put(this.colors);
        this.colorBuffer.position(0);
        this.mTexture = bitmap;
        ByteBuffer allocateDirect3 = ByteBuffer.allocateDirect(this.texCoords.length * 4);
        allocateDirect3.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer3 = allocateDirect3.asFloatBuffer();
        this.texBuffer = asFloatBuffer3;
        asFloatBuffer3.put(this.texCoords);
        this.texBuffer.position(0);
    }

    public void draw(GL10 gl10) {
        gl10.glFrontFace(2305);
        gl10.glEnable(2884);
        gl10.glCullFace(1029);
        gl10.glEnableClientState(32888);
        gl10.glTexCoordPointer(2, 5126, 0, this.texBuffer);
        gl10.glEnableClientState(32884);
        gl10.glVertexPointer(3, 5126, 0, this.vertexBuffer);
        gl10.glEnableClientState(32886);
        for (int i = 0; i < 6; i++) {
            gl10.glColorPointer(4, 5126, 0, this.colorBuffer);
            gl10.glDrawArrays(5, i * 4, 4);
        }
        gl10.glDisableClientState(32884);
        gl10.glDisable(2884);
        gl10.glDisableClientState(32888);
        gl10.glDisableClientState(32886);
    }

    public void loadTexture(GL10 gl10, Context context) {
        gl10.glDeleteTextures(1, this.textureIDs, 0);
        gl10.glGenTextures(1, this.textureIDs, 0);
        gl10.glBindTexture(3553, this.textureIDs[0]);
        gl10.glTexParameterf(3553, 10241, 9728.0f);
        gl10.glTexParameterf(3553, 10240, 9729.0f);
        GLUtils.texImage2D(3553, 0, this.mTexture, 0);
    }
}

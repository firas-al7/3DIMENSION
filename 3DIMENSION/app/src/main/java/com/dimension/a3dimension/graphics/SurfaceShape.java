package com.dimension.a3dimension.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;


public class SurfaceShape {
    private FloatBuffer colorBuffer;
    private FloatBuffer vertexBuffer;

    public SurfaceShape(float[] fArr, float[] fArr2) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        this.vertexBuffer = asFloatBuffer;
        asFloatBuffer.put(fArr);
        this.vertexBuffer.position(0);
        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(fArr2.length * 4);
        allocateDirect2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
        this.colorBuffer = asFloatBuffer2;
        asFloatBuffer2.put(fArr2);
        this.colorBuffer.position(0);
    }

    public void draw(GL10 gl10) {
        gl10.glFrontFace(2305);
        gl10.glEnable(2884);
        gl10.glCullFace(1029);
        gl10.glEnableClientState(32884);
        gl10.glVertexPointer(3, 5126, 0, this.vertexBuffer);
        gl10.glEnableClientState(32886);
        for (int i = 0; i < 6; i++) {
            gl10.glColorPointer(4, 5126, 0, this.colorBuffer);
            gl10.glDrawArrays(5, i * 4, 4);
        }
        gl10.glDisableClientState(32884);
        gl10.glDisable(2884);
        gl10.glDisableClientState(32886);
    }
}

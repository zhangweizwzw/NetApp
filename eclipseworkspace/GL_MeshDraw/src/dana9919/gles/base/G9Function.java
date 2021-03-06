/*
 * 一些个有用的算法，喵的，GL20太白痴了，连normalize vector的方法都没有
 * */

package dana9919.gles.base;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * 作用: 算法支撑,GLES的有些个基础的算法都没有提供,让人很崩溃
 * @author 徐潇
 * mail: dana9919@163.com
 * QQ 61092517
 */
public final class G9Function {
	/**
	 * 用于向量的规格化，注意只支持float[3]，在内部扔出异常 没扔到外部去了 所以DEBUG时要注意加断点
	 * */
	public static void glV3Normalize(float[] vecInOut){
		int iSize =vecInOut.length; 
		if(iSize !=3)
		{
			throw new RuntimeException("err in glV3Normalize");//	
		}
		float fScale = (float)Math.sqrt(vecInOut[0]*vecInOut[0]+vecInOut[1]*vecInOut[1]+vecInOut[2]*vecInOut[2]);
		if(fScale == 0)
			return;
		fScale = 1/fScale;//喵的,除法会不会很费时?
		for(int i=0;i<iSize; i++)
		{
			vecInOut[i] *= fScale;
		}
		return;		
	
	}//ef
	/**
	 * 叉积函数,进函数前参数请注意normalize(),只支持3元向量
	 * */
	public static void glV3Cross(float[] v3OUT,float[] v3A,float[] v3B){
		//参数检测
		if((v3OUT.length !=3)||(v3A.length !=3)||v3B.length!=3){

			throw new RuntimeException("err in glV3Cross");
		}
		/*
		v3OUT[0] =  v3B[1]*v3A[2]-v3A[1]*v3B[2];
		v3OUT[1] =  v3B[2]*v3A[0]-v3A[2]*v3B[0];
		v3OUT[2] =  v3B[0]*v3A[1]-v3A[0]*v3B[1];
		*/
		v3OUT[0] =  v3A[1]*v3B[2]-v3B[1]*v3A[2];
		v3OUT[1] =  v3A[2]*v3B[0]-v3B[2]*v3A[0];
		v3OUT[2] =  v3A[0]*v3B[1]-v3B[0]*v3A[1];
		return;
	}//ef
	/**
	 * 点积函数,进函数前注意normalize() 只支持3元向量
	 */
	public static float glV3Dot(float[] v3A,float[] v3B){
		if((v3A.length !=3)||v3B.length!=3){
			
			throw new RuntimeException("err in glV3Dot");
		}
		return v3A[0]*v3B[0] + v3A[1]*v3B[1] + v3A[2]*v3B[2];
		
	}//ef
	/**
	 * 三元向量相加 (参数必须normalize后才有意义,即 v3Dst是后两个向量的中间插值)
	 * @param v3Dst 输出
	 * @param v3LParam 左参
	 * @param v3RParam 右参
	 */
	public static void glV3Add(float[] v3Dst,float[] v3LParam,float[] v3RParam){
		if((v3Dst.length !=3 )||(v3LParam.length != 3)|| (v3RParam.length!=3))
			throw new RuntimeException("err in glV3Add");
		for(int i=0; i<3; i++){
			v3Dst[i] = v3LParam[i]+v3RParam[i];
		}
	}//ef
	/**
	 * 三元量(位置v3Position)相减
	 * 		注意,作为位置信息的参数不应该规格化
	 * @param v3Dst 输出,即v3R点到v3L点的向量
	 * @param v3LParam 左参
	 * @param v3RParam 右参
	 */
	public static void glV3Subtract(float[] v3Dst,float[] v3LParam,float[] v3RParam){
		if((v3Dst.length !=3 )||(v3LParam.length != 3)|| (v3RParam.length!=3))
			throw new RuntimeException("err in glV3Add");
		for(int i=0; i<3; i++){
			v3Dst[i] = v3LParam[i]-v3RParam[i];
		}
	}//ef
	/**
	 * 3元向量变换，mxXform是变换矩阵 注意会改变参数的3元量!! 
	 * */
	public static void glV3Transform(float[] v3InOut,float[] mxXform)
	{
		if((v3InOut.length !=3)||(mxXform.length !=16))
			throw new RuntimeException("err in glV3Transform");
		float f0,f1,f2;
		/*
		f0 = v3InOut[0]*mxXform[0] + v3InOut[1]*mxXform[1] + 
				v3InOut[2]*mxXform[2]+v3InOut[3]*mxXform[3];
		f1 = v3InOut[0]*mxXform[4] + v3InOut[1]*mxXform[5] +
				v3InOut[2]*mxXform[6]+v3InOut[3]*mxXform[7];
		f2 = v3InOut[0]*mxXform[8] + v3InOut[1]*mxXform[9] + 
				v3InOut[2]*mxXform[10]+v3InOut[3]*mxXform[11];
		*/
		f0 = v3InOut[0]*mxXform[0] + v3InOut[1]*mxXform[4] +v3InOut[2]*mxXform[8] + 1*mxXform[12]; 
				
		f1 =  v3InOut[0]*mxXform[1] + v3InOut[1]*mxXform[5] +v3InOut[2]*mxXform[9] + 1*mxXform[13];
		
		f2 =  v3InOut[0]*mxXform[2] + v3InOut[1]*mxXform[6] +v3InOut[2]*mxXform[10] + 1*mxXform[14];
		v3InOut[0] = f0;
		v3InOut[1] = f1;
		v3InOut[2] = f2;
		return;
		
	}//ef
	/**
	 * 4元向量变换，mxXform是变换矩阵 注意会改变参数的3元量
	 * */
	public static void glV4Transform(float[] v4InOut,float[] mxXform)
	{
		if((v4InOut.length !=4)||(mxXform.length !=16))
			throw new RuntimeException("err in glV3Transform");
		float f0,f1,f2,f3;
		f0 = v4InOut[0]*mxXform[0] + v4InOut[1]*mxXform[1] + 
				v4InOut[2]*mxXform[2]+v4InOut[3]*mxXform[3];
		f1 = v4InOut[0]*mxXform[4] + v4InOut[1]*mxXform[5] +
				v4InOut[2]*mxXform[6]+v4InOut[3]*mxXform[7];
		f2 = v4InOut[0]*mxXform[8] + v4InOut[1]*mxXform[9] + 
				v4InOut[2]*mxXform[10]+v4InOut[3]*mxXform[11];
		f3 = v4InOut[0]*mxXform[12] + v4InOut[1]*mxXform[13] + 
				v4InOut[2]*mxXform[14]+v4InOut[3]*mxXform[15];
		v4InOut[0] = f0;
		v4InOut[1] = f1;
		v4InOut[2] = f2;
		v4InOut[3] = f3;
		return;
		
	}//ef
	/**	 
	 * 任意轴旋转  mxOut为传出矩阵  fAngle为角度, v3axis喵的,是中轴
	 * 测试通过,但与D3D里的同价函数比较后,两个出的矩阵是完全相反的,应该是左右手不同造成的
	 *与GL相关旋转函数比较后一致
	 */
	public static void glRotateAxis(float[] mxOut,float fAngle,float[] v3Axis){
		//先检错		
		if((mxOut.length !=16)||(v3Axis.length!=3))
		{

			throw new RuntimeException("err in glRotateAxis");
		}
		//不改变v3Axis 复制一个后去规格化
		float[] v3a = glV3Copy(v3Axis);
		glV3Normalize(v3a);
		// ux v3a[0] ;uy = v3a[1] ; uz = v3a[2]; 
		float s = (float)Math.sin(Math.toRadians(fAngle));//转角sin值
		float c = (float)Math.cos(Math.toRadians(fAngle));//转角cos值
		//还是按书里的矩阵推导来填 
	
		mxOut[0] = c+(1-c)*v3a[0]*v3a[0];
		mxOut[1] = (1-c)*v3a[0]*v3a[1] + s*v3a[2];
		mxOut[2] = (1-c)*v3a[0]*v3a[2] -s*v3a[1];
		mxOut[3] = 0;
		
		mxOut[4] = (1-c)*v3a[1]*v3a[0] - s*v3a[2];
		mxOut[5] = c+(1-c)*v3a[1]*v3a[1];
		mxOut[6] = (1-c)*v3a[1]*v3a[2]+s*v3a[0];
		mxOut[7] = 0;
		
		mxOut[8] = (1-c)*v3a[2]*v3a[0] + s*v3a[1];
		mxOut[9] = (1-c)*v3a[2]*v3a[1] - s*v3a[0];
		mxOut[10] = c+(1-c)*v3a[2]*v3a[2];
		mxOut[11] = 0;
		
		mxOut[12] = 0;
		mxOut[13] = 0;
		mxOut[14] = 0;
		mxOut[15] = 1.0f;
		
		
	}//ef
	/**
	 * 内部方法]] 考贝一份float[3]的 vector
	 * 注意参数 
	 */
	public static float[] glV3Copy(float[] v3Source){
		
		if(v3Source.length!=3)
		{
			throw new  RuntimeException("err in glVec3Copy");
		}
		float[] v3Out = new float[3];
		for(int i=0; i<3; i++){
			v3Out[i] = v3Source[i];
		}
		return v3Out;
	}//ef
	/**
	 * float数组考贝,从src 到dst
	 * @param arrDST 目标 
	 * @param arrSRC 源
	 * @return 数组长度
	 */
	public static int glFArrayCopy(float[] arrDST,float[] arrSRC){
		int iSize = arrDST.length;
		if(arrSRC.length != iSize)
			throw new RuntimeException("err in glFArrayCopy");
		for(int i=0 ; i< iSize; i++){
			arrDST[i] = arrSRC[i];
		}
		return iSize;
	}//ef
	/**
	 * 将一组FLOAT按序压入一个float buffer ，由于GL要求float buffer 建在系统内存中
	 * 即allocateDirect()特作此函数
	 * @param data 要压入buffer人数据
	 * @return 由些函数建立的 floatbuffer
	 */
	public static FloatBuffer PushIntoFLoatBuffer(float[] data){
		FloatBuffer fb = null;
		ByteBuffer bb = ByteBuffer.allocateDirect(data.length *4);
		bb.order(ByteOrder.nativeOrder());
		fb = bb.asFloatBuffer();
		fb.put(data);
		fb.position(0);
		return fb;
	}//ef
	
	public static FloatBuffer PushIntoFLoatBuffer(ArrayList<Float> arrData){
		//要先做成 数组，再压到BUFFER里去 所以会出现一个无句柄的中间量，
		int iSize = arrData.size();
		float[] data = new float[iSize];
		for(int i=0; i<iSize; i++){
			data[i] = arrData.get(i);
		}
		
		FloatBuffer fb = null;
		ByteBuffer bb = ByteBuffer.allocateDirect(data.length *4);
		bb.order(ByteOrder.nativeOrder());
		fb = bb.asFloatBuffer();
		fb.put(data);
		fb.position(0);
		return fb;
	}//ef
	/**
	 * 将一组INT压入INT BUFFER，使用 allocateDirect() 及 nativeOrder将BUFFER建在系统内存
	 * 因为，GLES只认识系统native的内存
	 */
	public static IntBuffer PushIntoIntBuffer(int[] data){
		IntBuffer ib = null;
		ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);//4 = Integer.SIZE/8  
		bb.order(ByteOrder.nativeOrder());
		ib = bb.asIntBuffer();
		ib.put(data);
		ib.position(0);
		return ib;		
	}//ef
	/**
	 * 通过资源图片创建纹理 返回纹理id
	 * @param pthis 暂定为activity的context
	 * @param IdDrawable 图片的id （放在drawable内）
	 * @return 纹理ID
	 */
	public static int CreateTexture(Resources resource,int IdDrawable){
		int[] arrTexID = new int[1];
		GLES20.glGenTextures(1, arrTexID, 0);		
		int IdTexOut = arrTexID[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, IdTexOut);//将ID绑定到一个纹理(现为空)
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
		//接下去通过流来加载图片  这里注意不一定要 stream 也可以直接 decodeResource 或文件 
		InputStream is = resource.openRawResource(IdDrawable);
		
		Bitmap bitmapTemp = null;
		try{
			bitmapTemp = BitmapFactory.decodeStream(is);
		}
		catch (Exception e) {
			// 
			e.printStackTrace();
		}
		finally{
			try {
				is.close();
			} catch (IOException e) {
				Log.e("ERR", e.toString());
			}
		}
		//再实际地加载图片
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTemp, 0);
		//提醒注意 别忘记干掉bitmapTemp 已经在显存了，再次加载只要id就行，所以bitmaptemp没用了
		bitmapTemp.recycle();
		//返回
		return IdTexOut;
		
	}//ef
	/**
	 * 通过资源图片创建纹理 返回纹理id
	 * @param resource 系统资源
	 * @param strTexFileName 文件名(尺寸不用我提醒了吧,必须放在系统 assets档下)
	 * @return 纹理ID
	 */
	public static int CreateTexture(Resources resource, String strTexFileName)   {
		// 从资源assets中加载纹理 
		int[] arrTexID = new int[1];
		GLES20.glGenTextures(1, arrTexID, 0);
		
		int IdTexOut = arrTexID[0];		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, IdTexOut);//将ID绑定到一个纹理(现为空)
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
		//从ASSETS私有文件中读取
		Bitmap bitmapTemp = null;
		InputStream ins = null;
		
		
		try {
			ins = resource.getAssets().open(strTexFileName);
			bitmapTemp = BitmapFactory.decodeStream(ins);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				ins.close();
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
		}
		
		
		if(bitmapTemp == null)
			return -1;
		
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmapTemp,0);//TNN的这种操作居然没有THROW的,GL个狗屁			
		bitmapTemp.recycle();
		
		int iERR = GLES20.glGetError();
		if(iERR !=0)
			Log.v("","");
		
		return IdTexOut;
	}//ef
	
}//EC

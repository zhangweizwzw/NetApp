package com.example.objLoader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import dana9919.gles.base.G9ENUM;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.base.SubInfo;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.Debug;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MyRenderer extends GLSurfaceView implements Renderer {
	
	/** Triangle instance */
	private OBJParser parser;
	private TDModel model;
	
	/* Rotation values */
	private float xrot;					//X Rotation
	private float yrot;					//Y Rotation

	/* Rotation speed values */
	
	private float xspeed;				//X Rotation Speed ( NEW )
	private float yspeed;				//Y Rotation Speed ( NEW )
	
	private float z = 50.0f;
	
	private float oldX;
    private float oldY;
	private final float TOUCH_SCALE = 0.4f;		//Proved to be good for normal rotation ( NEW )
	
	private float[] lightAmbient = {1.0f, 1.0f, 1.0f, 1.0f};
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	private float[] lightPosition = {0.0f, -3.0f, 2.0f, 1.0f};
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightPositionBuffer;

	public MyRenderer(Context ctx) {
		super(ctx);
		
		parser=new OBJParser(ctx);
		
//		String path = "file:///android_asset/test.obj";  
		model=parser.parseOBJ("/sdcard/cube.obj");
//		model=parser.parseOBJ("/storage/emulated/0/cube.obj");
		
//			String file= String.valueOf(getClass().getClassLoader().getResourceAsStream("assets/test.obj"));
		model=parser.parseOBJ("assets/smiley/test.obj");
		this.setRenderer(this);
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
	}
	
	
	public String getStringFromAssert(String fileName){ 
        String content=null; //结果字符串 
        try{ 
            InputStream is=this.getResources().getAssets().open(fileName); //打开文件 
            int ch=0; 
            ByteArrayOutputStream out = new  ByteArrayOutputStream(); //实现了一个输出流 
            while((ch=is.read())!=-1){ 
               out.write(ch); //将指定的字节写入此 byte 数组输出流 
            byte[] buff=out.toByteArray();//以 byte 数组的形式返回此输出流的当前内容 
            out.close(); //关闭流 
            is.close(); //关闭流 
            content=new String(buff,"UTF-8"); //设置字符串编码 
            }
         }catch(Exception e){ 
//             Toast.makeText(this, "对不起，没有找到指定文件！", Toast.LENGTH_SHORT).show(); 
         } 
        return content; 
  }
	
	public G9Mesh(Resources resource,String strObjFileName) {
		// 按文件名生成网格，并填充至 _info 这个函数会很长 并且原则上不应分成小函数
		ArrayList<Float> arr_v = new ArrayList<Float>();//用于临存 pos 索引元
		ArrayList<Float> arr_vt = new ArrayList<Float>();//临存 texcoord 索引元
		ArrayList<Float> arr_vn = new ArrayList<Float>();//临存 normal 索引元
		try{
			InputStream ins = resource.getAssets().open(strObjFileName) ;//喵的，只提供inputstream的返回
			InputStreamReader inReader = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(inReader);
			String strTemp = null;//临存每行字符			
			
			SubInfo  curSub = null;//当前操作的SubInfo 用于填充临存数据;
			_info = new MeshInfo();
			
			while((strTemp = br.readLine())!=null)//喵，空行也不为null???
			{
				String[] arrStr = strTemp.split("[ ]+");//按空格分割
				if(arrStr[0].trim().equals("o"))//取文件名
				{
					_info.strMeshName = arrStr[1];
				}else if(arrStr[0].trim().equals("mtllib") )//材质所在文件
				{
					//打开mtl文件,初始图片(时机在sufaceview建立之后),并返回subset数量
					String strMtlFilename = arrStr[1]; 
					_info.iSubsets = SetupMaterial(resource,strMtlFilename);
					
				}else if(arrStr[0].trim().equals( "v"))//POS数据
				{
					arr_v.add(Float.parseFloat(arrStr[1]));
					arr_v.add(Float.parseFloat(arrStr[2]));
					arr_v.add(Float.parseFloat(arrStr[3]));
					
				}else if(arrStr[0] .trim().equals("vt"))//texcoord数据
				{
					arr_vt.add(Float.parseFloat(arrStr[1]));
					arr_vt.add(1.0f-Float.parseFloat(arrStr[2]));
					
				}else if(arrStr[0] .trim().equals( "vn"))//normal 数据
				{
					arr_vn.add(Float.parseFloat(arrStr[1]));
					arr_vn.add(Float.parseFloat(arrStr[2]));
					arr_vn.add(Float.parseFloat(arrStr[3]));
				}/////////////接下去读取面信息并填充到 SubTemp结构
				else if(arrStr[0] .trim().equals( "usemtl"))//以下的面属于哪个SUBSET
				{
					curSub = _info.GetSubset(arrStr[1]);//变更当前操作的subinfo
				}				
				else if(arrStr[0] .trim().equals( "f"))
				{
					//首先这个时候 curSub应不为空 暂时省去检测了
					String[] arrVertex = null;
					int index = -1;					 
					for(int i =1; i<=3; i++)
					{
						arrVertex = arrStr[i].split("/");//本面第 i 个点的信息
						index= Integer.parseInt(arrVertex[0])-1;//POS的索引
						curSub.arrV.add(arr_v.get(3*index+0));
						curSub.arrV.add(arr_v.get(3*index+1));
						curSub.arrV.add(arr_v.get(3*index+2));
						
						index = Integer.parseInt(arrVertex[1])-1;//UV的索引
						curSub.arrVT.add(arr_vt.get(2*index));
						curSub.arrVT.add(arr_vt.get(2*index+1));
						
						
						index = Integer.parseInt(arrVertex[2])-1;//NORMAL 的索引
						curSub.arrVN.add(arr_vn.get(3*index));
						curSub.arrVN.add(arr_vn.get(3*index+1));
						curSub.arrVN.add(arr_vn.get(3*index+2));
					}//end for					
				}//"f"开头段结束				
			}//end while			
			//调用MeshInfo的自操作函数 CommitMeshInfo()来进行最后的调拨
			_info.CommitMeshInfo();
		}catch (Exception e) {
			// 
			e.printStackTrace();
		}//end try
		//初始RENDER_TECH 以phong技术打底
		_info.enumRENDER_TECH = G9ENUM.RENDER_TECH_G9PHONG;
		
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);	
		gl.glEnable(GL10.GL_LIGHT0);
									
		
		gl.glShadeModel(GL10.GL_SMOOTH); 			
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	
		gl.glClearDepthf(1.0f); 					
		gl.glEnable(GL10.GL_DEPTH_TEST); 			
		gl.glDepthFunc(GL10.GL_LEQUAL); 		
	
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glTranslatef(0.0f, -1.2f, -z);	//Move down 1.2 Unit And Into The Screen 6.0
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);	//X
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);	//Y
		model.draw(gl);						//Draw the square
		gl.glLoadIdentity();
		
		xrot += xspeed;
		yrot += yspeed;
		
	}

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 500.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//
		float x = event.getX();
        float y = event.getY();
        
        //If a touch is moved on the screen
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
        	//Calculate the change
        	float dx = x - oldX;
	        float dy = y - oldY;
        	//Define an upper area of 10% on the screen
        	int upperArea = this.getHeight() / 10;
        	
        	//Zoom in/out if the touch move has been made in the upper
        	if(y < upperArea) {
        		z -= dx * TOUCH_SCALE / 2;
        	
        	//Rotate around the axis otherwise
        	} else {        		
    	        xrot += dy * TOUCH_SCALE;
    	        yrot += dx * TOUCH_SCALE;
        	}        
        
        //A press on the screen
        } else if(event.getAction() == MotionEvent.ACTION_UP) {


        }
        
        //Remember the values
        oldX = x;
        oldY = y;
        
        //We handled the event
		return true;
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//
		if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			z -= 3;
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			z += 3;
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

		}

		//We handled the event
		return true;
	}
}
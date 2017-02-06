package dana9919.apk.gl_meshdraw;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import dana9919.gles.G9Camera;
import dana9919.gles.G9Mesh;
import dana9919.gles.G9Model;
import dana9919.gles.G9Render;
import dana9919.gles.ModelCollection;
import dana9919.gles.base.G9ENUM;

import dana9919.gles.base.G9GameView;

/** 
 * @author ����
 * mail: dana9919@163.com
 * QQ:61092517
 * Class name MyGame0824 ������ GlSurfaceView ������3D����,�����,��Ⱦ�����г�ʼ��
 * 	:���������InitModel�� InitCamera�����ĵ��ñȽ�����,����Render::onSurfaceCreated()������е�,
 * ��Ȼ����ԭ���,����Ĺ�����������ߴ�ȡ�õĲ��������� glSrufaceView����֮�� 
 *  :�����������߼��ϵ�����,��ܹ����ʱ���׼������Ⱦ��G9Render ����ͨ�õ�,�����Render����дInitModel InitCamera
 *  �Ͳ�����ʲôͨ������,���Թ����� G9GameView,��glSurfaceView�����ϼ�������abstract����,
 *  ��Ҫ��G9Render����ʱ���봫��һ�� G9GameView���� 
 *  ��Ȼ�߼�������һȦ,����������� ��֤��dana9919.gles ���Ķ�����*  
 * 
 */
public class MyGame0824 extends G9GameView{

	//------------------------------------------------����
	static final float ONE_DEGREE = 1.0f/180.0f;
	//------------------------------------------------Ԫ��
	G9Camera viewCamera = null;//�����ڻص�������
	G9Camera lightCamera = null;//�����ڻص�������
	G9Render render = null;	
	ModelCollection mc = null;
	//�����߳�
	TestThread td;
	float fXScreenPre = 0;
	float fYScreenPre = 0;
	//------------------------------------------------����
	public MyGame0824(Context context) {
		super(context);
		render = new G9Render(this);//����ֵ��ȥû��,RENDER�ﱣ����ǿ�ָ������
		setRenderer(render);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}//ef constructor

	//------------------------------------------------����
	@Override
	public void InitModel() {
		// ����ģ��
		mc = new ModelCollection();
		G9Mesh mesh = new G9Mesh(this.getResources(), "test.obj");
		mesh.SetRenderTech(G9ENUM.RENDER_TECH_G9PHONG);
		G9Model model = new G9Model(mesh);
		
		mc.AddModel(model);
		//�� mc װ��render��ȥ
		render.SetModeCollection(mc);
		Log.d("test", "breakPoint");
		
	}//ef
	@Override
	public void InitCamera(int iWinWidth, int iWinHeight) {
		// ��ʼ�������
		viewCamera = new G9Camera(iWinWidth, iWinHeight, 5, 0.8f);
		lightCamera = new G9Camera(100, 100, 100.0f, 0.5f);
		
		viewCamera.SetPosition(new float[]{0,0,1.1f});
		viewCamera.SetLookTarget(new float[]{0,0,0});
		
		lightCamera.SetPosition(new float[]{2,5,0});
		lightCamera.SetLookTarget(new float[]{0,0,0});
		render.SetCameras(viewCamera, lightCamera);
		
		//��һ�λ���
		//����thread��ʼҪ��һ��CAMERA��ȥ�����Ҳ��ܴ���ֵ��ȥ,����ֻ���������
		//����C++ָ��ָ���ָ��
		td = new TestThread(viewCamera);
		td.start();
		
	}//ef
	
	//------------------------------------------------����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			fXScreenPre = event.getX();
			fYScreenPre = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float fXNow = event.getX();
			float fYNow = event.getY();
			float fXDelta = fXNow - fXScreenPre;
			float fYDelta = fYNow - fYScreenPre;
			fXScreenPre = fXNow;
			fYScreenPre = fYNow;
			td.fXRotate = -fXDelta*0.5f;
			td.fYRotate = -fYDelta*0.5f;
			break;
			
		default:
			break;
		}
		
		return true;
	}
	//------------------------------------------------����

	

	 
		
	
}//EC

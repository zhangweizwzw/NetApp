package dana9919.gles;


import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import dana9919.gles.base.G9ENUM;
import dana9919.gles.base.G9GameView;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.effect.Effect_g9phong;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

/**
 * ��Ⱦ�� �Ǳ��������Ҫ��һ��
 * @author ����
 * mail: dana9919@163.com
 * QQ 61092517
 * 2012-8-26 ��ʱֻ��һ�� phongЧ������ɫ��,��ѧ���������
 */
 
public class G9Render implements GLSurfaceView.Renderer{
	//------------------------------------------------����
	//------------------------------------------------Ԫ��
	protected Effect_g9phong _eff_g9phong = null;//��ʼʱ����Ϊ�գ��ڽ�����ֵ
	
	protected ModelCollection _mc;//ģ�Ͷ���ָ��
	G9Camera _vCamera;//�������ָ��
	G9Camera _lCamera;//�������ָ��
	G9GameView _sv;//���������� 
	
	//------------------------------------------------����
	public G9Render(G9GameView sv) {	
		_sv = sv;
	}
	//------------------------------------------------����
	protected void InitShader(){
		_eff_g9phong = new Effect_g9phong(_sv.getResources());
				
			
	}//ef
	public void SetModeCollection(ModelCollection mc){
		_mc = mc;
	}//ef
	public void SetCameras(G9Camera viewCamera,G9Camera  lightCamera){
		_vCamera = viewCamera;
		_lCamera = lightCamera;
	}//ef
	@Override
	public void onDrawFrame(GL10 gl) {
		
		//����
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		// ��������_mc�������ģ��
		ArrayList<G9Model> arrModel = _mc.GetMoelList();
		int iSize = arrModel.size();
		for(int i=0; i<iSize; i++){
			G9Model model = arrModel.get(i);
			MeshInfo meshinfo = model.GetMeshInfo();			
			switch (meshinfo.enumRENDER_TECH) {
			case G9ENUM.RENDER_TECH_ERROR:
				
				break;
			case G9ENUM.RENDER_TECH_G9PHONG:
				_eff_g9phong.DrawModel(_vCamera, _lCamera, model);
				break;
			
			default:
				break;
			}//end switch			
		}//end for		
	}//ef
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//�����ӿ�
		GLES20.glViewport(0, 0, width, height);		
		//�߼��ϵ�Ť������,��������ɵ�
		_sv.InitCamera(width, height);			
	}//ef

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0, 0, 0, 1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//������
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		//GLES20.glFrontFace(GLES20.GL_CW);
		//�Ƶ�û�취�� ��������ó�ʼ��ɫ����ģ������(������ر�����glSuface������)
		InitShader();
		_sv.InitModel();
		
		
	}//ef
	//------------------------------------------------����
	//------------------------------------------------����

	

}//EC

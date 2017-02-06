package dana9919.gles;

import dana9919.gles.base.G9Function;
import dana9919.gles.base.MeshDrawable;
import dana9919.gles.base.MeshInfo;
import android.opengl.Matrix;
/**
 * �ռ�ģ����
 * @author ����
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class G9Model {
	protected MeshDrawable _mesh = null;//ģ�����ڰ�����������
	protected float[] _mxWorld = new float[16];
	/**
	 * ������INIT
	 */
	void defultInit(){
		Matrix.setIdentityM(_mxWorld, 0);
	}//ef
	public G9Model(MeshDrawable mesh) {
		// 
		_mesh = mesh;
		defultInit();
	}//ef constructor
	public MeshInfo GetMeshInfo(){
		//����װ����MESH�� vertinfo
		return _mesh.GetMeshInfo();
	}//ef
	/**
	 * ����ָ��,С��ʹ��
	 * @return
	 */
	public float[] GetWorldMatrix(){
		return _mxWorld;
	}//ef
	/******����Ϊ����ר�� **********/
	float _fAngle=0.0f;
	public void TestMen(){
		//�ۼư����ֶ�
		float[] mxRotate = new float[16];
		G9Function.glRotateAxis(mxRotate,1,new float[]{1,0,0});
		Matrix.multiplyMM(_mxWorld, 0, _mxWorld, 0, mxRotate, 0);
		
	}//ef
	public void TestMen2(float fXAngle, float fYAngle){
		float[] mxRotateX = new float[16];
		float[] mxRotateY = new float[16];
		float[] mxR  = new float[16];
		G9Function.glRotateAxis(mxRotateX, fXAngle, new float[]{1,0,0});
		G9Function.glRotateAxis(mxRotateY, fXAngle, new float[]{0,1,0});
		Matrix.multiplyMM(mxR, 0, mxRotateX, 0, mxRotateY, 0);
		Matrix.multiplyMM(_mxWorld,0,_mxWorld,0,mxR,0);
		//fXAngle = 0;
		//fYAngle = 0;
		
	}//ef
	
}//EC

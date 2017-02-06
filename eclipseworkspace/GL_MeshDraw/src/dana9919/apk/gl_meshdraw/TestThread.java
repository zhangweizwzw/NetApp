package dana9919.apk.gl_meshdraw;


import android.opengl.Matrix;
import dana9919.gles.G9Camera;
import dana9919.gles.base.G9Function;

/** 
 * @author ����
 * mail: dana9919@163.com
 * QQ:61092517
 *  * �����������ԭ��Ϊ����ת��
 */
public class TestThread extends Thread{	
	static float[] _mxTempRX = new float[16];
	static float[] _mxTempRY = new float[16];
	static float[] _mxTemp = new float[16];//������ת����
	
	protected  G9Camera _vcemera = null;
	public boolean bFlag = true;
	public float fXRotate = 0;//����X����ۼ��ƶ��� ��3ά�ռ䰴Y����ת�Ļ���
	public float fYRotate = 0;//����Y����ۼ��ƶ��� ��3ά�ռ䰴X����ת�Ļ���
	/**
	 * ����ʱ�����������
	 */	
	public TestThread(G9Camera vCamera) {
		_vcemera = vCamera;
	}//ef
	
	@Override
	public void run() {
		while (bFlag) {
			//ȡ��ָ�ƶ��ľ��룬��� foffX foffY Ȼ��
			try {
				sleep(33);
			} catch (InterruptedException e) {
				// 
				e.printStackTrace();
			}

			synchronized (_vcemera) {
				float[] v3UP = new float[3];_vcemera.GetUp(v3UP);
				float[] v3Right = new float[3];_vcemera.GetRight(v3Right);
				G9Function.glRotateAxis(_mxTempRX, fXRotate, v3UP);
				G9Function.glRotateAxis(_mxTempRY, fYRotate, v3Right);
				Matrix.multiplyMM(_mxTemp, 0,_mxTempRX,0,_mxTempRY,0);
				
				float[] v3Pos =new float[3]; 
				_vcemera.GetPosition(v3Pos);			
				G9Function.glV3Transform(v3Pos, _mxTemp);
				_vcemera.SetPosition(v3Pos);
				_vcemera.SetLookTarget(new float[]{0,0,0});	
			}			
		}//end while
		//super.run();
	}//ef
}//EC

package dana9919.gles;

import dana9919.gles.base.G9Function;
import android.opengl.Matrix;
/**
 * �������
 * @author ����
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class G9Camera {
	//------------------------------------------------����
	//------------------------------------------------Ԫ��
		//STATIC TEMP
	protected static float[] _mxTemp= new float[16];
		//������ ����,��D3D ��ͬ��� ����,������_right,
	protected float[]	_right = new float[]{1.0f,0f,0f};
	protected float[]   _up = new float[]{0f,1.0f,0f};
	protected float[]	_invLook = new float[]{0f,0f,1.0f};//����NN��
	protected float[]	_pos = new float[]{0f,0f,0f};
			//����
	protected float[] _mxView = new float[16];
	protected float[] _mxProj = new float[16];
	//------------------------------------------------����
	/**
	 * ����һ����������� ͶӰʹ��͸��ֱ��
	 * @param iWinWidth GL���ڿ�
	 * @param iWinHeight GL���ڸ�
	 * @param zf Զƽ��
	 * @param zn ��ƽ��
	 */
	public G9Camera(int iWinWidth,int iWinHeight,float zf,float zn) {
		// ����һ��������mxProj��һ��������mxView
		
		float fRatio = (float)iWinWidth/iWinHeight;
		RefreshViewMatrix(this);//�������ķ������¹���һ��mxView
		//��mxProj
		Matrix.frustumM(_mxProj, 0, -fRatio, fRatio, -1, 1, zn, zf);
		
	}//ef constructor
	//------------------------------------------------����
	private static void RefreshViewMatrix(G9Camera pthis) {
		// look����,
		G9Function.glV3Normalize(pthis._invLook);
		//����_left
		//G9Function.glV3Cross(pthis._left, pthis._invLook, pthis._up);
		G9Function.glV3Cross(pthis._right, pthis._up, pthis._invLook);
		G9Function.glV3Normalize(pthis._right);
		//�ٻع�����up
		G9Function.glV3Cross(pthis._up,pthis._invLook, pthis._right);
		G9Function.glV3Normalize(pthis._up);
		//�ٽ�mxView
		pthis._mxView[0] = pthis._right[0];pthis._mxView[1] = pthis._up[0]; pthis._mxView[2] = pthis._invLook[0];pthis._mxView[3] = 0;
		pthis._mxView[4] = pthis._right[1];pthis._mxView[5] = pthis._up[1]; pthis._mxView[6] = pthis._invLook[1];pthis._mxView[7] = 0;
		pthis._mxView[8] = pthis._right[2];pthis._mxView[9] = pthis._up[2]; pthis._mxView[10] = pthis._invLook[2];pthis._mxView[11] = 0;
		pthis._mxView[12] = -G9Function.glV3Dot(pthis._right, pthis._pos);
		pthis._mxView[13] = -G9Function.glV3Dot(pthis._up, pthis._pos);
		pthis._mxView[14] = -G9Function.glV3Dot(pthis._invLook, pthis._pos);
		pthis._mxView[15] = 1.0f;
	}//ef
	public void getWorldMatrix(float[] mxWorldOut)//ȡ�������
	{
		if(mxWorldOut.length !=16)
			throw new RuntimeException("err in Camera::getMxWorld");
		Matrix.invertM(mxWorldOut, 0, _mxView, 0);
		return;
	}//ef
	public void SetDirection(float[] v3Direction){
		for(int i=0; i<3 ; i++){
			_invLook[i] = -v3Direction[i];
		}
		
		G9Function.glV3Normalize(_invLook);
		RefreshViewMatrix(this);
	}//ef
	public void SetPosition(float[] v3Pos){
		G9Function.glFArrayCopy(_pos, v3Pos);
		RefreshViewMatrix(this);
	}//ef
	public void Set_Up(float[] v3Up){
		G9Function.glFArrayCopy(_up, v3Up);
		RefreshViewMatrix(this);
	}//ef
	public void SetLookTarget(float[] v3LookTarget){
		G9Function.glV3Subtract(_invLook,  _pos,v3LookTarget);//_pos��target������
		G9Function.glV3Normalize(_invLook);
		RefreshViewMatrix(this);
	}//ef
	public void setRight(float[] v3Right){
		G9Function.glFArrayCopy(_right, v3Right);
		G9Function.glV3Normalize(_right);
		RefreshViewMatrix(this);
	}//ef
	public void SetProjMatrix(float[] mxProjSource){
		G9Function.glFArrayCopy(_mxProj, mxProjSource);
	}//ef
	/*Get ��������ʱȫ�Բ�����ʽ�������֤��Ԫ�صİ�ȫ
	 * �����������صķ�������  getViewMatrix getProjMatrix �ķ���float[]�������Ǵ���ָ���
	 * ��Ҫ��С��ʹ���ˣ�ʹ�ò�������ο���
	 * */
	public void GetPosition(float[] v3PosOut){
		if(v3PosOut.length != 3)
			throw new RuntimeException("err in GetPosition");
		for(int i=0; i<3 ; i++){
			v3PosOut[i] = _pos[i];
		}
	}//ef
	public void GetRight(float[] v3RightOut){		
		G9Function.glFArrayCopy(v3RightOut, _right);
	}//ef
	public void GetUp(float[] v3UpOut){
		G9Function.glFArrayCopy(v3UpOut, _up);
	}//ef
	public void GetLook(float[] v3LookOut){
		if(v3LookOut.length!=3)
			throw new RuntimeException("err in camera");
		for(int i=0; i<3; i++)
			v3LookOut[i] = -_invLook[i];		
	}//ef
	public void GetInvLook(float[] v3InvLookOut){
		G9Function.glFArrayCopy(v3InvLookOut, _invLook);
	}//ef
	public void GetViewMatrix(float[] mxViewOut){
		G9Function.glFArrayCopy(mxViewOut, _mxView);
	}
	/**
	 * ����������ܴ��ڷ��գ�ԭ��������Ĳ��ǿ�����������Ԫ�ص�ָ��
	 * PS: ע�ⲻҪ��new һ��mx �� mx = GetViewMatrix() ��
	 * 		ֱ������һ����ָ���float[] Ȼ�� ���к���
	 * @return 
	 */
	public float[] GetViewMatrix(){
	
		return _mxView;
	}//ef
	public void GetProjMatrix(float[] mxProjOut){
		G9Function.glFArrayCopy(mxProjOut, _mxProj);
	}//ef
	/**
	 * ����������ܴ��ڷ��գ�ԭ��������Ĳ��ǿ�����������Ԫ�ص�ָ��
	 * @return ��FLOAT����ָ�� 
	 */
	public float[] GetProjMatrix(){
		return _mxProj;
	}//ef
	public void GetWorldMatrix(float[] mxWorldOut){
		//
		if(mxWorldOut.length!=16)
			throw new RuntimeException("err in GetWorldMatrix");
		Matrix.invertM(mxWorldOut, 0, _mxView, 0);
		return;
	}//ef
			//�����ת������ �ƶ�����
	public void strafe(float fOffset)//left<->right
	{
		for(int i=0;i<3;i++){
			_pos[i] += _right[i]*fOffset;
		}		
	}//ef
	public void fly(float fOffset)//up<.>down
	{
		for(int i=0; i<3; i++){
			_pos[i] += _up[i]*fOffset;
		}
	}//ef
	public void walk(float fOffset)//z+<.>z-
	{
		for(int i=0; i<3; i++){
			_pos[i] -= _invLook[i]*fOffset;
		}
	}//ef
	public void pitch(float fAngle)//��rightΪ��
	{
		//ʹ������STATIC ��TEMP�� _mxTemp;ʡ��ÿ�ν�һ���¾������֪��ʲôʱ��ϵͳ�Ż�ȥ�ɵ���
		//�����_mxTemp��Ϊ��ת����
		G9Function.glRotateAxis(_mxTemp, fAngle, _right);
		G9Function.glV3Transform(_up, _mxTemp);
		G9Function.glV3Transform(_invLook, _mxTemp);
		
		//ˢ���Ӿ���
		RefreshViewMatrix(this);		
	}//ef
	public void yaw(float fAngle)//upΪ��
	{
		G9Function.glRotateAxis(_mxTemp, fAngle, _up);
		G9Function.glV3Transform(_right, _mxTemp);
		G9Function.glV3Transform(_invLook, _mxTemp);
		RefreshViewMatrix(this);
	}//ef
	public void roll(float fAngle)//��lookΪ��
	{
		G9Function.glRotateAxis(_mxTemp, fAngle, _invLook);
		G9Function.glV3Transform(_up, _mxTemp);
		G9Function.glV3Transform(_right, _mxTemp);
		RefreshViewMatrix(this);
	}//ef
	//------------------------------------------------����
	//------------------------------------------------����
	
}//EC

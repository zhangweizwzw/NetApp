//���ಢ������ʹ�ã�����Ӧ�����������ʵ������
package dana9919.gles.base;

import android.content.Context;
import android.opengl.GLSurfaceView;
/**
 * ����: GLSurfaceView��abstract������,�ṩ�� initModel() initcamera()�ķ���ģ��,������ɫ������б�׼���ĵ��� 
 * @author ����
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public abstract class G9GameView extends GLSurfaceView{
	protected Context _context;

	public G9GameView(Context context){
		super(context);
		//һЩ��Ȼ�Ķ�������������
		_context = context;
		this.setEGLContextClientVersion(2);//���ע�ⲻҪ����, ����GLES�İ汾��		
	}//ef constructor
	
	//����
	/**��������ĵ��� ���� Render������� ������ģ�͵��뼰camera��ʼ��Ҫ��surface���ɺ�
	�������ȣ���������������д
	*/
	public abstract void InitModel();
	/**
	 * ��������ĵ��� ���� Render������� ������ģ�͵��뼰camera��ʼ��Ҫ��surface���ɺ�
	 * һ���� onSurfaceChange()����Եõ���������
	 * �������ȣ���������������д
	 */
	public abstract void InitCamera(int iWinWidth,int iWinHeight);
	
	
}//EC

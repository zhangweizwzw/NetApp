package dana9919.gles;

import java.util.ArrayList;


/**
 * ģ�Ͷ��� Ϊ����ʵ�ֱ�׼�������ݸ�ʽ
 * @author ����
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class ModelCollection {
	ArrayList<G9Model> _arrModel = new ArrayList<G9Model>();
	public ModelCollection() {
		// ��ʼ��
		//��ʱû��
	}//ef constructor
	/**
	 * �򼯺�������һ��Model
	 * @param model ��,��ָ��
	 */
	public void AddModel(G9Model model){
		_arrModel.add(model);
	}//ef
	/**
	 * ȡ��ģ�Ͷ���
	 * @return ���ص�����ָ��,��ʱС��,��С�ĺ��������
	 */
	public ArrayList<G9Model> GetMoelList(){
		return _arrModel;
	}//ef
}//EC

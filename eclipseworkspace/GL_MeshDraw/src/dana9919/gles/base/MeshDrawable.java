package dana9919.gles.base;


/**
 * ����Ҫ��G9���ߵ�ģ��,��Ҫ����������ӿ���
 * ����������
 * @author dana9919
 *
 */
 
public interface MeshDrawable {
	/**
	 * ȡ�� G9VertInfo ��G9Render��(����ȷ����GPU������Щ��������)
	 * �����������������Ҫ���Ƶ���Ϣ(������) ����ʡȥ��drawMesh 
	 * @return
	 */	
	public MeshInfo GetMeshInfo();
}

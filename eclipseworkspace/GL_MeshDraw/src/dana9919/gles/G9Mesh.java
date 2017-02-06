package dana9919.gles;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.res.Resources;
import dana9919.gles.base.G9ENUM;
import dana9919.gles.base.G9Function;
import dana9919.gles.base.MeshDrawable;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.base.SubInfo;
/**
 * 3D����
 *  ��ʱֻ����  *.obj�ļ���ȡ,����Ҫ�� *.obj *.mtl �������ļ����� assets��
 *  ��ʱ������ȡ��3D����ֻ֧��  λ��,��������,����������
 * @author ����
 * mail: dana9919@163.com
 * QQ 61092517
 */
public class G9Mesh implements MeshDrawable{
	//------------------------------------------------����	
	//------------------------------------------------Ԫ��
	MeshInfo _info = null;
	//------------------------------------------------���죨���ṩ�޲ι��죩
	public G9Mesh(Resources resource,String strObjFileName) {
		// ���ļ����������񣬲������ _info ���������ܳ� ����ԭ���ϲ�Ӧ�ֳ�С����
		ArrayList<Float> arr_v = new ArrayList<Float>();//�����ٴ� pos ����Ԫ
		ArrayList<Float> arr_vt = new ArrayList<Float>();//�ٴ� texcoord ����Ԫ
		ArrayList<Float> arr_vn = new ArrayList<Float>();//�ٴ� normal ����Ԫ
		try{
			InputStream ins = resource.getAssets().open(strObjFileName) ;//���ģ�ֻ�ṩinputstream�ķ���
			InputStreamReader inReader = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(inReader);
			String strTemp = null;//�ٴ�ÿ���ַ�			
			
			SubInfo  curSub = null;//��ǰ������SubInfo ��������ٴ�����;
			_info = new MeshInfo();
			
			while((strTemp = br.readLine())!=null)//��������Ҳ��Ϊnull???
			{
				String[] arrStr = strTemp.split("[ ]+");//���ո�ָ�
				if(arrStr[0].trim().equals("o"))//ȡ�ļ���
				{
					_info.strMeshName = arrStr[1];
				}else if(arrStr[0].trim().equals("mtllib") )//���������ļ�
				{
					//��mtl�ļ�,��ʼͼƬ(ʱ����sufaceview����֮��),������subset����
					String strMtlFilename = arrStr[1]; 
					_info.iSubsets = SetupMaterial(resource,strMtlFilename);
					
				}else if(arrStr[0].trim().equals( "v"))//POS����
				{
					arr_v.add(Float.parseFloat(arrStr[1]));
					arr_v.add(Float.parseFloat(arrStr[2]));
					arr_v.add(Float.parseFloat(arrStr[3]));
					
				}else if(arrStr[0] .trim().equals("vt"))//texcoord����
				{
					arr_vt.add(Float.parseFloat(arrStr[1]));
					arr_vt.add(1.0f-Float.parseFloat(arrStr[2]));
					
				}else if(arrStr[0] .trim().equals( "vn"))//normal ����
				{
					arr_vn.add(Float.parseFloat(arrStr[1]));
					arr_vn.add(Float.parseFloat(arrStr[2]));
					arr_vn.add(Float.parseFloat(arrStr[3]));
				}/////////////����ȥ��ȡ����Ϣ����䵽 SubTemp�ṹ
				else if(arrStr[0] .trim().equals( "usemtl"))//���µ��������ĸ�SUBSET
				{
					curSub = _info.GetSubset(arrStr[1]);//�����ǰ������subinfo
				}				
				else if(arrStr[0] .trim().equals( "f"))
				{
					//�������ʱ�� curSubӦ��Ϊ�� ��ʱʡȥ�����
					String[] arrVertex = null;
					int index = -1;					 
					for(int i =1; i<=3; i++)
					{
						arrVertex = arrStr[i].split("/");//����� i �������Ϣ
						index= Integer.parseInt(arrVertex[0])-1;//POS������
						curSub.arrV.add(arr_v.get(3*index+0));
						curSub.arrV.add(arr_v.get(3*index+1));
						curSub.arrV.add(arr_v.get(3*index+2));
						
						index = Integer.parseInt(arrVertex[1])-1;//UV������
						curSub.arrVT.add(arr_vt.get(2*index));
						curSub.arrVT.add(arr_vt.get(2*index+1));
						
						
						index = Integer.parseInt(arrVertex[2])-1;//NORMAL ������
						curSub.arrVN.add(arr_vn.get(3*index));
						curSub.arrVN.add(arr_vn.get(3*index+1));
						curSub.arrVN.add(arr_vn.get(3*index+2));
					}//end for					
				}//"f"��ͷ�ν���				
			}//end while			
			//����MeshInfo���Բ������� CommitMeshInfo()���������ĵ���
			_info.CommitMeshInfo();
		}catch (Exception e) {
			// 
			e.printStackTrace();
		}//end try
		//��ʼRENDER_TECH ��phong�������
		_info.enumRENDER_TECH = G9ENUM.RENDER_TECH_G9PHONG;
		
	}//ef constructor
	/**
	 * ��ʼ����MESH�Ĳ�����Ϣ
	 * @param resource ϵͳ��Դ
	 * @param strMtlFilename �����ļ�
	 * @return SUBSET����
	 * @throws IOException 
	 */
	private int SetupMaterial(Resources resource, String strMtlFilename) throws IOException {
		//Ka Kd Ks��ʱ����,������ subinfo��û��û����ȥ
		InputStream ins = resource.getAssets().open(strMtlFilename);
		InputStreamReader insReader = new InputStreamReader(ins);
		BufferedReader br = new BufferedReader(insReader);
		String strTemp = null;
		int iSubCount = 0;
		SubInfo curSub = null;//��ǰ��SUBSET INFO
		while((strTemp = br.readLine())!= null){
			String[] arrStr = strTemp.split("[ ]+");
			if(arrStr[0] .trim().equals( "newmtl"))//��ʾ����
			{
				//��ʱ����������ʼ
				iSubCount++;
				curSub = new SubInfo(arrStr[1]);
				_info.arrSubInfo.add(curSub);
			}else if(arrStr[0] .trim().equals( "map_Kd"))//������
			{
				//�Ӹ��ռ�,�е����
				if(curSub == null)
					break;								
				curSub.idDTex = G9Function.CreateTexture(resource,arrStr[1]);				
			}
		}//end while		
		return iSubCount;
	}//ef
	//------------------------------------------------����
	@Override
	public MeshInfo GetMeshInfo() {
		// ��Ҫʱ����NULL���
		return _info;
	}//ef
	public void SetRenderTech(int enumRENDER_TECH){
		_info.enumRENDER_TECH = enumRENDER_TECH;
	}//ef

	
	//------------------------------------------------����
	//------------------------------------------------����
	//------------------------------------------------test

	


}//EC

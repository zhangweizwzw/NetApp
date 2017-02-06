package dana9919.gles.base;

import java.util.ArrayList;

import android.util.Log;
/**
 * ����:3D���� ��ʵ�����ݰ�����
 * @author ����
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public class MeshInfo {
	public String strMeshName = "";
	public int iSubsets=0;//SUBSET�ĸ���
	public int enumRENDER_TECH = -1;
	public ArrayList<SubInfo> arrSubInfo = new ArrayList<SubInfo>();
	
	public int GetSubIndex(String strSubName){
		for(int i=0; i<arrSubInfo.size(); i++){
			if((arrSubInfo.get(i).strSubsetName)==strSubName)
				return i;
		}//end for
		return -1;
	}//ef
	public SubInfo GetSubset(String strSubName){
		
		for(int i=0; i<arrSubInfo.size(); i++){
			if((arrSubInfo.get(i).strSubsetName).trim().equals(strSubName))
				return arrSubInfo.get(i);
		}//end for
		return null;
	}//ef
	
	public SubInfo GetSubset(int index){
		if(index< arrSubInfo.size()){
			return arrSubInfo.get(index);
		}else{
			Log.e("ERR", "in MeshInfo::GetSubset");
			return null;
		}
		
	}//ef
	/**
	 * ����MESH INFO�����һ�� ��Ⲣ�������MESH INFO
	 * @return �ܶ�����
	 */
	public int CommitMeshInfo(){
		iSubsets = arrSubInfo.size();
		int iVertCount = 0;
		for (int i=0; i<iSubsets; i++){
			int iSubVert = arrSubInfo.get(i).CommitSubInfo();
			if(iSubVert<0)
			{
				//�Ӹ�
				throw new RuntimeException("Mesh��subset������");
			}				
			iVertCount += iSubVert;
		}//end for
		return iVertCount;
	}//ef
}//EC

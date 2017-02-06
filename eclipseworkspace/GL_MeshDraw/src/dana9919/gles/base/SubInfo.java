package dana9919.gles.base;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/*
 * ���Ҫ��ȫ��Ӧ����Ԫ������ protected ��Ȼ����һ�� get����
 * �ں�������null���󴫳�  ���Ժܴ�����ϱ�֤��������ָ��
 * ����ʱû��ƻ����Լ��õģ�С�ĵ������ 
 * 2012-8-23 �ռ�������,���ڽ����������Ҫ,������������arraylist<Float>��TEMP�����ٴ�
 * ���ٴ治��Ҫ֮��,����ʹ�� null��ֵ ����ϵͳ����
 * */
/**
 * ����:3D������ÿ����(������)��������Ϣ
 * @author ����
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public class SubInfo {
	public String   strSubsetName = "";//���ڶ�obj�ļ������� ,������������(�� obj mtl�ļ�����ͬ)
	public FBInfo	posInfo = null;//ע�� FBInfo��� iStradeʹ�� const�� 
	public FBInfo	normalInfo = null;
	public FBInfo	uvInfo = null;
	public FBInfo	colorInfo = null;
	public FBInfo	tangentInfo = null;
	public FBInfo	binormalInfo = null;
	
	public int idDTex; //����texture��ID ���û�� ӦΪ-1
	public int idSTex;
	public int idNTex;
	public int iSubVertCount;//��� subset��Ķ������� 
	
	//����Ϊ�ٴ�ʹ�õı��� ��������ʱӦ�����ٴ����ָ�� ��ϵͳ����
	public ArrayList<Float>		arrV = new ArrayList<Float>();//�ٴ�pos
	public ArrayList<Float> 	arrVT = new ArrayList<Float>();//�ٴ�texcoord
	public ArrayList<Float>    	arrVN  = new ArrayList<Float>();//�ٴ�NORMAL
	
	
	
	public SubInfo(String strSubName){
		//ע�� ��ʹʹ���˹��� ������� FBInfo�����ǿյ�
		strSubsetName = strSubName;
		idDTex = -1;
		idSTex = -1;
		idNTex = -1;
		iSubVertCount = 0;
	}//ef constructor



	public void PutPosFB(FloatBuffer fbuf)//stride ���������ˣ��Զ�
	{
		posInfo = new FBInfo();
		posInfo._fbuf = fbuf;
		posInfo._iStride = 3*4;
	}//ef
	public void PutNormalFB(FloatBuffer fbuf){
		normalInfo = new FBInfo();
		normalInfo._fbuf = fbuf;
		normalInfo._iStride = 3*4;
		
	}//ef
	public void PutUvFB(FloatBuffer fbuf){
		uvInfo = new FBInfo();
		uvInfo._fbuf = fbuf;
		uvInfo._iStride = 2*4;
	}//ef
	public void PutColorFB(FloatBuffer fbuf){
		colorInfo = new FBInfo();
		colorInfo._fbuf = fbuf;
		colorInfo._iStride = 4*4;
	}//ef
	public void PutTangentFB(FloatBuffer fbuf){
		tangentInfo = new FBInfo();
		tangentInfo._fbuf = fbuf;
		tangentInfo._iStride = 3*4;
	}//ef
	public void PutBinormalFB(FloatBuffer fbuf){
		binormalInfo = new FBInfo();
		binormalInfo._fbuf = fbuf;
		binormalInfo._iStride = 3*4;
	}//ef
	/**
	 * ���ٴ����Ϣ������ ���FBInfo��ʽ,��,Commit�ǲ��������˵�,�л��ỻ�����ɵ������
	 * @return ��SUBSET�Ķ�������(,������-1)
	 */
	public int CommitSubInfo(){
		PutPosFB(G9Function.PushIntoFLoatBuffer(arrV));
		PutUvFB(G9Function.PushIntoFLoatBuffer(arrVT));
		PutNormalFB(G9Function.PushIntoFLoatBuffer(arrVN));
		iSubVertCount = arrV.size()/3;
		//�Լ�
		if((arrVT.size()/2 != iSubVertCount)||(arrVN.size()/3 != iSubVertCount))
		{
			iSubVertCount = -1;			
		}
		//�ӵ��ٴ����ݵ�ָ��
		arrV = null;
		arrVN = null;
		arrVT = null;
		return iSubVertCount;
		
	}//ef
}//EC

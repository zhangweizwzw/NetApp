package dana9919.gles.effect;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import dana9919.gles.G9Camera;
import dana9919.gles.G9Model;
import dana9919.gles.base.MeshInfo;
import dana9919.gles.base.ShaderUtil;
import dana9919.gles.base.SubInfo;
/**
 * ����: ��Ⱦ�����׵���ɫ��Ӧ���� ����ʵ�־���������ɫ 
 * PS:��������׵�������ɫ���ı�����ϵͳassets���� �ļ���Ϊ: g9phong.vs g9phong.ps
 * @author ����
 * mail: dana9919@163.com
 * QQ: 61092517
 */
public class Effect_g9phong {
	//��EFFECT PROGRAM
	public int hProgram;
	//UNIFORM
	public int hmxWVP;
	public int hmxView;
	public int hmxWorldView;
	public int hv3LightDir;
	public int hspDiffuse;//������
	//ATTRIBUTE
	public int hv3Pos;
	public int hv3Normal;
	public int hv2UV;
	//CONSTRUCTOR
	public Effect_g9phong(Resources resource) {
		// NEW ��ʱ���Զ�ȫ��ֵ
		String textVS = ShaderUtil.loadFromAssetsFile("g9phong.vs", resource);
		String textPS = ShaderUtil.loadFromAssetsFile("g9phong.ps", resource);	
		//����effect���
		hProgram = ShaderUtil.createProgram(textVS, textPS);
		//uniform
		hmxWVP = GLES20.glGetUniformLocation(hProgram, "g_mxWVP");
		hmxView = GLES20.glGetUniformLocation(hProgram, "g_mxView");		
		hmxWorldView=GLES20.glGetUniformLocation(hProgram, "g_mxWorldView");
		hv3LightDir = GLES20.glGetUniformLocation(hProgram, "g_v3LightDir");
		hspDiffuse = GLES20.glGetUniformLocation(hProgram, "spDiffuse");
		//attribute
		hv3Pos = GLES20.glGetAttribLocation(hProgram, "v3Pos");
		hv3Normal = GLES20.glGetAttribLocation(hProgram, "v3Normal");
		hv2UV  = GLES20.glGetAttribLocation(hProgram, "v2UV");		
	}//ef constructor
	/**
	 * �ô�effect������ָ��ģ��
	 * @param vcamera �������
	 * @param lcamera �������
	 * @param model Ҫ���Ƶ�ģ��
	 */
	public void DrawModel(G9Camera vcamera,G9Camera lcamera,G9Model model){
		//��Effect��������Ⱦ �����е������ϣ�����ȷʵ���㲻��
		//��ֵ������� ʹ��ʱС��		
		GLES20.glUseProgram(hProgram);
		MeshInfo meshinfo = model.GetMeshInfo();
		//������� 
		float[] mxWVP = new float[16];
		float[] mxWorldView = new float[16];
		float[] mxView = new float[16];
		
		vcamera.GetViewMatrix(mxView);		
		Matrix.multiplyMM(mxWorldView, 0, mxView, 0, model.GetWorldMatrix(), 0);
		Matrix.multiplyMM(mxWVP, 0, vcamera.GetProjMatrix(), 0, mxWorldView, 0);
		
		//������
		GLES20.glUniformMatrix4fv(hmxWVP, 1, false, mxWVP, 0);
		GLES20.glUniformMatrix4fv(hmxView, 1, false, mxView, 0);
		GLES20.glUniformMatrix4fv(hmxWorldView, 1, false, mxWorldView, 0);
		//��������
		float[] v3LightDir = new float[3];
		lcamera.GetLook(v3LightDir);
		GLES20.glUniform3fv(hv3LightDir, 1, v3LightDir, 0);
		//��SUBSET �����㼰���� �� draw\
		SubInfo curSub = null;
		for(int i=0; i<meshinfo.iSubsets; i++)		
		{
			curSub = meshinfo.GetSubset(i);
			//�������
			GLES20.glVertexAttribPointer(hv3Pos, 
					3, GLES20.GL_FLOAT,false,
					curSub.posInfo._iStride, 
					curSub.posInfo._fbuf);
			GLES20.glVertexAttribPointer(hv3Normal,
					3,GLES20.GL_FLOAT,false,
					curSub.normalInfo._iStride,
					curSub.normalInfo._fbuf);
			GLES20.glVertexAttribPointer(hv2UV,
					2,GLES20.GL_FLOAT,false,
					curSub.uvInfo._iStride,
					curSub.uvInfo._fbuf);
			GLES20.glEnableVertexAttribArray(hv3Pos);
			GLES20.glEnableVertexAttribArray(hv3Normal);
			GLES20.glEnableVertexAttribArray(hv2UV);
			//TEXTURE����//�����˴�ǹ
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, curSub.idDTex);//�ٴ�ϣ��WP8����ANDROID
			GLES20.glUniform1i(hspDiffuse, 0);
			
			
			//���Ƶ�ǰ subset
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, curSub.iSubVertCount);			
		}//end for		
	}//ef
}

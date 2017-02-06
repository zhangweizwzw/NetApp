package ting.app.NaTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.ags.na.NAFeaturesAsFeature;
import com.esri.core.tasks.ags.na.Route;
import com.esri.core.tasks.ags.na.RoutingDirection;
import com.esri.core.tasks.ags.na.RoutingParameters;
import com.esri.core.tasks.ags.na.RoutingResult;
import com.esri.core.tasks.ags.na.RoutingTask;
import com.esri.core.tasks.ags.na.StopGraphic;


public class NATestActivity extends Activity {
	
	MapView mMapView ;
	private TextView tv_label;
	private SpatialReference mapSR=SpatialReference.create(102100);;
	private SpatialReference NASR=SpatialReference.create(4326);
	private GraphicsLayer routeLayer;//��ѯ��������·��
	private GraphicsLayer semLayer;//��ѯ����·��Ƭ��
	private List<Point> stopPoints;//�������е�ͣ����
	private Symbol stopSymbol;//ͣ����ķ���
	 private SimpleLineSymbol hiderSym;//��ѯ����·����Ƭ�Σ�����͸�����䡰���ء�
	 private SimpleLineSymbol showSym;//��·��Ƭ�α�ѡ�е�ʱ����ʾ�ķ���
	 private int selectID=-1;//��ѡ�е�·��Ƭ�ε�ID��-1��ʾû�б�ѡ�У�
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		mMapView = (MapView) findViewById(R.id.map);
		
		tv_label=(TextView) findViewById(R.id.directionsLabel);
		ArcGISTiledMapServiceLayer atl=new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
		mMapView.addLayer(atl);
		//mapSR=mMapView.getSpatialReference();
		routeLayer=new GraphicsLayer();
		semLayer=new GraphicsLayer();
		stopPoints=new ArrayList<Point>();
		mMapView.addLayer(routeLayer);
		mMapView.addLayer(semLayer);
		stopSymbol=new SimpleMarkerSymbol(Color.RED, 3, SimpleMarkerSymbol.STYLE.CIRCLE);
		hiderSym=new SimpleLineSymbol(Color.WHITE, 1);
		//���ó�͸����ʹ�䴦�ڡ����ء�״̬
		hiderSym.setAlpha(100);
		showSym=new SimpleLineSymbol(Color.RED, 4);
		mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			
			public void onStatusChanged(Object arg0, STATUS arg1) {
				if(arg0==mMapView&&arg1==STATUS.INITIALIZED){
					Point p= new Point( -122.084095,37.422006);
					mMapView.zoomToResolution((Point) GeometryEngine.project(p, NASR, mapSR), 20.0);
					Log.i("OK", "KKK");
					//������ͼ���Ҫ������λ��
					mMapView.setOnSingleTapListener(new MyOnSintTapLis());
					//������ʼִ��·������
					mMapView.setOnLongPressListener(new MyOnPressLis());
					//����tv_label�ĵ����¼�Ϊ���������ѡ�еĺͲ�ѯ�Ľ��
					tv_label.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							semLayer.removeAll();
							routeLayer.removeAll();
							selectID=-1;
							tv_label.setText("");
						}
					});
				}
			}
		});

    }
    
    /**
     * ������ͼ�¼������ж���semLayer�����Ƿ�Ϊ�գ�
     * ���Ϊ�վ�����ͣ���㣬�����Ϊ�������ѡ��semLayer�е����ݲ���
     */
    class MyOnSintTapLis implements OnSingleTapListener{
		public void onSingleTap(float arg0, float arg1) {
			
			if(semLayer.getNumberOfGraphics()==0){//����ͣ����
				Point mapPoint=mMapView.toMapPoint(arg0, arg1);
				routeLayer.addGraphic(new Graphic(mapPoint, stopSymbol));
				stopPoints.add((Point)GeometryEngine.project(mapPoint, mapSR, NASR));
			}else{//ѡ��·��Ƭ��
				int[] ids= semLayer.getGraphicIDs(arg0, arg1, 20);
				if(ids.length>0){//��·��Ƭ�α�ѡ��
					//�ظ��Ѿ���ѡ�е�·��Ƭ��Ϊ͸��
					semLayer.updateGraphic(selectID,hiderSym);
					//�����µı�ѡ�е�·��Ƭ��Ϊ��ѡ��״̬
					selectID=ids[0];
					semLayer.updateGraphic(selectID,showSym);
					//ȡ�ñ�ѡ�е�·��Ƭ�κ�������
					Graphic selectGraphic=semLayer.getGraphic(selectID);
					String text=(String) selectGraphic.getAttributeValue("text");
					double time=(Double) selectGraphic.getAttributeValue("time");
					double length=(Double) selectGraphic.getAttributeValue("length");
					tv_label.setText("���ȣ�"+length+"��,  ʱ�䣺"+time+"����,������"+text);
					//���ŵ���ѡ�е�Ƭ��
					mMapView.setExtent(selectGraphic.getGeometry(),100);
				}
			}
		}
    	
    }
    //������ͼ�¼�
    class MyOnPressLis implements OnLongPressListener{
		public void onLongPress(float arg0, float arg1) {
		if(stopPoints.size()<2){
			Toast.makeText(getApplicationContext(), "��������ѡ������ͣ����", 0).show();
			return;
		}
		Toast.makeText(getApplicationContext(), "��ʼ�������·��", 1).show();
			new Thread(){//����������������߳��н���
				public void run() {
					//׼������
					RoutingParameters rp=new RoutingParameters();
					NAFeaturesAsFeature naferture=new NAFeaturesAsFeature();
					//���ò�ѯͣ���㣬����Ҫ����
					for(Point p :stopPoints){
						StopGraphic sg=new StopGraphic(p);
						naferture.addFeature(sg);
					}
					rp.setStops(naferture);
					//���ò�ѯ���������ϵ����ͼһ��
					rp.setOutSpatialReference(mapSR);
					  RoutingTask rt = new RoutingTask( 
			                  "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Network/USA/NAServer/Route",
			                  null);
					  try {
						  //ִ�в���
						RoutingResult rr=rt.solve(rp);
						runOnUiThread(new MyRun(rr));
					} catch (Exception e) {
						e.printStackTrace();
						Looper.prepare();
						Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
						Looper.loop();
					}
				
				}
			}.start();
		}
    	
    }
    
    class MyRun implements Runnable{
    	private RoutingResult rr;
		public MyRun(RoutingResult rr) {
			this.rr=rr;
		}
		public void run() {
				stopPoints.clear();
				//�õ���ѯ����·��
				Route r=rr.getRoutes().get(0);
				//��tv_label�и�ֵ
				tv_label.setText("�г��ȣ�"+r.getTotalLength()+"��,  ��ʱ�䣺"+r.getTotalTime()+"����,������"+r.getRouteName());
				//��������ѯ��·������routeLayer��
				Graphic routeGraphic=new Graphic(r.getRoute().getGeometry(),new SimpleLineSymbol(Color.BLUE, 3));
				routeLayer.addGraphic(routeGraphic);
				mMapView.setExtent(routeGraphic.getGeometry(),100);
				List<RoutingDirection>  routeDirs=rr.getRoutes().get(0).getRoutingDirections();
				
				//ȡ�ò�ѯ����·��Ƭ�Σ��������ԣ����뵽semLayer�У��������ѯÿ��Ƭ����Ϣ
				Map<String ,Object> atts=new HashMap<String,Object>();
				for(RoutingDirection routeDir: routeDirs){
				atts.put("text", routeDir.getText());
				atts.put("length", routeDir.getLength());
				atts.put("time", routeDir.getTime());
				Graphic dirGraphic=new Graphic(routeDir.getGeometry(), hiderSym, atts, null);
				semLayer.addGraphic(dirGraphic);	
			}
		}
    	
    }

	@Override 
	protected void onDestroy() { 
		super.onDestroy();
 }
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.pause();
 }
	@Override 	protected void onResume() {
		super.onResume(); 
		mMapView.unpause();
	}

}
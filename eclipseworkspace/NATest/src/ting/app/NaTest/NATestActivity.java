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
	private GraphicsLayer routeLayer;//查询到的整条路径
	private GraphicsLayer semLayer;//查询到的路径片段
	private List<Point> stopPoints;//保存所有的停靠点
	private Symbol stopSymbol;//停靠点的符号
	 private SimpleLineSymbol hiderSym;//查询到的路径的片段，设置透明将其“隐藏”
	 private SimpleLineSymbol showSym;//当路径片段被选中的时候，显示的符号
	 private int selectID=-1;//被选中的路径片段的ID（-1表示没有变选中）
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
		//设置成透明，使其处于“隐藏”状态
		hiderSym.setAlpha(100);
		showSym=new SimpleLineSymbol(Color.RED, 4);
		mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			
			public void onStatusChanged(Object arg0, STATUS arg1) {
				if(arg0==mMapView&&arg1==STATUS.INITIALIZED){
					Point p= new Point( -122.084095,37.422006);
					mMapView.zoomToResolution((Point) GeometryEngine.project(p, NASR, mapSR), 20.0);
					Log.i("OK", "KKK");
					//单击地图添加要经过的位置
					mMapView.setOnSingleTapListener(new MyOnSintTapLis());
					//长按开始执行路径分析
					mMapView.setOnLongPressListener(new MyOnPressLis());
					//设置tv_label的单击事件为，清楚所以选中的和查询的结果
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
     * 单击地图事件，先判断是semLayer里面是否为空，
     * 如果为空就增加停靠点，如果不为空则进行选择semLayer中的内容操作
     */
    class MyOnSintTapLis implements OnSingleTapListener{
		public void onSingleTap(float arg0, float arg1) {
			
			if(semLayer.getNumberOfGraphics()==0){//增加停靠点
				Point mapPoint=mMapView.toMapPoint(arg0, arg1);
				routeLayer.addGraphic(new Graphic(mapPoint, stopSymbol));
				stopPoints.add((Point)GeometryEngine.project(mapPoint, mapSR, NASR));
			}else{//选中路径片段
				int[] ids= semLayer.getGraphicIDs(arg0, arg1, 20);
				if(ids.length>0){//有路径片段被选中
					//回复已经被选中的路径片段为透明
					semLayer.updateGraphic(selectID,hiderSym);
					//设置新的被选中的路径片段为被选中状态
					selectID=ids[0];
					semLayer.updateGraphic(selectID,showSym);
					//取得被选中的路径片段和其属性
					Graphic selectGraphic=semLayer.getGraphic(selectID);
					String text=(String) selectGraphic.getAttributeValue("text");
					double time=(Double) selectGraphic.getAttributeValue("time");
					double length=(Double) selectGraphic.getAttributeValue("length");
					tv_label.setText("长度："+length+"米,  时间："+time+"分钟,描述："+text);
					//缩放到被选中的片段
					mMapView.setExtent(selectGraphic.getGeometry(),100);
				}
			}
		}
    	
    }
    //长按地图事件
    class MyOnPressLis implements OnLongPressListener{
		public void onLongPress(float arg0, float arg1) {
		if(stopPoints.size()<2){
			Toast.makeText(getApplicationContext(), "必须至少选择两个停靠点", 0).show();
			return;
		}
		Toast.makeText(getApplicationContext(), "开始查找最短路径", 1).show();
			new Thread(){//网络分析不能再主线程中进行
				public void run() {
					//准备参数
					RoutingParameters rp=new RoutingParameters();
					NAFeaturesAsFeature naferture=new NAFeaturesAsFeature();
					//设置查询停靠点，至少要两个
					for(Point p :stopPoints){
						StopGraphic sg=new StopGraphic(p);
						naferture.addFeature(sg);
					}
					rp.setStops(naferture);
					//设置查询输入的坐标系跟底图一样
					rp.setOutSpatialReference(mapSR);
					  RoutingTask rt = new RoutingTask( 
			                  "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Network/USA/NAServer/Route",
			                  null);
					  try {
						  //执行操作
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
				//得到查询到的路径
				Route r=rr.getRoutes().get(0);
				//往tv_label中赋值
				tv_label.setText("中长度："+r.getTotalLength()+"米,  总时间："+r.getTotalTime()+"分钟,描述："+r.getRouteName());
				//将这条查询到路径放入routeLayer中
				Graphic routeGraphic=new Graphic(r.getRoute().getGeometry(),new SimpleLineSymbol(Color.BLUE, 3));
				routeLayer.addGraphic(routeGraphic);
				mMapView.setExtent(routeGraphic.getGeometry(),100);
				List<RoutingDirection>  routeDirs=rr.getRoutes().get(0).getRoutingDirections();
				
				//取得查询到的路径片段，和其属性，放入到semLayer中，供点击查询每个片断信息
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
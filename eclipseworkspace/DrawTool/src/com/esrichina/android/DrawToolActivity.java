package com.esrichina.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esrichina.android.ext.drawtool.DrawEvent;
import com.esrichina.android.ext.drawtool.DrawEventListener;
import com.esrichina.android.ext.drawtool.DrawTool;
import com.esrichina.demo.geocoding.R;

/**
 * 
 * @author ropp gispace@yeah.net
 * 
 * ����Ҫ��ӦDrawTool�Ļ�ͼ�¼����˴���Ҫʵ��DrawEventListener�ӿ�
 */
public class DrawToolActivity extends Activity implements DrawEventListener{

	private MapView mapView;
	private GraphicsLayer drawLayer;
	private DrawTool drawTool;
	
	private MarkerSymbol markerSymbol;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.mapView = (MapView)this.findViewById(R.id.map);
        this.drawLayer = new GraphicsLayer(this.mapView.getContext());
        this.mapView.addLayer(this.drawLayer);
        // ��ʼ��DrawToolʵ��
        this.drawTool = new DrawTool(this.mapView);
        // ����Activity����ΪDrawToolʵ����Listener
        this.drawTool.addEventListener(this);
        
        // �˴����Ը�����Ҫ����DrawToolʵ����ͼʱʹ�õĸ���symbol
        this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
        //this.drawTool.setMarkerSymbol(markerSymbol);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.point:
			drawTool.activate(DrawTool.POINT);
			break;
		case R.id.envelope:
			drawTool.activate(DrawTool.ENVELOPE);
			break;
		case R.id.polygon:
			drawTool.activate(DrawTool.POLYGON);
			break;
		case R.id.polyline:
			drawTool.activate(DrawTool.POLYLINE);
			break;
		case R.id.freehandpolygon:
			drawTool.activate(DrawTool.FREEHAND_POLYGON);
			break;
		case R.id.freehandpolyline:
			drawTool.activate(DrawTool.FREEHAND_POLYLINE);
			break;
		case R.id.circle:
			drawTool.activate(DrawTool.CIRCLE);
			break;
		case R.id.clear:
			this.drawLayer.clear();
			this.drawLayer.postInvalidate();
			this.drawTool.deactivate();
			break;
		}
		return true;
	}

    // ʵ��DrawEventListener�ж���ķ���
	public void handleDrawEvent(DrawEvent event) {
		// �����õ�ͼ�Σ��Ѿ�ʵ������Graphic������ӵ�drawLayer�в�ˢ����ʾ
		this.drawLayer.addGraphic(event.getDrawGraphic());
		this.drawLayer.postInvalidate();
	}
}
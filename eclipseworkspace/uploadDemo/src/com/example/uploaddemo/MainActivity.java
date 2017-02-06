package com.example.uploaddemo;

import java.io.File;

import okhttp3.Call;
import okhttp3.RequestBody;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.uploaddemo.activity.AlbumActivity;
import com.example.uploaddemo.activity.GalleryActivity;
import com.example.uploaddemo.adapter.GridAdapter;
import com.example.uploaddemo.utils.Bimp;
import com.example.uploaddemo.utils.FileUtils;
import com.example.uploaddemo.utils.ImageItem;
import com.example.uploaddemo.utils.PublicWay;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class MainActivity extends Activity {

	private static final int TAKE_PICTURE = 0x000001;
	private GridView mGridView;
	private GridAdapter adapter;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap bimap;
	private LinearLayout parentView;
	private Button submit;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PublicWay.activityList.add(this);
		initViews();
	}

	private void initViews() {

		initPopu();
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		parentView = (LinearLayout) findViewById(R.id.ll_grid_view);
		submit = (Button) findViewById(R.id.submit);
		mGridView = (GridView) findViewById(R.id.grid_view);
		// 点击GridView时出现背景色设置为透明
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(MainActivity.this,
							GalleryActivity.class);
					intent.putExtra("ID", position);
					startActivity(intent);
				}
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
				 PublicWay.fileList.add(new File(Bimp.tempSelectBitmap.get(i).getImagePath()));
				 }
				doAn();
			}
		});
	}

	protected void doAn() {
		OkHttpUtils.post()
		.addMoreFile("upload", "img", PublicWay.fileList)
		.url(url)
		.build()
		.execute(new StringCallback() {
			
			@Override
			public void onResponse(String response) {
				System.out.println(response);
			}
			
			@Override
			public void onError(Call call, Exception e) {
				System.out.println(e.toString());
			}
			@Override
			public void inProgress(float progress) {
				Message message = handler.obtainMessage();
				message.obj = progress;
				handler.sendMessage(message);
			}
		});
	}

	private String url = "http://192.168.0.22:8080/Demo/PhotoTest";
//	1private String url = "http://192.168.1.103:8080/rm/upload/handwrite";
	private ProgressBar progressBar;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			float pro = (Float) msg.obj;
			progressBar.setVisibility(View.VISIBLE);
			System.out.println((int) (pro * 100) + "");
			progressBar.setProgress((int) (pro * 100));
			if(pro == 1) {
				progressBar.setVisibility(View.GONE);
				Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_LONG).show();
				Bimp.tempSelectBitmap.clear();
				PublicWay.fileList.clear();
				adapter.notifyDataSetChanged();
			}
		};
	};
	private RequestBody body;

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		mGridView.setAdapter(adapter);
	}

	private void initPopu() {
		pop = new PopupWindow(MainActivity.this);
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
	}

	protected void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				Bimp.tempSelectBitmap.add(takePhoto);
			}
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for (int i = 0; i < PublicWay.activityList.size(); i++) {
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			System.exit(0);
		}
		return true;
	}
}

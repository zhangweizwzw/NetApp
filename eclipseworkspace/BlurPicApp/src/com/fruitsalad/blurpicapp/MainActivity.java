package com.fruitsalad.blurpicapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity{
	private ImageView blurView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		blurView = (ImageView) findViewById(R.id.show);
		
		InputStream is = getResources().openRawResource(R.drawable.aaa);  

		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		
		Bitmap newImage = Blur.fastblur(MainActivity.this, mBitmap,24);
		blurView.setImageBitmap(newImage);
		
//		selectButton = (Button) findViewById(R.id.select_pic);
//		saveButton = (Button) findViewById(R.id.save_pic);
//
//		selectButton.setOnClickListener(this);
//		saveButton.setOnClickListener(this);
//
//		outDir = new File(Environment.getExternalStorageDirectory(), "BlurPic");
//		if (!outDir.exists())
//			outDir.mkdirs();
	}

}

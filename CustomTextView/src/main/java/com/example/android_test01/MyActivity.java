package com.example.android_test01;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.app.Activity;

public class MyActivity extends Activity {

	private final String TAG = "MyActivity";

	TextView textView01 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my);

		// 初始化UI控件
		initUI();

		// 加载数据
		String textViewStr = loadFromAssetsFile("baike_textdemo01.txt");

		// 数据显示
		textView01.setText(textViewStr);
	}

	private void initUI() {
		textView01 = (TextView) findViewById(R.id.textView01);
	}

	private String loadFromAssetsFile(String fname) {
		String result = null;
		try {
			InputStream in = this.getResources().getAssets().open(fname);
			int ch = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((ch = in.read()) != -1) {
				baos.write(ch);
			}
			byte[] buff = baos.toByteArray();
			baos.close();
			in.close();
			result = new String(buff, "UTF-8");
			result = result.replaceAll("\\r\\n", "\n");
		} catch (Exception e) {
			Log.i(TAG, "the local file did not find");
			e.printStackTrace();
		}
		return result;
	}

}

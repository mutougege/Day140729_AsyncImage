package com.zzw.asyncimage;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	protected static final int SUCCESS_GET_CONTACT = 0;
	
	private ListView mListView;
	private MyContactAdapter mAdapter;

	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			
			if (msg.what == SUCCESS_GET_CONTACT) {
				
				List<Contact> contacts = (List<Contact>) msg.obj;
				mAdapter = new MyContactAdapter(getApplicationContext(),contacts);
				mListView.setAdapter(mAdapter);
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (ListView) findViewById(R.id.listview);
		ContactService service = new ContactService(getApplicationContext());
		service.getContactAll(mHandler);
	}
}
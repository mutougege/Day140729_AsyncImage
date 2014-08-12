package com.zzw.asyncimage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;

public class ContactService {

	protected static final int SUCCESS_GET_CONTACT = 0;
	protected static final int SUCCESS_GET_IMAGE = 1;
	
	private Context context;
	
	public ContactService(Context context) {
		this.context = context;
	}
	
	/**
	 * �ӷ������ϻ�ȡ����
	 */
	public void getContactAll(final Handler mHandler) {

		// ��ȡ���ݣ���UI�߳��ǲ�������ʱ�����ģ������������߳�����
		new Thread() {
			public void run() {
				try {
					List<Contact> contacts = null;
					AssetManager assetManager = context.getAssets();
					InputStream inputStream = null;

					inputStream = assetManager.open("list.xml");
					// �����ȡ����ֱ�ӷ���XmlPullParser�������
					contacts = xmlParser(inputStream);
					// ���߳�ͨ��Message�����װ��Ϣ�������ó�ʼ���õģ�
					// Handler�����sendMessage()���������ݷ��͵����߳��У��Ӷ��ﵽ����UI���̵߳�Ŀ��
					Message msg = new Message();
					msg.what = SUCCESS_GET_CONTACT;
					msg.obj = contacts;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.e("tag", e.getMessage());
				}
			};
		}.start();

	}

	// ���ﲢû������ͼƬ���������ǰ�ͼƬ�ĵ�ַ����������
	private List<Contact> xmlParser(InputStream is) throws Exception {
		List<Contact> contacts = null;
		Contact contact = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("contacts")) {
					contacts = new ArrayList<Contact>();
				} else if (parser.getName().equals("contact")) {
					contact = new Contact();
					contact.setId(Integer.valueOf(parser.getAttributeValue(0)));
				} else if (parser.getName().equals("name")) {
					contact.setName(parser.nextText());
				} else if (parser.getName().equals("image")) {
					contact.setImage(parser.getAttributeValue(0));
				}
				break;

			case XmlPullParser.END_TAG:
				if (parser.getName().equals("contact")) {
					contacts.add(contact);
				}
				break;
			}
		}
		return contacts;
	}
}
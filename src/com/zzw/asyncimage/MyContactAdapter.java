package com.zzw.asyncimage;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyContactAdapter extends BaseAdapter {

	protected static final int SUCCESS_GET_IMAGE = 0;
	private Context context;
	
	private List<Contact> contacts;
	
	private LayoutInflater mInflater;
	private ImageView iv_header;
	private TextView tv_name;
	
	private ContactService service;

	// ���캯��
	public MyContactAdapter(Context context, List<Contact> contacts) {
		this.context = context;
		this.contacts = contacts;
		service = new ContactService(context);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1��ȡitem,�ٵõ��ؼ�
		// 2 ��ȡ����
		// 3�����ݵ�item
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = mInflater.inflate(R.layout.item, null);
		}

		iv_header = (ImageView) view.findViewById(R.id.iv_header);
		tv_name = (TextView) view.findViewById(R.id.tv_name);

		Contact contact = contacts.get(position);

		// �첽�ļ���ͼƬ
		service.asyncloadImage(iv_header, contact.image);
		tv_name.setText(contact.name);

		return view;
	}
}
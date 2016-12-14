package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.AttributeAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

public class AddGeneralPropertyActivity extends ActionBarActivity {

	Long featureId = 0L;
	List<Attribute> attribList;
	ListView listView;
	final Context context = this;
	AttributeAdapter adapterList;
	Button btnSave,btnBack;
	CommonFunctions cf = CommonFunctions.getInstance();
	int groupId = 0;
	int roleId=0;
	static String serverFeatureId=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());
		
		setContentView(R.layout.activity_add_property_info);		
		
		roleId=CommonFunctions.getRoleID(); 
		btnSave=(Button) findViewById(R.id.btn_save);
		btnBack=(Button) findViewById(R.id.btn_cancel);

		listView = (ListView)findViewById(android.R.id.list);
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
		listView.setEmptyView(emptyText);	

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.AddNewProperty);
		if(toolbar!=null)
			setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
		DBController db = new DBController(context);
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			featureId = extras.getLong("featureid");
			serverFeatureId=extras.getString("Server_featureid");

			String keyword="Property";
			attribList = db.getFeatureGenaralInfo(featureId,keyword,cf.getLocale());
			if(attribList.size()>0)
			{
				groupId = attribList.get(0).getGroupId();
			}
			db.close();
		}

		db.close();

		try {
			adapterList = new AttributeAdapter(context, attribList);
		} 
		catch (Exception e) {

			e.printStackTrace();
		}
		listView.setAdapter(adapterList);

		if(roleId==2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		{
			btnSave.setEnabled(false);			
		}
		btnSave.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{             
				saveData();
			}			
		});


		btnBack.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) {

				finish();
			}
		});

	}
	public void saveData() 
	{
		if(validate())
		{
			try {
				if(groupId==0)
				{
					groupId = cf.getGroupId();
				}
				DBController sqllite = new DBController(context);
				String keyword="Property";
				boolean saveResult = sqllite.saveFormDataTemp(attribList,groupId,featureId,keyword);
				sqllite.close();
				if(saveResult){
					Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					finish();
					Intent myIntent = new Intent(context, AddSocialTenureActivity.class);
					myIntent.putExtra("featureid", featureId);
					myIntent.putExtra("serverFeaterID",serverFeatureId);
					startActivity(myIntent);
				}else{							
					Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				cf.appLog("", e);e.printStackTrace();
				Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
			}
		}
		else{	    	 				
			Toast.makeText(context,R.string.fill_mandatory, Toast.LENGTH_SHORT).show();
		}
	}

	public boolean validate()
	{
		return GuiUtility.validateAttributes(attribList);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if(id == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}

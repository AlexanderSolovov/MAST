package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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

/**
 * @author Prashant.Nigam
 */
public class AddMediaActivity extends ActionBarActivity 
{
	List<Attribute> attribList;
	ListView listView;
	final Context context = this;
	AttributeAdapter	adapterList;
	CommonFunctions cf = CommonFunctions.getInstance();
	int groupId = 0;
	Long featureId = 0L;
	Button btnSave;
	int roleId=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//Initializing context in common functions in case of a crash
		try{CommonFunctions.getInstance().Initialize(getApplicationContext());}catch(Exception e){}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_add_media);
		
		roleId=CommonFunctions.getRoleID();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_add_media);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		btnSave=(Button)findViewById(R.id.btn_save);
		
		if(roleId==2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		  {
			btnSave.setEnabled(false);
			
		  }

		DBController sqllite = new DBController(context);
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			int gId = extras.getInt("groupid");
			featureId = extras.getLong("featureid");
			if (gId != 0) 
			{
				groupId  = gId;
			}
			attribList = sqllite.getMultimediaFormDataByGroupId(groupId,cf.getLocale());
		}
		sqllite.close();

		listView = (ListView) findViewById(R.id.list_view);
		try {
			adapterList = new AttributeAdapter(context, attribList);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		listView.setAdapter(adapterList);

		
		
		btnSave.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{             
				saveData();				
			}			
		});

		/*btnCancel.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
*/

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		finish();
		return true;
	}

	public void saveData() {

		if(validate())
		{
			try {
				DBController sqllite = new DBController(context);
				String keyword="MEDIA";
				boolean saveResult = sqllite.saveFormDataTemp(attribList,groupId,featureId,keyword);
				sqllite.close();
				if(saveResult)
				{					
					Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					finish();
				}else{			
					Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				cf.appLog("", e);e.printStackTrace();
				Toast.makeText(context,R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(context,R.string.fill_mandatory, Toast.LENGTH_SHORT).show();
		}
	}

	public boolean validate()
	{
		return GuiUtility.validateAttributes(attribList);
	}

	@Override
	public void onBackPressed()
	{

		if(roleId==2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
		  {
			finish();
			
		  }
		else{
			
			 //Do nothing
		}
		
	  
	}

}

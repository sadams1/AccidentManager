package com.android.accidentmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AddNewDashboard extends Activity{

	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_add_new);
	}
	public void onPeopleInvolvedClick(View v)
	{
		Intent addPeopleInvolved = new Intent(AddNewDashboard.this,ContactAdd.class);
		startActivity(addPeopleInvolved);
	}
}


package com.smarps.smarps;

import android.app.Application;

import com.parse.Parse;

public class NoteAppApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();

		final String APPLICATION_ID = "rx4we62JFUoxfpCw5Q1AValcPnEviT8YU3bw0Iin";
		final String CLIENT_KEY = "5tSo5GL4NSKzXCJdZrH7fw2NSVIGXTGBtVGRe98S";

		Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
	}

}

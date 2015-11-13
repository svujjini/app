package com.app.android.hashmap;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseTwitterUtils;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "UrSdkyg8gHEGgfptKve0D2jxMgVhdOjVDii784pe", "JKdqxBPZVc6ZHjktH33NW4jbHrhHjZMITEUUa024");
		ParseTwitterUtils.initialize("Nz0q9R39uW7H59rkN82qtqpPo", "jAD2myk7fVywft0wJ90A1cqd6DSNDFa7HatnSeorDvPRFe6bm4");
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
	}
}

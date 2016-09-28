package de.ibh_impex.androidhelputils;

import java.io.IOException;
import java.util.Properties;

import android.content.Context;

public class Config {

	private static final String TAG = "Config";
    private Properties configuration;
    private static final String FILENAME = "config.ini";
    private Context context;

    public void remove(String key)
    {
    	configuration.remove(key);
    }
    
    public void clear()
    {
    	configuration.clear();
    }
    
    public Config(Context context) {
    	this.context = context;
        configuration = new Properties();
    }

    public boolean load() {
        boolean retval = false;

        try {
            configuration.load(context.openFileInput(FILENAME));
            retval = true;
        } catch (IOException e) {
        	e.printStackTrace();
//        	Log.e(TAG, "Configuration error: " + e.getMessage());
        }

        return retval;
    }

    public boolean store() {
        boolean retval = false;

        try {
            configuration.store(context.openFileOutput(FILENAME,Context.MODE_PRIVATE), null);
            retval = true;
        } catch (IOException e) {
        	e.printStackTrace();
//        	Log.e(TAG, "Configuration error: " + e.getMessage());
        }

        return retval;
    }

    public void set(String key, String value) {
        configuration.setProperty(key, value);
    }

    public String get(String key) {
    	if (configuration.getProperty(key) != null)
    		return configuration.getProperty(key);
    	else
    		return "";
    }
}


// http://stackoverflow.com/questions/20932102/execute-shell-command-from-android
package com.startx.android;

import java.lang.*;
import java.io.* ;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class StartXActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	Process p=null;
                try {
        	    TextView text = new TextView(this);
        	    text.setText("Starting X Windows...\n");
        	    setContentView(text);
		    Process su = Runtime.getRuntime().exec("su");
		    DataOutputStream os = new DataOutputStream(su.getOutputStream());              
		    os.writeBytes("sh /data/local/tmp/chroot2slackware.sh\n");
		    text.append("done\n");
		    os.flush();		    
		    os.writeBytes("exit\n");  
		    text.append("exit\n");
		    os.flush();		    
		    try 
 		    {
		      su.waitFor();
		    } catch(Exception e2) {
			e2.printStackTrace();
		    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(p!=null) p.destroy();
                }
    }
}

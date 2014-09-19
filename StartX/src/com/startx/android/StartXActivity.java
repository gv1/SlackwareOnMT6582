package com.startx.android;

import java.io.* ;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import android.app.ActionBar;
import android.view.MenuInflater;


public class StartXActivity extends Activity {
    String value , key;
    SharedPreferences  settings;
    SharedPreferences.Editor editor;
    boolean expert_mode;
    TextView text;
    String startx_script_dir, startx_script;
    String slackware_install_script;
    String slackware_root_dir;
    String slackware_partition;
    String slackware_partition_type;
    String slackware_mount_dir;
    String startx_script_name;
    ActionBar actionBar ;

    @Override
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	settings = PreferenceManager.getDefaultSharedPreferences(this);
	
	expert_mode = boolSetting("expert_mode");	

	if (expert_mode) 
	    setContentView(R.layout.expert);
	else
	    setContentView(R.layout.main);

	text = (TextView)findViewById(R.id.msgbox);
	text.append("[INFO] start.\n");
	loadPreferences();
	
	setupStartX();

	actionBar =  getActionBar();
	actionBar.show();

    }
    
    private boolean test(String mode, String path) {	
	File f = new File(path);
	if (!f.exists()) {
	    text.append("[ERROR]: file not found " + path + "\n"); 
	    return false;
	}
	return true;
    }

    private void installSlackwareInstallScript()
    {
	String start = "#!/system/bin/sh\n" ;
	installFile(start,R.raw.slackware_install, "/data/local/tmp/slackware_install.sh");
    }

    private void installExitScript()
    {
	installFile(null,R.raw.exitfromslackware, "/data/local/tmp/exitfromslackware.sh");
    }

    private void installDotWbar()
    {
        installFile(null,R.raw.wbar, "/data/local/tmp/slackware/root/.wbar");
    }

    private void installXinitrc()
    {
        installFile(null,R.raw.xinitrc, "/data/local/tmp/slackware/root/.xinitrc");
    }


    private void installStartXScript() {	
	String start = "#!/system/bin/sh\n" + 
	    "SWMNTDIR=" + getString(R.string.slackware_mount_dir) + "\n" + 
	    "SWPART=" + getString(R.string.slackware_partition) + "\n" + 
	    "SWPARTTYPE=" + getString(R.string.slackware_partition_type) + "\n" + 
	    "SWROOTDIR=" + getString(R.string.slackware_root_dir) + "\n";
	installFile(start,R.raw.startx,"/data/local/tmp/startx.sh");
    }
    
    private void installFile(String start, int src, String dest) {
	// /data/data/com.startx.android/files/
	if ( src <= 0  ) {
	    text.append("[ERROR] source file for copying not specified\n");
	    return;
	}
	if ( dest == null){
	    text.append("[ERROR] destination not specified\n");
	    return;
	}

	String pwd = getApplicationContext().getFilesDir().toString();
	text.append("[INFO] copying to "  + pwd + "/tmp_src\n");
	File t1 = new File(pwd + "/tmp_src");
	try{
            BufferedInputStream is = new BufferedInputStream(getResources().openRawResource(src));
            FileWriter fout = new FileWriter(t1);
	    if (start != null )
		fout.write(start);
            while(is.available() > 0){
                fout.write(is.read());
            }
            fout.close();
        } catch(Exception e) {
	    text.append("[ERROR] exception : ");
	    text.append(e.getMessage());
        }
	text.append("[INFO] moving " + pwd + "/tmp_src to " + dest + "\n");
	execCommand("mv " + pwd + "/tmp_src " + dest);

    }

    private void installXorgconf() {
	// Unsless startx.sh is executed, slackware is not likely mounted.
	// So, just keep the file ready in /data/local/tmp, for startx.sh to
	// copy to /etc/X11/ 
	installFile(null, R.raw.xorg, "/data/local/tmp/xorg.conf");
    }

    public void onInstallSlackware(View v) {
	installSlackwareInstallScript();
    }
    
    private void execCommand(String cmd) {
        Process p=null;
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            text.append("[INFO] Executing commmand: " + cmd + "\n");
	    os.writeBytes(cmd + "\n");
	    os.flush();
            os.writeBytes("exit\n");
            os.flush(); 
            text.append("[INFO] done\n");
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
    private void runStartX() {
	String script = startx_script_dir + "/" + startx_script;
	if ( test("-f", script) ) {
	    execCommand("sh " + script );
	}
    }
    public void onStartX(View v) {	
	runStartX();
    }
    
    void loadPreferences() {
	
        text.append("[INFO] LoadPreference::\n");

	expert_mode = boolSetting("expert_mode");
	text.append("[INFO] Expert mode : " + expert_mode + "\n");

	startx_script = loadPreference("startx_script_name");
	text.append("[INFO] StartX script : " + startx_script + "\n");
	
	startx_script_dir = loadPreference("startx_script_dir");
	text.append("[INFO] StartX script dir : " + startx_script_dir + "\n");
	
	slackware_install_script  = getString(R.string.slackware_install_script);
	text.append("[INFO] Slackware install script : " + slackware_install_script + "\n");

	slackware_root_dir = getString(R.string.slackware_root_dir);
	text.append("[INFO] Slackware root dir : " + slackware_root_dir + "\n");

	slackware_partition = loadPreference("slackware_partition");
	text.append("[INFO] Partition: " + slackware_partition + "\n");

	slackware_partition_type = loadPreference("slackware_partition_type");
	text.append("[INFO] Partition Type : " + slackware_partition_type + "\n");

	key = "slackware_mount_dir";
	slackware_mount_dir = loadPreference("slackware_mount_dir");
	text.append("[INFO] Mount dir : " + slackware_mount_dir + "\n");
	
        startx_script_dir = loadPreference("startx_script_dir");
	text.append("[INFO] startx script dir : " + "startx_script_dir" + "\n");

        startx_script_name = loadPreference("startx_script_name");
	text.append("[INFO] startx script name: " + startx_script_name + "\n");

    }

    public void onCancel(View v) {	
	System.exit(0);
    }
    
    private String loadPreference(String k) {
	return settings.getString(k, null);
    }
    
    private boolean boolSetting(String k) {
	return settings.getBoolean(k, false);
    }
    
    private void setupStartX() {
	installStartXScript();
	installSlackwareInstallScript();
	installXorgconf();
	installExitScript();
	installDotWbar();
	installXinitrc();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
	inflater.inflate(R.menu.main_activity_actions,menu);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_preferences) {
            doPreferences();
        }
	if (id == R.id.action_preferences) {
	    doPreferences();
	}
	if (id == R.id.action_run) {
	    runStartX();
	}
	if (id == R.id.action_cancel) {
	    System.exit(0);
	}
        return super.onOptionsItemSelected(item);
    }


    private void doPreferences() {
	Intent intent = new Intent(this, StartXPreferencesActivity.class);
        startActivity(intent);
    }


}

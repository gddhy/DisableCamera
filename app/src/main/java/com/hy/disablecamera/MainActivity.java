package com.hy.disablecamera;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.preference.*;
import android.widget.*;

public class MainActivity extends Activity 
{
	private SharedPreferences pref;
    private  SharedPreferences.Editor editor;
	boolean type;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		type = pref.getBoolean("type",false);
		setCamera();
    }
	
	
	private void setCamera(){
		DevicePolicyManager dc = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName mdeviceAdmin=new ComponentName(this,MyAdmin.class);
		if(dc.isAdminActive(mdeviceAdmin)){
			type = !type;
			dc.setCameraDisabled(mdeviceAdmin,type);
			showToast("已"+(type?"禁用":"启用")+"相机");
			editor = pref.edit();
			editor.putBoolean("type",type);
			editor.apply();
			setIcon(type);
			finish();
		}else{
			//需要开启管理员权限
			type = false;
			String str = "请激活设备管理员授予禁用相机权限";
			showToast(str);
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mdeviceAdmin);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,str);
			startActivityForResult(intent, 10086);
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
			setCamera();
        } else {
            showToast("未获得权限，请重试");
			finish();
        }
    }

	
	
	private Toast toast;

    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainActivity.this, null, Toast.LENGTH_LONG);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
	
	
	//程序动态图标设置
    public void setIcon(boolean type){
		ComponentName icon1 = new ComponentName(this,getPackageName()+".MainActivity");
		ComponentName icon2 = new ComponentName(this,getPackageName()+".Main2");
        if (type) {
			enableComponent(icon2);
			disableComponent(icon1);
        } else {
			enableComponent(icon1);
			disableComponent(icon2);
        }
    }

    private void enableComponent(ComponentName componentName){

        getPackageManager().setComponentEnabledSetting(componentName,
															   PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
															   PackageManager.DONT_KILL_APP);
    }
	
    private void disableComponent(ComponentName componentName){
        getPackageManager().setComponentEnabledSetting(componentName,
															   PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
															   PackageManager.DONT_KILL_APP);
    }
}

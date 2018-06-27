package presentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kumar.appupdatedemo.BuildConfig;
import com.kumar.appupdatedemo.R;

import net.wequick.small.Small;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import data.WelcomeMsgRepositoryImpl;
import domain.WelcomeMsgUsecase;

/**
 * Created by kumara on 23/5/18.
 */

public class MainActivity extends AppCompatActivity {
    TextView welocomeText = null;
    private final int PERMISSIONS_REQUEST_STORAGE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welocomeText = findViewById(R.id.welocome_msg);

        Button loginButton = findViewById(R.id.login);
        Button signupButton = findViewById(R.id.signup);
        Button updateButton = findViewById(R.id.id_update);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Small.setUp(MainActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("login",MainActivity.this);
                    }
                });
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Small.setUp(MainActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("signup",MainActivity.this);
                    }
                });
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    upgradeApp();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_STORAGE);
                }

            }
        });

        TextView version = findViewById(R.id.id_version);
        version.setText("version:"+ BuildConfig.VERSION_NAME);

        getMessage();
    }

    private void getMessage(){
        WelcomeMsgRepositoryImpl welcomeMsgRepository = new WelcomeMsgRepositoryImpl();
        WelcomeMsgUsecase welcomeMsgUsecase = new WelcomeMsgUsecase(welcomeMsgRepository);
        welocomeText.setText(welcomeMsgUsecase.getWelcomeMessage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_STORAGE:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    upgradeApp();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Enable storage permission to update App", Toast.LENGTH_SHORT ).show();
                }
                break;
            }
        }
    }

    private void upgradeApp(){
        new Thread(new Runnable() {
            @Override
            public void run() {

//                        {
//                            "version": "1.0.0",
//                                "bundles": [
//                            {
//                                "uri": "main",
//                                    "pkg": "com.example.appmain"
//                            },
//                            {
//                                "uri": "kumar",
//                                    "pkg": "com.example.appkumar"
//                            },
//                            {
//                                "pkg" : "com.example.libstyle"
//                            }
//  ]
//                        }

//                File updateDir = new File(Environment.getExternalStorageDirectory()+File.separator+"Update Folder");
//                if(!updateDir.exists())
//                    return;
//
//                File patchFiles[] = updateDir.listFiles(new FilenameFilter() {
//                    @Override
//                    public boolean accept(File file, String s) {
//                        String fileName = file.getName();
//                        if(fileName.startsWith("libcom_kumar") && fileName.endsWith(".so"))
//                            return true;
//                        return false;
//                    }
//                });
//
//                if(patchFiles.length == 0)
//                    return;

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("version","1.0.0");
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("uri","login");
                    jsonObject1.put("pkg","com.kumar.applogin");
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("uri","signup");
                    jsonObject2.put("pkg","com.kumar.appsignup");
                    jsonArray.put(0,jsonObject1);
                    jsonArray.put(1,jsonObject2);

                    jsonObject.put("bundles",jsonArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!Small.updateManifest(jsonObject, false)) {
                    System.out.println("maifest update failed returning...");
                    return;
                }





                net.wequick.small.Bundle bundle = Small.getBundle("com.kumar.applogin");
                int versionCode = bundle.getVersionCode();
                File parentFolder = bundle.getPatchFile().getParentFile();
                File file = new File(parentFolder,"update_libcom_kumar_applogin.so");

                if(file.exists())
                {
                    file.delete();
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                File updateFolder = new File(Environment.getExternalStorageDirectory()+File.separator+"Update Folder");
                if(!updateFolder.exists())
                    updateFolder.mkdir();

                File inputFile = new File(updateFolder, "libcom_kumar_applogin.so");
                if(!inputFile.exists())
                    return;
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = new FileInputStream(inputFile);
                    os = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if(os!=null)
                            os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(is!=null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//                SharedPreferences Settings = getSharedPreferences("AppUpgrade", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = Settings.edit();
//                editor.putBoolean("app_main",true);
//                editor.commit();

                // Upgrade
//                bundle.upgrade();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"update is done",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:{

                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                    net.wequick.small.Bundle bundle = Small.getBundle("com.kumar.applogin");
                    File parentFolder = bundle.getPatchFile().getParentFile();
                    File file = new File(parentFolder,"update_libcom_kumar_applogin.so");

                    if(file.exists())
                    {
                        File destFile = new File(parentFolder,"libcom_kumar_applogin.so");
                        boolean isRenamed = file.renameTo(destFile);
                        if(isRenamed) {
                            bundle.upgrade();
                            Log.e("app:MainActiivty", "patch applied for applogin.so");
                        }
                        file = new File(parentFolder,"update_libcom_kumar_applogin.so");
                        file.deleteOnExit();
                    }

                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

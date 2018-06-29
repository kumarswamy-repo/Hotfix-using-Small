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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kumar.appupdatedemo.BuildConfig;
import com.kumar.appupdatedemo.BundlesConfig;
import com.kumar.appupdatedemo.Constants;
import com.kumar.appupdatedemo.R;

import net.wequick.small.Small;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import data.WelcomeMsgRepositoryImpl;
import domain.WelcomeMsgUsecase;

/**
 * Created by kumara on 23/5/18.
 */

public class MainActivity extends AppCompatActivity {
    TextView welocomeText = null;
    private final int PERMISSIONS_REQUEST_STORAGE = 100;
    private BundlesConfig mBundlesConfig = null;

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
                        Small.openUri("login", MainActivity.this);
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
                        Small.openUri("signup", MainActivity.this);
                    }
                });
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    upgradeApp();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_STORAGE);
                }

            }
        });

        TextView version = findViewById(R.id.id_version);
        version.setText("version:" + BuildConfig.VERSION_NAME);

        getMessage();
    }

    private void getMessage() {
        WelcomeMsgRepositoryImpl welcomeMsgRepository = new WelcomeMsgRepositoryImpl();
        WelcomeMsgUsecase welcomeMsgUsecase = new WelcomeMsgUsecase(welcomeMsgRepository);
        welocomeText.setText(welcomeMsgUsecase.getWelcomeMessage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    upgradeApp();
                } else {
                    Toast.makeText(MainActivity.this, "Enable storage permission to update App", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void upgradeApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                File updateDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Update Folder");
                if (!updateDir.exists()) {
                    boolean isCreated = updateDir.mkdir();
                    if (!isCreated)
                        return;
                }

                File patchFiles[] = updateDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        return s.startsWith(Constants.LIB_PREFIX) && s.endsWith(Constants.SO_EXT);
                    }
                });

                if (patchFiles.length == 0)
                    return;

                File bundleFile = new File(updateDir, Constants.BUNDLE_FILE);
                if (bundleFile.exists()) {
                    try {
                        Gson gson = new Gson();
                        JsonReader reader = new JsonReader(new FileReader(bundleFile));
                        mBundlesConfig = gson.fromJson(reader, BundlesConfig.class);
                        if (mBundlesConfig != null) {
                            JSONObject manifestObject = new JSONObject(gson.toJson(mBundlesConfig));
                            if (!Small.updateManifest(manifestObject, false)) {
                                System.out.println("maifest update failed returning...");
                                return;
                            }
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(File patchFile:patchFiles){
                    String packageName = getPackageNameFromPatchFile(patchFile.getName());
                    net.wequick.small.Bundle bundle = Small.getBundle(packageName);
                    File patchesFolder = bundle.getPatchFile().getParentFile();
                    File destPatchFile = new File(patchesFolder,Constants.UPDATE_FILE_PREFIX+patchFile.getName());
                    copyPatchFile(patchFile, destPatchFile);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "update is done", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void copyPatchFile(File srcFile, File destFile){
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(srcFile);
            os = new FileOutputStream(destFile);
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
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    List<net.wequick.small.Bundle> bundles = Small.getBundles();
                    for(net.wequick.small.Bundle bundle:bundles){
                        File bundleFile = bundle.getPatchFile();
                        File patchFolder = bundleFile.getParentFile();
                        File patchFile = new File(patchFolder, Constants.UPDATE_FILE_PREFIX + bundleFile.getName());
                        if(patchFile.exists()){
                            boolean isRenamed = patchFile.renameTo(bundleFile);
                            if (isRenamed) {
                                bundle.upgrade();
                                Log.e("app:MainActiivty", "patch applied for "+bundleFile.getName());
                            }

                            patchFile = new File(patchFolder, Constants.UPDATE_FILE_PREFIX + bundleFile.getName());
                            patchFile.deleteOnExit();
                        }
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getPackageNameFromPatchFile(String patchFileName){
        return patchFileName.replaceFirst("lib","").replace(Constants.SO_EXT,"").replace("_",".");
    }
}

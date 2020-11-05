package m.hp.scanqrcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private static final int CAMERA_REQ_CODE = 100;
    private static final int REQUEST_OK = 200;
    private String tag = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //CAMERA_REQ_CODE为用户自定义，用于接收权限校验结果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQ_CODE);
        }
    }

    //实现“onRequestPermissionsResult”函数接收校验权限结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断“requestCode”是否为申请权限时设置请求码CAMERA_REQ_CODE，然后校验权限开启状态
        if (requestCode == CAMERA_REQ_CODE && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //调用扫码接口，构建扫码能力，需开发者实现

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                //“QRCODE_SCAN_TYPE ”和“ DATAMATRIX_SCAN_TYPE表示只扫描QR和Data Matrix的码
                HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setPhotoMode(true).create();
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(this, bitmap, options);
                //处理扫码结果
                if (hmsScans != null && hmsScans.length > 0) {
                    //展示扫码结果
                    showResult(hmsScans);
                    Toast.makeText(this,"发现二维码",Toast.LENGTH_SHORT ).show();
                }else {
                    Toast.makeText(this,"没有发现二维码",Toast.LENGTH_SHORT ).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showResult(HmsScan[] hmsScans) {
        for (int i = 0; i < hmsScans.length; i++){
            Log.d(tag, hmsScans[i].showResult);
        }
    }


    public void onClick(View view) {
        //data是Intent类型，data.getData是待扫描的条码图片Uri
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_OK);
    }
}
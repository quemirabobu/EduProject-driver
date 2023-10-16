package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.example.finalproject.LocationUpdatesService;

import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends FragmentActivity {

    private final int REQUEST_CODE = 101;

    private static final int REQUEST_CAMERA_PERMISSION = 200;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoUri;
    private String currentPhotoPath;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText carnumberEditText = findViewById(R.id.edit_carnumber);
        final EditText phonenumberEditText = findViewById(R.id.edit_phonenumber);
        Button startButton = findViewById(R.id.startButton);  // replace with your button id

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "부릉부릉!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LocationUpdatesService.class);
                intent.putExtra("carnumber", carnumberEditText.getText().toString());
                intent.putExtra("phonenumber", phonenumberEditText.getText().toString());


                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }
            }
        });

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto(v);
            }
        });


    }


    public void capturePhoto(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {

                photoUri = FileProvider.getUriForFile(this, "com.example.finalproject.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath(); // 경로 저장
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 오류가 발생하면 null 반환
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // 이 라인 추가
        final EditText carnumberEditText = findViewById(R.id.edit_carnumber);

        String carNumber = carnumberEditText.getText().toString();


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File file = new File(currentPhotoPath);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", carNumber+"호차마지막사진.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), file)).addFormDataPart("carnumber", carNumber) // car number 추가
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.0.220:8081/receivephoto")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    Log.i("Myapp", "사진보내는거실패함");
                    Log.i("Myapp", e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                   if (response.isSuccessful()) {

                        Log.i("MyApp", "사진보내는거성공함" + response.toString());
                       mHandler.post(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(MainActivity.this, "전송완료!", Toast.LENGTH_SHORT).show();
                           }
                       });

                    } else {

                        Log.i("MyApp", "사진보내는거실패함 이유는" + response.toString());


                        // 실패 처리 코드
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final EditText carnumberEditText = findViewById(R.id.edit_carnumber);
                    final EditText phonenumberEditText = findViewById(R.id.edit_phonenumber);
                    Intent intent = new Intent(this, LocationUpdatesService.class);
                    intent.putExtra("carnumber", carnumberEditText.getText().toString());
                    intent.putExtra("phonenumber", phonenumberEditText.getText().toString());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }
                break;
        }
    }
}

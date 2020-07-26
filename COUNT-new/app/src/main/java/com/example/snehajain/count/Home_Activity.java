package com.example.snehajain.count;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.IOException;
import java.util.ArrayList;

public class Home_Activity extends AppCompatActivity {


    static{
                OpenCVLoader.initDebug();
    }

    Bitmap bitmap;
Mat image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_);
        Button cam=findViewById(R.id.button2);
        cam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Home_Activity.this);
            }
        });

        Button clear = findViewById(R.id.button3);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });


        Button count =findViewById(R.id.button4);
        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(bitmap!=null)
                {

                    image = new Mat();
                    Utils.bitmapToMat(bitmap,image);
                    Log.d("Fn call","About to call counting fn");
                    int count = combinedCount(image);
                    TextView res = findViewById(R.id.result);
                    res.setText(count + " booklets detected");

                }

                else {
                    Toast.makeText(Home_Activity.this,"No image captured/chosen",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OCV Loaded", "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OCV Not Found", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OCV Found", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private int combinedCount(Mat image)
    {   Log.d("cc","In CC");
        Binarization ob2 = new Binarization();
        Mat image1 = new Mat();
        image1 = image.clone();
        int[] limits;
        limits = ob2.cropScript(image);
        int miny = limits[0];
        int maxy = limits[1];
        Smoothening ob1 = new Smoothening();
        ArrayList<Integer> results1 =  ob1.getScriptCount(image,miny,maxy);
        ArrayList<Integer> results2 = ob2.getScriptCount(image1,miny,maxy);
        results1.addAll(results2);
        return ob1.mode(results1);
    }

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ImageView img=findViewById(R.id.imageView);
                img.setImageURI(resultUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

package com.example.facerecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
public class MainActivity extends AppCompatActivity {

    // Flag to control app behavior
    public static int appFlag = 0;
    // UI Variables
    private Button histeqButton;
    private Button sharpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_activity);
        super.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Request User Permission on Camera
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);}

        // Setup Button for Histogram Equalization
        histeqButton = (Button) findViewById(R.id.histeqButton);
        histeqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appFlag = 1;
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

        // Setup Button for Sharpening
        sharpButton = (Button) findViewById(R.id.sharpeButton);
        sharpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appFlag = 2;
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });


    }

    @Override
    protected void onResume(){
        super.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }

}



















//
//
//    public Bitmap displayEigenface() {
//        float[][] eigenFace = getPCA();
//        int[] faceOne = new int[4096];
//        for (int i = 0; i < 4096; i++) {
//            faceOne[i] = (int) eigenFace[i][0]*255;
//        }
//        return Bitmap.createBitmap(faceOne,64,64,Bitmap.Config.ARGB_8888);
//    }
//    public String predictResult() {
//        float finalResult = outputValue();
//        String person;
//        if (finalResult < 0.5) {
//            person = "Ivy";
//        } else {
//            person = "Yaning";
//        }
//        return "ivy";
//
//    }
//    public float[] testArr() {
//        float[] newImageArr = resizeImage2Arr();
//        float[] xMean = xMean();
//        float[] centerImage = new float[64*64];
//        for (int i = 0;i<64*64; i++) {
//            centerImage[i] = newImageArr[i] - xMean[i];
//        }
//
//        float[][] pcaComponents = getPCA(); //size = 4096*15
//        float[] outputArr = new float[15];
//        for (int i = 0; i < 15; i++) {
//            for (int j = 0; j < 4096; j++) {
//                outputArr[i] += centerImage[j]*pcaComponents[j][i];
//            }
//
//        }
//    return outputArr;
//    }
//    public float[][] getPCA() {
//        AssetManager assetManager = getAssets();
//        try {
//            String[] file1 = assetManager.list("pca_components_tp.txt");
//            float[] pcaCompFile = new float [file1.length];
//            for (int i = 0; i < file1.length; i++) {
//                pcaCompFile[i] = Float.valueOf(file1[i]);
//            }
//            float[][] pca_comp = new float[4096][15];
//            for (int i = 0; i < 4096; i++) {
//                for (int j = 0; j < 15; j++) {
//                    pca_comp[i][j] = pcaCompFile[i*15+j];
//                }
//            }
//            return pca_comp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            float[][] wrong = new float[0][0];
//            return wrong;
//        }
//
//    }
//    public float[] xMean() {
//        AssetManager assetManager = getAssets();
//        try {
//            String[] file1 = assetManager.list("x_mean.txt");
//            float[] xMeanFile = new float [file1.length];
//            for (int i = 0; i < file1.length; i++) {
//                xMeanFile[i] = Float.valueOf(file1[i]);
//            }
//            return xMeanFile;
//        } catch (Exception e) {
//            e.printStackTrace();
//            float[] wrong = new float[0];
//            return wrong;
//        }
//
//    }
//    public List<Float> changeRGB(Bitmap bitmap) {
//        Bitmap resized = Bitmap.createScaledBitmap(bitmap,64,64,true);
//        List<Float> bwChannel = new ArrayList<Float>();
//        for (int i = 0; i < 64; i++) {
//            for (int j = 0; j < 64; j++) {
//                 int colour = resized.getPixel(i,j);
//                 int red = Color.red(colour);
//                 int blue = Color.blue(colour);
//                 int green = Color.green(colour);
//                 float bw = (red+blue+green)*(1/3)/255;
//                 bwChannel.add(bw);
//            }
//        }
//    return bwChannel;
//    }
//
//    public float[][] resizeIMage2Matrix(float[] arr) {
////        float[] originalImage = new float[arr.length];
//        float[][] matrixImage = new float[64][64];
//        for (int i = 0; i < 64; i++) {
//            for (int j = 0; j < 64; j++) {
//                matrixImage[i][j] = arr[i*64+j];
//            }
//        }
//    return matrixImage;
//    }
//
//    public float[] resizeImage2Arr() {
//        float[] originalImage = new float[changeRGB(bitmap).size()];
//        for (int i = 0; i < changeRGB(bitmap).size(); i++) {
//            originalImage[i] = changeRGB(bitmap).get(i);
//        }
//        return originalImage;
//    }
//
//    public float outputValue() {
//        //Load the native openCV Library
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        // Set up training data
//        int[] labels = {1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0};
//        double[] doubleArray = { 1949,629,2531,-158,-180,395,-845,-194,124,-574,118,-13,-28,0,0,4687,-794,-1184,-791,-1475,218,237,1609,-291,-66,268,-21,114,76,-11,-2394,-1730,114,21,11,-225,-180,-32,-431,-26,-554,80,43,956,-2,-2507,-1553,-7,81,43,-495,-256,76,-953,-57,37,701,-188,-356,-467,1949,629,2531,-158,-180,395,-845,-194,124,-574,118,-13,-28,0,0,-2742,-1480,-42,-89,78,-182,156,-489,-138,262,1544,-279,162,164,24,3964,-521,-2208,330,2300,924,9,74,-409,-434,49,-95,-22,8,-1,-2065,3846,-565,-46,490,-918,-202,359,151,-177,-74,187,914,-76,42,5018,-259,-1771,-1632,-385,-1077,-187,-1333,259,268,-202,62,-58,-28,3,-2556,-1814,7,-168,-52,72,81,32,235,-70,-488,-522,155,-2,-136,2541,-386,-999,3207,-754,-353,-412,-204,359,319,-40,-41,2,-34,-2,-2328,-1840,-189,-344,-92,260,181,62,683,-101,-332,-616,102,-376,-221,1946,817,1963,544,-43,-404,2062,-356,-188,-464,-93,64,-39,3,2,-1972,-1749,-359,-246,282,437,256,281,1589,-27,90,770,-155,13,190,-2280,3865,-728,-118,310,-908,-128,596,250,-79,116,-319,-847,93,-20,-2502,-1651,-6,-23,-172,-277,-179,32,-918,-60,-276,-51,-116,-396,626,-2600,3422,-1440,-139,-1029,1888,228,-706,-358,259,-110,121,-25,6,-21,1893,568,2355,-268,849,249,25,385,-88,1603,-171,-12,13,-49,-6};
//
//        float[] trainingData = new float[doubleArray.length];
//        for (int i = 0; i < doubleArray.length; i++) {
//            trainingData[i] = (float) doubleArray[i];
//        }
//        float[] testingData = testArr();
//        Mat trainingDataMat = new Mat(18,15,CvType.CV_32FC1);
//        trainingDataMat.put(0,0,trainingData);
//        Mat labelMat = new Mat(18,1, CvType.CV_32SC1);
//        labelMat.put(0,0,labels);
//        SVM svm = SVM.create();
//        svm.setType(SVM.C_SVC);
//        svm.setKernel(SVM.RBF);
//        svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER,100,1e-6));
//        svm.train(trainingDataMat,Ml.ROW_SAMPLE,labelMat);
//        Mat sampleMat = new Mat(1,15, CvType.CV_32F);
//        sampleMat.put(0,0,testingData);
//        float result = svm.predict(sampleMat);
//        return result;
//    }
//}

package com.example.facerecognition;

import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Path;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
// import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import java.io.File;
import java.io.IOException;
import java.lang.Math;
// import java.util.List;


public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    // UI Variable
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView2;
    private SurfaceHolder surfaceHolder2;
    private TextView textHelper;
    private TextView textHelper2;
    // Camera Variable
    private Camera camera;
    boolean previewing = false;
    private int width = 64;
    private int height = 64;
    // Kernels
    private double[][] kernelS = new double[][] {{-1,-1,-1},{-1,9,-1},{-1,-1,-1}};
    private double[][] kernelX = new double[][] {{1,0,-1},{1,0,-1},{1,0,-1}};
    private double[][] kernelY = new double[][] {{1,1,1},{0,0,0},{-1,-1,-1}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        super.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Modify UI Text
        textHelper = findViewById(R.id.Helper);
        textHelper2 = findViewById(R.id.Helper2);
        if(MainActivity.appFlag == 1) textHelper.setText("Histogram Equalized Image");
        else if(MainActivity.appFlag == 2) textHelper.setText("Sharpened Image");


        // Setup Surface View handler
        surfaceView = findViewById(R.id.ViewOrigin);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        surfaceView2 = findViewById(R.id.ViewHisteq);
//        surfaceHolder2 = surfaceView2.getHolder();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Must have to override native method
        return;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!previewing) {
            camera = Camera.open();
            if (camera != null) {
                try {
                    // Modify Camera Settings
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewSize(width, height);
                    // Following lines could log possible camera resolutions, including
                    // 2592x1944;1920x1080;1440x1080;1280x720;640x480;352x288;320x240;176x144;
                    // List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                    // for(int i=0; i<sizes.size(); i++) {
                    //     int height = sizes.get(i).height;
                    //     int width = sizes.get(i).width;
                    //     Log.d("size: ", Integer.toString(width) + ";" + Integer.toString(height));
                    // }
                    camera.setParameters(parameters);
                    camera.setDisplayOrientation(90);
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setPreviewCallback(new PreviewCallback() {
                        public void onPreviewFrame(byte[] data, Camera camera)
                        {
//                            // Lock canvas
//                            Canvas canvas = surfaceHolder2.lockCanvas(null);
                            // Where Callback Happens, camera preview frame ready
                            onCameraFrame(data);
//                            // Unlock canvas
//                            surfaceHolder2.unlockCanvasAndPost(canvas);
                        }
                    });
                    camera.startPreview();
                    previewing = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Cleaning Up
        if (camera != null && previewing) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            previewing = false;
        }
    }

    // Camera Preview Frame Callback Function
    protected void onCameraFrame(byte[] data) {



        // Apply different processing methods
        if (MainActivity.appFlag == 1) {
            //change byte to int
            int[] image = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                image[i] = data[i] & 0xFF;
            }
            float[] binaryImage = new float[image.length];
            for (int i = 0; i < image.length; i++) {
                binaryImage[i] = image[i]/255;
            }
            //load the weights from pca
            AssetManager assetManager = getAssets();
            float[][] pca_comp = new float[4096][15];
            try {
                String[] file1 = assetManager.list("pca_components_tp.txt");
                float[] pcaCompFile = new float [file1.length];
                for (int i = 0; i < file1.length; i++) {
                    pcaCompFile[i] = Float.valueOf(file1[i]);
                }
                for (int i = 0; i < 4096; i++) {
                    for (int j = 0; j < 15; j++) {
                        pca_comp[i][j] = pcaCompFile[i*15+j];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //load x_mean
            AssetManager assetManager2 = getAssets();
            float[] xMeanFile = new float[4096];
            try {
                String[] file1 = assetManager2.list("x_mean.txt");
                for (int i = 0; i < 4096; i++) {
                    xMeanFile[i] = Float.valueOf(file1[i]);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //center new image
            float[] centerImage = new float[64*64];
            for (int i = 0;i<64*64; i++) {
                centerImage[i] = image[i] - xMeanFile[i];
            }
             //size = 4096*15
            float[] outputArr = new float[15];
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 4096; j++) {
                    outputArr[i] += centerImage[j]*pca_comp[j][i];
                }

            }
            //predict the result
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            // Set up training data
            int[] labels = {1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0};
            double[] doubleArray = { 1949,629,2531,-158,-180,395,-845,-194,124,-574,118,-13,-28,0,0,4687,-794,-1184,-791,-1475,218,237,1609,-291,-66,268,-21,114,76,-11,-2394,-1730,114,21,11,-225,-180,-32,-431,-26,-554,80,43,956,-2,-2507,-1553,-7,81,43,-495,-256,76,-953,-57,37,701,-188,-356,-467,1949,629,2531,-158,-180,395,-845,-194,124,-574,118,-13,-28,0,0,-2742,-1480,-42,-89,78,-182,156,-489,-138,262,1544,-279,162,164,24,3964,-521,-2208,330,2300,924,9,74,-409,-434,49,-95,-22,8,-1,-2065,3846,-565,-46,490,-918,-202,359,151,-177,-74,187,914,-76,42,5018,-259,-1771,-1632,-385,-1077,-187,-1333,259,268,-202,62,-58,-28,3,-2556,-1814,7,-168,-52,72,81,32,235,-70,-488,-522,155,-2,-136,2541,-386,-999,3207,-754,-353,-412,-204,359,319,-40,-41,2,-34,-2,-2328,-1840,-189,-344,-92,260,181,62,683,-101,-332,-616,102,-376,-221,1946,817,1963,544,-43,-404,2062,-356,-188,-464,-93,64,-39,3,2,-1972,-1749,-359,-246,282,437,256,281,1589,-27,90,770,-155,13,190,-2280,3865,-728,-118,310,-908,-128,596,250,-79,116,-319,-847,93,-20,-2502,-1651,-6,-23,-172,-277,-179,32,-918,-60,-276,-51,-116,-396,626,-2600,3422,-1440,-139,-1029,1888,228,-706,-358,259,-110,121,-25,6,-21,1893,568,2355,-268,849,249,25,385,-88,1603,-171,-12,13,-49,-6};

            float[] trainingData = new float[doubleArray.length];
            for (int i = 0; i < doubleArray.length; i++) {
                trainingData[i] = (float) doubleArray[i];
            }
            float[] testingData = outputArr;
            Mat trainingDataMat = new Mat(18,15,CvType.CV_32FC1);
            trainingDataMat.put(0,0,trainingData);
            Mat labelMat = new Mat(18,1, CvType.CV_32SC1);
            labelMat.put(0,0,labels);
            SVM svm = SVM.create();
            svm.setType(SVM.C_SVC);
            svm.setKernel(SVM.RBF);
            svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER,100,1e-6));
            svm.train(trainingDataMat,Ml.ROW_SAMPLE,labelMat);
            Mat sampleMat = new Mat(1,15, CvType.CV_32F);
            sampleMat.put(0,0,testingData);
            float result = svm.predict(sampleMat);
            String outputName;
            if (result < 0.5) {
                outputName = "ivy";
            } else {
                outputName = "Yaning";
            }
            textHelper2.setText("1");
//            textHelper2.setText(outputName);

        }

    }}
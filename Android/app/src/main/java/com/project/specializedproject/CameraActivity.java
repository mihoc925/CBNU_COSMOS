/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.specializedproject;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.specializedproject.env.ImageUtils;
import com.project.specializedproject.env.Logger;
import com.project.specializedproject.tflite.Classifier.Device;
import com.project.specializedproject.tflite.Classifier.Model;
import com.project.specializedproject.tflite.Classifier.Recognition;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        SurfaceHolder.Callback{
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private LinearLayout bottomSheetLayout;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior<LinearLayout> sheetBehavior;
  protected TextView recognitionTextView,
      recognition1TextView,
      recognition2TextView,
      recognitionValueTextView,
      recognition1ValueTextView,
      recognition2ValueTextView;
  protected TextView maxItem,
          maxItem1,
          maxItem2,
          maxItemValue,
          maxItemValue1,
          maxItemValue2;
  protected TextView targetsItem, // 임시 변수용 뷰로 수정@
          targetsItem1,
          targetsItem2,
          targetsItemValue,
          targetsItemValue1,
          targetsItemValue2;
  protected ImageView targets_item_image,
          targets_item_image1,
          targets_item_image2;
  protected TextView message_item;
  protected RelativeLayout model_item;

  protected ImageView bottomSheetArrowImageView;
  private ImageView plusImageView, minusImageView;
  private Spinner modelSpinner;
  private Spinner deviceSpinner;
  private TextView threadsTextView;

  private Model model = Model.QUANTIZED_EFFICIENTNET;
  private Device device = Device.CPU;
  private int numThreads = -1;

  // 추가 설정
  private Button photo_btn, set_btn;
  ArrayList<ModelFeed> mFeed = new ArrayList<>();
  String state = "write"; // 작성, 미션
  int content = 0; // 번호
  String fid = ""; // mission fid 불러오기
  boolean missionBool = true; // 한 번만 실행 체크
  String maxTxt0, maxTxt1, maxTxt2; // 최대 감지 아이템 text
  float maxFlo0, maxFlo1, maxFlo2; // 최대 감지 아이템 value
  boolean maxBlo0 = true, maxBlo1 = true, maxBlo2 = true;
  String fd_item1, fd_item2, fd_item3, fd_item_value1, fd_item_value2, fd_item_value3; // 뷰 바인딩 매칭
  int tImageNum = 0; // 점수
  boolean mission0 = false, mission1 = false, mission2 = false; // 미션 체크
  private static int adjustmentValue = 2; // 보정값 (n/2)
  int[] recognitionCheck = new int[3];

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.tfe_ic_activity_camera);

    Intent intent = getIntent();
    state = intent.getExtras().getString("state");
    content = intent.getExtras().getInt("content");
    fid = intent.getExtras().getString("fid");

    for(int i=0; i<recognitionCheck.length; i++) // 초기화
      recognitionCheck[i] = 0;

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }

    threadsTextView = findViewById(R.id.threads);
    plusImageView = findViewById(R.id.plus);
    minusImageView = findViewById(R.id.minus);
    modelSpinner = findViewById(R.id.model_spinner);
    deviceSpinner = findViewById(R.id.device_spinner);
    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);
    photo_btn = findViewById(R.id.photo_btn);
    set_btn = findViewById(R.id.set_btn);
    photo_btn.setOnClickListener(this::onClick);
    set_btn.setOnClickListener(this::onClick);

    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
              gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
              gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            //                int width = bottomSheetLayout.getMeasuredWidth();
            int height = gestureLayout.getMeasuredHeight();

            sheetBehavior.setPeekHeight(height);
          }
        });
    sheetBehavior.setHideable(false);

    sheetBehavior.setBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
              case BottomSheetBehavior.STATE_HIDDEN:
                break;
              case BottomSheetBehavior.STATE_EXPANDED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                }
                break;
              case BottomSheetBehavior.STATE_COLLAPSED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                }
                break;
              case BottomSheetBehavior.STATE_DRAGGING:
                break;
              case BottomSheetBehavior.STATE_SETTLING:
                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                break;
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

    recognitionTextView = findViewById(R.id.detected_item);
    recognitionValueTextView = findViewById(R.id.detected_item_value);
    recognition1TextView = findViewById(R.id.detected_item1);
    recognition1ValueTextView = findViewById(R.id.detected_item1_value);
    recognition2TextView = findViewById(R.id.detected_item2);
    recognition2ValueTextView = findViewById(R.id.detected_item2_value);

    maxItem = findViewById(R.id.max_item);
    maxItem1 = findViewById(R.id.max_item1);
    maxItem2 = findViewById(R.id.max_item2);
    maxItemValue = findViewById(R.id.max_item_value);
    maxItemValue1 = findViewById(R.id.max_item1_value);
    maxItemValue2 = findViewById(R.id.max_item2_value);

    targetsItem = findViewById(R.id.targets_item);
    targetsItem1 = findViewById(R.id.targets1_item);
    targetsItem2 = findViewById(R.id.targets_item2);
    targetsItemValue = findViewById(R.id.targets_item_value);
    targetsItemValue1 = findViewById(R.id.targets_item1_value);
    targetsItemValue2 = findViewById(R.id.targets_item2_value);

    targets_item_image = findViewById(R.id.targets_item_image);
    targets_item_image1 = findViewById(R.id.targets_item_image1);
    targets_item_image2 = findViewById(R.id.targets_item_image2);

    message_item = findViewById(R.id.message_item);
    model_item = findViewById(R.id.model_item);

    modelSpinner.setOnItemSelectedListener(this);
    deviceSpinner.setOnItemSelectedListener(this);

    plusImageView.setOnClickListener(this);
    minusImageView.setOnClickListener(this);

    model = Model.valueOf(modelSpinner.getSelectedItem().toString().toUpperCase());
    device = Device.valueOf(deviceSpinner.getSelectedItem().toString());
    numThreads = Integer.parseInt(threadsTextView.getText().toString().trim());
  }

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
            .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
      CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
            (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new com.project.specializedproject.LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {

    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  @UiThread
  protected void showResultsInBottomSheet(List<Recognition> results) {
    if (results != null && results.size() >= 3) {
      Recognition recognition = results.get(0);
      if (recognition != null) {
        if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
        if (recognition.getConfidence() != null)
          recognitionValueTextView.setText(
              String.format("%.2f", (100 * recognition.getConfidence())) + "%");


        if(recognition.getTitle() != null && recognition.getConfidence() != null) {
          if (maxBlo0 == true) { // 초기
            maxBlo0 = false;
            maxTxt0 = recognition.getTitle();
            maxFlo0 = ( recognition.getConfidence() * 100 );

            if(state.equals("write")) {
              maxItem.setText(maxTxt0);
              maxItemValue.setText(String.format("%.2f", (maxFlo0)) + "%");
            }else if(state.equals("mission")){
              targetsItem.setText(maxTxt0);
              targetsItemValue.setText(String.format("%.2f", (maxFlo0)) + "%");
            }
          }

          if (maxTxt0.equals(recognition.getTitle())) { // 같은 이름
            if (maxFlo0 < ( recognition.getConfidence() * 100 )) { // 갱신
              maxFlo0 = ( recognition.getConfidence() * 100 );
              if(state.equals("write")) {
                maxItemValue.setText(String.format("%.2f", (maxFlo0)) + "%");
              }else if(state.equals("mission")){
                targetsItemValue.setText(String.format("%.2f", (maxFlo0)) + "%");
              }
            }
          } else { // 다른 이름
            if (maxFlo0 <= ( recognition.getConfidence() * 100 )) { // 변경
              maxTxt0 = recognition.getTitle();
              maxFlo0 = ( recognition.getConfidence() * 100 );

              if(state.equals("write")) {
                maxItem.setText(maxTxt0);
                maxItemValue.setText(String.format("%.2f", (maxFlo0)) + "%");
              }else if(state.equals("mission")){
                targetsItem.setText(maxTxt0);
                targetsItemValue.setText(String.format("%.2f", (maxFlo0)) + "%");
              }
            }
          }
        }
      }

      Recognition recognition1 = results.get(1);
      if (recognition1 != null) {
        if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
        if (recognition1.getConfidence() != null)
          recognition1ValueTextView.setText(
              String.format("%.2f", (100 * recognition1.getConfidence())) + "%");

        if(recognition1.getTitle() != null && recognition1.getConfidence() != null) {
          if (maxBlo1 == true) { // 초기
            maxBlo1 = false;
            maxTxt1 = recognition1.getTitle();
            maxFlo1 = ( recognition1.getConfidence() * 100 );

            if(state.equals("write")) {
              maxItem1.setText(maxTxt1);
              maxItemValue1.setText(String.format("%.2f", (maxFlo1)) + "%");
            }else if(state.equals("mission")){
              targetsItem1.setText(maxTxt1);
              targetsItemValue1.setText(String.format("%.2f", (maxFlo1)) + "%");
            }
          }

          if (maxTxt1.equals(recognition1.getTitle())) { // 같은 이름
            if (maxFlo1 < ( recognition1.getConfidence() * 100 )) { // 갱신
              maxFlo1 = ( recognition1.getConfidence() * 100 );
              if(state.equals("write")) {
                maxItemValue1.setText(String.format("%.2f", (maxFlo1)) + "%");
              }else if(state.equals("mission")){
                targetsItemValue1.setText(String.format("%.2f", (maxFlo1)) + "%");
              }
            }
          } else { // 다른 이름
            if (maxFlo1 <= ( recognition1.getConfidence() * 100 )) { // 변경
              maxTxt1 = recognition1.getTitle();
              maxFlo1 = ( recognition1.getConfidence() * 100 );

              if(state.equals("write")) {
                maxItem1.setText(maxTxt1);
                maxItemValue1.setText(String.format("%.2f", (maxFlo1)) + "%");
              }else if(state.equals("mission")){
                targetsItem1.setText(maxTxt1);
                targetsItemValue1.setText(String.format("%.2f", (maxFlo1)) + "%");
              }
            }
          }
        }
      }


      Recognition recognition2 = results.get(2);
      if (recognition2 != null) {
        if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
        if (recognition2.getConfidence() != null)
          recognition2ValueTextView.setText(
              String.format("%.2f", (100 * recognition2.getConfidence())) + "%");

        if(recognition2.getTitle() != null && recognition2.getConfidence() != null) {
          if (maxBlo2 == true) { // 초기
            maxBlo2 = false;
            maxTxt2 = recognition2.getTitle();
            maxFlo2 = ( recognition2.getConfidence() * 100 );

            if(state.equals("write")) {
              maxItem2.setText(maxTxt2);
              maxItemValue2.setText(String.format("%.2f", (maxFlo2)) + "%");
            }else if(state.equals("mission")){
              targetsItem2.setText(maxTxt2);
              targetsItemValue2.setText(String.format("%.2f", (maxFlo2)) + "%");
            }
          }

          if (maxTxt2.equals(recognition2.getTitle())) { // 같은 이름
            if (maxFlo2 < ( recognition2.getConfidence() * 100 )) { // 갱신
              maxFlo2 = ( recognition2.getConfidence() * 100 );
              if(state.equals("write")) {
                maxItemValue2.setText(String.format("%.2f", (maxFlo2)) + "%");
              }else if(state.equals("mission")){
                targetsItemValue2.setText(String.format("%.2f", (maxFlo2)) + "%");
              }
            }
          } else { // 다른 이름
            if (maxFlo2 <= ( recognition2.getConfidence() * 100 )) { // 변경
              maxTxt2 = recognition2.getTitle();
              maxFlo2 = ( recognition2.getConfidence() * 100 );

              if(state.equals("write")) {
                maxItem2.setText(maxTxt2);
                maxItemValue2.setText(String.format("%.2f", (maxFlo2)) + "%");
              }else if(state.equals("mission")){
                targetsItem2.setText(maxTxt2);
                targetsItemValue2.setText(String.format("%.2f", (maxFlo2)) + "%");
              }
            }
          }
        }
      }

      // 한 번만 실행
      if(state.equals("write") && missionBool == true){
        missionBool = false;
        targets_item_image.setVisibility(View.GONE);
        targets_item_image1.setVisibility(View.GONE);
        targets_item_image2.setVisibility(View.GONE);
        model_item.setVisibility(View.GONE);
        message_item.setText("1. 사물을 인식해 주세요. ( 보정값 : "+(100/adjustmentValue)+"% )\n" +
                "2. 결과를 완료하고 촬영한 사진을 업로드해 주세요.");
      }

      if(state.equals("mission") && missionBool == true){
        missionBool = false;
        targets_item_image.setVisibility(View.VISIBLE);
        targets_item_image1.setVisibility(View.VISIBLE);
        targets_item_image2.setVisibility(View.VISIBLE);
        model_item.setVisibility(View.VISIBLE);
        message_item.setText("1. 달성 목표의 사물을 인식해주세요\n" +
                "2. 해당 분석률에 도달하면 자동으로 종료됩니다.\n" +
                "3. Threads를 올리면 더 빠른 분석이 가능합니다.");
        searchMission();
      }

      // 반복 실행
      if(state.equals("mission")){
        if(mission0 == false) {
          String tStr = maxItem.getText().toString();
          String vStr = maxItemValue.getText().toString();
          String rStr = recognition.getTitle();
          float fStr = recognition.getConfidence();

          if (rStr.equals(tStr)) {
            if (tStr != null && !tStr.equals("")) {
              String targetFlo = vStr.substring(0, vStr.length() - 1);

              if ((fStr * 100) >= Float.parseFloat(targetFlo)) {
                targets_item_image.setImageResource(R.drawable.check_detected_verification);
                tImageNum += 60;
                recognitionCheck[0] = 1;
                mission0 = true;
              }
            }
          }
        }
        if(mission1 == false) {
          String tStr1 = maxItem1.getText().toString();
          String vStr1 = maxItemValue1.getText().toString();
          String rStr1 = recognition1.getTitle();
          float fStr1 = recognition1.getConfidence();

          if (rStr1.equals(tStr1)) {
            if (tStr1 != null && !tStr1.equals("")) {
              String targetFlo1 = vStr1.substring(0, vStr1.length() - 1);

              if ((fStr1 * 100) >= Float.parseFloat(targetFlo1)) {
                targets_item_image1.setImageResource(R.drawable.check_detected_verification);
                tImageNum += 30;
                recognitionCheck[1] = 1;
                mission1 = true;
              }
            }
          }
        }
        if(mission2 == false) {
          String tStr2 = maxItem2.getText().toString();
          String vStr2 = maxItemValue2.getText().toString();
          String rStr2 = recognition2.getTitle();
          float fStr2 = recognition2.getConfidence();

          if (rStr2.equals(tStr2)) {
            if (tStr2 != null && !tStr2.equals("")) {
              String targetFlo2 = vStr2.substring(0, vStr2.length() - 1);

              if ((fStr2 * 100) >= Float.parseFloat(targetFlo2)) {
                targets_item_image2.setImageResource(R.drawable.check_detected_verification);
                tImageNum += 10;
                recognitionCheck[2] = 1;
                mission2 = true;
              }
            }
          }
        }
      }

      if(tImageNum == 100){
        endScore();
      }
    }
  }

  private void endScore(){
    Intent intent = new Intent(this, Mission.class);
    intent.putExtra("fid",  fid); // 피드 번호
    intent.putExtra("content",  content); // 도전과제 번호
    intent.putExtra("score",  tImageNum); // 점수
    intent.putExtra("recognitionCheck",  recognitionCheck); // content int 0/1
    startActivity(intent);
    finish();
  }

  private void searchMission() {
    Query searchData = FirebaseDatabase.getInstance().getReference("Feed").orderByChild("fid").equalTo(fid);
    searchData.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Map map = (Map) snapshot.getValue();
            Iterator<String> Iterator = map.keySet().iterator();
            while (Iterator.hasNext()) {
              Iterator.next();

              setString(content);

              maxItem.setText((String) map.get(fd_item1));
              maxItem1.setText((String) map.get(fd_item2));
              maxItem2.setText((String) map.get(fd_item3));
              maxItemValue.setText((String) map.get(fd_item_value1));
              maxItemValue1.setText((String) map.get(fd_item_value2));
              maxItemValue2.setText((String) map.get(fd_item_value3));
            }
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
  }

  private void setString(int i){ // 뷰 바인딩 매칭
    switch (i){
      case 1:
        fd_item1 = "fd_item11";
        fd_item2 = "fd_item12";
        fd_item3 = "fd_item13";
        fd_item_value1 = "fd_item_value11";
        fd_item_value2 = "fd_item_value12";
        fd_item_value3 = "fd_item_value13";
        break;
      case 2:
        fd_item1 = "fd_item21";
        fd_item2 = "fd_item22";
        fd_item3 = "fd_item23";
        fd_item_value1 = "fd_item_value21";
        fd_item_value2 = "fd_item_value22";
        fd_item_value3 = "fd_item_value23";
        break;
      case 3:
        fd_item1 = "fd_item31";
        fd_item2 = "fd_item32";
        fd_item3 = "fd_item33";
        fd_item_value1 = "fd_item_value31";
        fd_item_value2 = "fd_item_value32";
        fd_item_value3 = "fd_item_value33";
        break;
      case 4:
        fd_item1 = "fd_item41";
        fd_item2 = "fd_item42";
        fd_item3 = "fd_item43";
        fd_item_value1 = "fd_item_value41";
        fd_item_value2 = "fd_item_value42";
        fd_item_value3 = "fd_item_value43";
        break;
      case 5:
        fd_item1 = "fd_item51";
        fd_item2 = "fd_item52";
        fd_item3 = "fd_item53";
        fd_item_value1 = "fd_item_value51";
        fd_item_value2 = "fd_item_value52";
        fd_item_value3 = "fd_item_value53";
        break;
    }
  }

  protected Model getModel() {
    return model;
  }

  private void setModel(Model model) {
    if (this.model != model) {
      LOGGER.d("Updating  model: " + model);
      this.model = model;
      onInferenceConfigurationChanged();
    }
  }

  protected Device getDevice() {
    return device;
  }

  private void setDevice(Device device) {
    if (this.device != device) {
      LOGGER.d("Updating  device: " + device);
      this.device = device;
      final boolean threadsEnabled = device == Device.CPU;
      plusImageView.setEnabled(threadsEnabled);
      minusImageView.setEnabled(threadsEnabled);
      threadsTextView.setText(threadsEnabled ? String.valueOf(numThreads) : "N/A");
      onInferenceConfigurationChanged();
    }
  }

  protected int getNumThreads() {
    return numThreads;
  }

  private void setNumThreads(int numThreads) {
    if (this.numThreads != numThreads) {
      LOGGER.d("Updating  numThreads: " + numThreads);
      this.numThreads = numThreads;
      onInferenceConfigurationChanged();
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.plus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 9) return;
      setNumThreads(++numThreads);
      threadsTextView.setText(String.valueOf(numThreads));
    } else if (v.getId() == R.id.minus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 1) {
        return;
      }
      setNumThreads(--numThreads);
      threadsTextView.setText(String.valueOf(numThreads));

    } else if (v.getId() == R.id.photo_btn){
      
      captureImg(); // 촬영

    } else if (v.getId() == R.id.set_btn){
      if(state.equals("write")){
        if(content == 1) {
          ((FeedWrite) FeedWrite.feedW_Context).fd_mission1.setVisibility(View.VISIBLE);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item11.setText(maxTxt0);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item12.setText(maxTxt1);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item13.setText(maxTxt2);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value11.setText(String.format("%.2f", (maxFlo0 / adjustmentValue)) + "%");
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value12.setText(String.format("%.2f", (maxFlo1 / adjustmentValue)) + "%");
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value13.setText(String.format("%.2f", (maxFlo2 / adjustmentValue)) + "%");
        }else if(content == 2) {
          ((FeedWrite) FeedWrite.feedW_Context).fd_mission2.setVisibility(View.VISIBLE);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item21.setText(maxTxt0);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item22.setText(maxTxt1);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item23.setText(maxTxt2);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value21.setText(String.format("%.2f", (maxFlo0 / adjustmentValue)) + "%");
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value22.setText(String.format("%.2f", (maxFlo1 / adjustmentValue)) + "%");
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value23.setText(String.format("%.2f", (maxFlo2 / adjustmentValue)) + "%");
        }else if(content == 3) {
          ((FeedWrite) FeedWrite.feedW_Context).fd_mission3.setVisibility(View.VISIBLE);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item31.setText(maxTxt0);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item32.setText(maxTxt1);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item33.setText(maxTxt2);
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value31.setText(String.format("%.2f", (maxFlo0 / adjustmentValue)) + "%");
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value32.setText(String.format("%.2f", (maxFlo1 / adjustmentValue)) + "%");
          ((FeedWrite) FeedWrite.feedW_Context).fd_item_value33.setText(String.format("%.2f", (maxFlo2 / adjustmentValue)) + "%");
        }

        finish();
        captureImg();

      }else if(state.equals("mission")){
        new AlertDialog.Builder(this).setTitle("미션 종료").setMessage("목표를 달성하지 않았습니다 :(\n그래도 종료할까요?")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            endScore();
          }})
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int whichButton) {
                  }
                }).show();
      }
    }
  }

  private File file;

  public void captureImg() {
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    file = new File(path, "capture.png");
    Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
            BuildConfig.APPLICATION_ID + ".provider", file);

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    startActivityForResult(intent, 101);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inSampleSize = 8;
      Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

      Log.e("TAG", "bit = "+bitmap );
//      imageView.setImageBitmap(bitmap);
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    if (parent == modelSpinner) {
      setModel(Model.valueOf(parent.getItemAtPosition(pos).toString().toUpperCase()));
    } else if (parent == deviceSpinner) {
      setDevice(Device.valueOf(parent.getItemAtPosition(pos).toString()));
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onBackPressed() { // 뒤로가기 버튼
    if(state.equals("mission")){
      Intent intent = new Intent(this, FeedDetail.class);
      intent.putExtra("fid", fid);
      startActivity(intent);
    }else
      super.onBackPressed();
  }
}

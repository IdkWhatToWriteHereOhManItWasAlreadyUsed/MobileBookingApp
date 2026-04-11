package com.example.mobilebookingapp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mobilebookingapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = "AddPostActivity";
    private static final String IMAGEKIT_PRIVATE_KEY = "private_X9bJYickXVcqhg3uspwCaomom3s=";
    private static final String IMAGEKIT_UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload";

    private EditText postText;
    private ImageView previewImage;
    private Uri selectedImageUri;
    private Uri cameraImageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private OkHttpClient httpClient;
    private ExecutorService executorService;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    showPreview(uri);
                }
            }
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    selectedImageUri = cameraImageUri;
                    showPreview(selectedImageUri);
                }
            }
    );

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Нужно разрешение на камеру", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        httpClient = new OkHttpClient();
        executorService = Executors.newSingleThreadExecutor();

        postText = findViewById(R.id.postText);
        previewImage = findViewById(R.id.previewImage);
        MaterialButton selectImageBtn = findViewById(R.id.selectImageBtn);
        MaterialButton publishBtn = findViewById(R.id.publishBtn);

        selectImageBtn.setOnClickListener(v -> showImageSourceDialog());
        publishBtn.setOnClickListener(v -> publishPost());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void showImageSourceDialog() {
        String[] options = {"Галерея", "Камера"};
        new AlertDialog.Builder(this)
                .setTitle("Выберите фото")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        pickImageLauncher.launch("image/*");
                    } else {
                        requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
                    }
                })
                .show();
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(cameraImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка при создании файла", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }

    private void showPreview(Uri uri) {
        previewImage.setImageURI(uri);
        previewImage.setVisibility(View.VISIBLE);
    }

    private void publishPost() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Для публикации нужно войти в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = postText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Введите текст поста", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String userName = mAuth.getCurrentUser().getEmail();

        if (selectedImageUri != null) {
            uploadImageToImageKitAndSave(userId, userName, text);
        } else {
            savePost(userId, userName, text, null);
        }
    }

    private String getAuthToken() {
        String credentials = IMAGEKIT_PRIVATE_KEY + ":";
        return android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);
    }

    private void uploadImageToImageKitAndSave(String userId, String userName, String text) {
        runOnUiThread(() -> Toast.makeText(this, "Загрузка изображения...", Toast.LENGTH_LONG).show());

        executorService.execute(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                if (inputStream == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Не удалось открыть изображение", Toast.LENGTH_SHORT).show());
                    return;
                }
                byte[] imageBytes = new byte[inputStream.available()];
                inputStream.read(imageBytes);
                inputStream.close();

                String fileName = "posts_" + System.currentTimeMillis() + ".jpg";

                String mimeType = getContentResolver().getType(selectedImageUri);
                if (mimeType == null) {
                    mimeType = "image/jpeg";
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName,
                                RequestBody.create(MediaType.parse(mimeType), imageBytes))
                        .addFormDataPart("fileName", fileName)
                        .build();

                Request request = new Request.Builder()
                        .url(IMAGEKIT_UPLOAD_URL)
                        .addHeader("Authorization", "Basic " + getAuthToken())
                        .post(requestBody)
                        .build();

                Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String imageUrl = jsonResponse.getString("url");

                    runOnUiThread(() -> {
                        savePost(userId, userName, text, imageUrl);
                    });
                } else {
                    String errorBody = "";
                    if (response.body() != null) {
                        errorBody = response.body().string();
                    }
                    Log.e(TAG, "Ошибка загрузки в ImageKit.io: " + response.code() + " " + response.message() + " " + errorBody);
                    runOnUiThread(() -> {
                        Toast.makeText(AddPostActivity.this, "Ошибка загрузки фото: " + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Исключение при загрузке в ImageKit.io: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(AddPostActivity.this, "Произошла ошибка при загрузке фото: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void savePost(String userId, String userName, String text, String imageUrl) {
        HashMap<String, Object> post = new HashMap<>();
        post.put("userId", userId);
        post.put("userName", userName != null ? userName : "Пользователь");
        post.put("text", text);
        post.put("imageUrl", imageUrl);
        post.put("createdAt", FieldValue.serverTimestamp());

        db.collection("travelPosts").add(post)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Пост опубликован!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка публикации: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
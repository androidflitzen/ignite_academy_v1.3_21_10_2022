package com.anzo.igniteacademy.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Adapter.DocumentListAdapter;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Helper.SingleFileUploadInterface;
import com.anzo.igniteacademy.Helper.SingleUploadFileBrodCastReceiver;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.DocumentListModel;
import com.anzo.igniteacademy.Model.EnquiryListModel;
import com.anzo.igniteacademy.R;

import com.anzo.igniteacademy.databinding.ActivityEnrollmentDetailBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.google.gson.Gson;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.internal.Util;

public class EnrollmentDetailActivity extends BaseActivity {

    ActivityEnrollmentDetailBinding binding;
    SingleUploadFileBrodCastReceiver uploadReceiver;
    String uploadId;
    Dialog dialog;
    Bitmap bitmap;

    private int enquiryId = 0;
    private long downloadID;
    private int serverResponseCode = 0;
    private EnquiryListModel enquiryDetails;
    private SingleFileUploadInterface listener;
    String fileUrl;

    DocumentListAdapter documentListAdapter;
    List<DocumentListModel> documentListModels = new ArrayList<>();

    public static final int IMAGE_PICK_REQUEST = 101;
    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private static final int REQUEST_CROP = 500;
    String cameraPermission[];

    public String imagePath = null;

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if (downloadID == id){
                Toast.makeText(EnrollmentDetailActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnrollmentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uploadReceiver = new SingleUploadFileBrodCastReceiver();

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        enquiryId = getIntent().getIntExtra("EnquiryId", 0);
        //Toast.makeText(EnrollmentDetailActivity.this, String.valueOf(enquiryId), Toast.LENGTH_SHORT).show();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.rvDocumentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        documentListAdapter = new DocumentListAdapter(this, documentListModels);
        binding.rvDocumentList.setAdapter(documentListAdapter);
        documentListAdapter.setOnDeleteDocument(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                showDeleteDialog(position);
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_PICK_REQUEST);
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadDocument(imagePath);
            }
        });
        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                intent.putExtra("crop", "true");
//                intent.putExtra("scale", true);
//                intent.putExtra("outputX", 256);
//                intent.putExtra("outputY", 256);
//                intent.putExtra("aspectX", 1);
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("return-data", true);
                startActivityForResult(intent, IMAGEPICK_GALLERY_REQUEST);
            }
        });
        getEnrollmentDetail();
        getDocument();

    }

    private void showDeleteDialog(int position) {
        dialog = new Dialog(EnrollmentDetailActivity.this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_document);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        ImageView btnDeleteDocument = (ImageView) dialog.findViewById(R.id.ivDeleteDocument);
        ImageView btnDownloadDocument = (ImageView) dialog.findViewById(R.id.ivDownloadDocument);

        btnDeleteDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new AlertDialog.Builder(EnrollmentDetailActivity.this)
                        .setTitle("Delete Document")
                        .setMessage("Are you sure you want to delete document")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDocument(position);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
        btnDownloadDocument.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                downloadDocument(documentListModels.get(position).document);
            }
        });
        dialog.show();
    }

    /*private void downloadDocument(String document) {

        bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_docx);
        try {
            //saveBitmap(EnrollmentDetailActivity.this, bitmap, "image/bmp", System.currentTimeMillis() + "/igniteMedia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @NonNull
    private Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                           @NonNull final String mimeType,
                           @NonNull final String displayName) throws IOException {

        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        final ContentResolver resolver = context.getContentResolver();

        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }else{
                Toast.makeText(EnrollmentDetailActivity.this, "Bitmap Save", Toast.LENGTH_SHORT).show();
            }
            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");
            }
            return uri;
        } catch (IOException e) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }
            throw e;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadDocument(String document) {
        fileUrl = document;
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        fileName = fileName.substring(0,1).toUpperCase() + fileName.substring(1);
        File file = new File(getExternalCacheDir(),fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Toast.makeText(EnrollmentDetailActivity.this, "Temp File created Successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(EnrollmentDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }else {
            file.delete();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(file))
                .setTitle(fileName)
                .setDescription("Downloading Document...")
                .setRequiresCharging(false)
                .setAllowedOverMetered(false)
                .setAllowedOverRoaming(false);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        //downloadManager.enqueue(request);
        downloadID = downloadManager.enqueue(request);

        boolean finishDownload = false;
        int progress;
        while (!finishDownload){
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
            if (cursor.moveToFirst()){
                @SuppressLint("Range")
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status){
                    case DownloadManager.STATUS_FAILED:
                        Toast.makeText(EnrollmentDetailActivity.this, "Download FAILED", Toast.LENGTH_SHORT).show();
                        finishDownload = true;
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        Toast.makeText(EnrollmentDetailActivity.this, "Download PAUSED", Toast.LENGTH_SHORT).show();
                        break;
                    case DownloadManager.STATUS_PENDING:
                        Toast.makeText(EnrollmentDetailActivity.this, "Download PENDING", Toast.LENGTH_SHORT).show();
                        break;
                    case DownloadManager.STATUS_RUNNING:{
                        Toast.makeText(EnrollmentDetailActivity.this, "Download RUNNING", Toast.LENGTH_SHORT).show();
                        @SuppressLint("Range")
                        final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (total > 0){
                            @SuppressLint("Range") final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress = (int) ((downloaded * 100L) / total);
                        }
                        break;
                    }
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        progress = 100;
                        finishDownload = true;
                        Toast.makeText(EnrollmentDetailActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }


    private File createDocument(String directoryDownloads, String fileName) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(fileName.substring(0,fileName.lastIndexOf(".")),fileName.substring(fileName.lastIndexOf(".")),new File(directoryDownloads));
            Toast.makeText(EnrollmentDetailActivity.this, "Temp File Crated Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(EnrollmentDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return tempFile;
    }

    private void deleteDocument(int position) {
        ProgressDialog progressDialog = new ProgressDialog(EnrollmentDetailActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("document_id", documentListModels.get(position).documentId);
        reqParams.put("user_id", userPreference.getUserId());

        requestApi(reqParams, Request.Method.POST, WebService.DELETE_DOCUMENT, new RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        documentListModels.remove(position);
                        documentListAdapter.notifyItemRemoved(position);
                    }
                } catch (JSONException e) {
                    progressDialog.cancel();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                progressDialog.cancel();
            }
        }, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedDocument = data.getData();
                String fileName;
                Cursor cursor = getContentResolver().query(selectedDocument, null, null, null, null);

                if (cursor == null) fileName = selectedDocument.getPath();
                else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    //long fileSize = Long.parseLong(String.valueOf(cursor.getLong(sizeIndex))) / 1024;
                    //Toast.makeText(EnrollmentDetailActivity.this, "File Size "+Long.toString(cursor.getLong(sizeIndex)), Toast.LENGTH_SHORT).show();
                    fileName = cursor.getString(idx);
                    cursor.close();
                }
                File tempFile = new File(getExternalCacheDir(), fileName);
                createFile(selectedDocument, tempFile);
            } else {
                Toast.makeText(EnrollmentDetailActivity.this, "Data Is Null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IMAGEPICK_GALLERY_REQUEST && resultCode == RESULT_OK) {

            try {
                if (data != null) {
                    Uri selectedDocument = data.getData();
                    String fileName;
                    Cursor cursor = getContentResolver().query(selectedDocument, null, null, null, null);

                    if (cursor == null) fileName = selectedDocument.getPath();
                    else {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                        fileName = cursor.getString(idx);
                        cursor.close();
                    }
                    File tempFile = new File(getExternalCacheDir(), fileName);
                    createImageFile(selectedDocument, tempFile);
                }else {
                    Toast.makeText(EnrollmentDetailActivity.this, "Null", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(EnrollmentDetailActivity.this, "Some thing want wrong"+e, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void createImageFile(Uri selectedDocument, File tempFile) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedDocument);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            sendDocument(tempFile.getAbsolutePath());
        } catch (IOException e) {
            Helper.LOG("Create File Exception", e.toString());
        }
    }

    private void createFile(Uri selectedDocument, File tempFile) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedDocument);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            sendFile(tempFile.getAbsolutePath());
        } catch (IOException e) {
            Helper.LOG("Create File Exception", e.toString());
        }
    }

    private void sendFile(String filePath) {
        new AlertDialog.Builder(EnrollmentDetailActivity.this)
                .setTitle(getResources().getString(R.string.upload_title))
                .setMessage(getResources().getString(R.string.upload_message))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(EnrollmentDetailActivity.this, "Yes Click", Toast.LENGTH_SHORT).show();
                        uploadDocument(filePath);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EnrollmentDetailActivity.this, "No Click", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    private void sendDocument(String filePath) {
        new AlertDialog.Builder(EnrollmentDetailActivity.this)
                .setTitle(getResources().getString(R.string.upload_title))
                .setMessage(getResources().getString(R.string.upload_message))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(EnrollmentDetailActivity.this, "Yes Click", Toast.LENGTH_SHORT).show();
                        uploadPhoto(filePath);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EnrollmentDetailActivity.this, "No Click", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    private void uploadPhoto(String filePath) {
        ProgressDialog progressDialog = new ProgressDialog(EnrollmentDetailActivity.this);
        progressDialog.setMessage("Uploading Photo Please Wait...");
        progressDialog.show();

        uploadReceiver.register(EnrollmentDetailActivity.this);
        uploadId = UUID.randomUUID().toString();
        uploadReceiver.addUploadListener(new SingleFileUploadInterface() {
            @Override
            public void onProgress(int progress) {
                //Toast.makeText(EnrollmentDetailActivity.this, "Upload On Progress", Toast.LENGTH_SHORT).show();
                Helper.LOG("On Progress", "Upload In Progress");
            }

            @Override
            public void onProgress(long uploadedBytes, long totalBytes) {

            }

            @Override
            public void onError(Exception exception) {
                progressDialog.cancel();
                Helper.LOG("On Error", "Upload On Error");
            }

            @Override
            public void onCompleted(int serverResponseCode, String serverResponseBody) {
                progressDialog.cancel();
                uploadReceiver.unregister(EnrollmentDetailActivity.this);
                Helper.LOG("Response-Multipart", serverResponseBody);

                //Delete file once uploaded to the server.
                File deleteFile = new File(filePath);
                deleteFile.delete();

                getEnrollmentDetail();

                Helper.LOG("On Complete", "Uploading Is Complete");
            }

            @Override
            public void onCancelled() {
                progressDialog.cancel();
                uploadReceiver.unregister(EnrollmentDetailActivity.this);
                Helper.LOG("On Cancelled", "Upload Is On Cancelled");
            }
        });
        uploadReceiver.setUploadID(uploadId);
        try {

            MultipartUploadRequest request = new MultipartUploadRequest(this, uploadId, WebService.UPLOAD_PHOTO);
            request.addFileToUpload(filePath, "photo")
                    .addParameter("api_key", WebService.API_KEY)
                    .addParameter("enquiry_id", String.valueOf(enquiryId))
                    .addParameter("user_id", userPreference.getUserId());

            request.setMaxRetries(1).startUpload();

        } catch (MalformedURLException | FileNotFoundException e) {
            progressDialog.cancel();
            e.printStackTrace();
        }
    }

    private void uploadDocument(String filePath) {
        ProgressDialog progressDialog = new ProgressDialog(EnrollmentDetailActivity.this);
        progressDialog.setMessage("Uploading Document Please Wait...");
        progressDialog.show();

        uploadReceiver.register(EnrollmentDetailActivity.this);
        uploadId = UUID.randomUUID().toString();
        uploadReceiver.addUploadListener(new SingleFileUploadInterface() {
            @Override
            public void onProgress(int progress) {
                //Toast.makeText(EnrollmentDetailActivity.this, "Upload On Progress", Toast.LENGTH_SHORT).show();
                Helper.LOG("On Progress", "Upload In Progress");

            }

            @Override
            public void onProgress(long uploadedBytes, long totalBytes) {

            }

            @Override
            public void onError(Exception exception) {
                progressDialog.cancel();
                //Toast.makeText(EnrollmentDetailActivity.this, "Upload On Error", Toast.LENGTH_SHORT).show();
                Helper.LOG("On Error", "Upload On Error");
            }

            @Override
            public void onCompleted(int serverResponseCode, String serverResponseBody) {
                progressDialog.cancel();
                uploadReceiver.unregister(EnrollmentDetailActivity.this);
                Helper.LOG("Response-Multipart", serverResponseBody);

                //Delete file once uploaded to the server.
                File deleteFile = new File(filePath);
                deleteFile.delete();

                documentListModels.clear();
                //getEnrollmentDetail();
                getDocument();

                Helper.LOG("On Complete", "Uploading Is Complete");
                //Toast.makeText(EnrollmentDetailActivity.this, "Upload On Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {
                progressDialog.cancel();
                uploadReceiver.unregister(EnrollmentDetailActivity.this);
                Helper.LOG("On Cancelled", "Upload Is On Cancelled");
            }
        });
        uploadReceiver.setUploadID(uploadId);
        try {

            MultipartUploadRequest request = new MultipartUploadRequest(this, uploadId, WebService.UPLOAD_DOCUMENT);
            request.addFileToUpload(filePath, "document[0]")
                    .addParameter("api_key", WebService.API_KEY)
                    .addParameter("enquiry_id", String.valueOf(enquiryId))
                    .addParameter("user_id", userPreference.getUserId());

            request.setMaxRetries(1).startUpload();
            //Toast.makeText(EnrollmentDetailActivity.this, "Upload Start", Toast.LENGTH_SHORT).show();

        } catch (MalformedURLException | FileNotFoundException e) {
            progressDialog.cancel();
            e.printStackTrace();
        }

    }

    private void getDocument() {

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("enquiry_id", String.valueOf(enquiryId));
        reqParams.put("user_id", userPreference.getUserId());

        requestApi(reqParams, Request.Method.POST, WebService.GET_DOCUMENT, new RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONArray dataJsonArray = jsonObject.getJSONArray(WebService.Params.DATA);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject dataJsonObject = dataJsonArray.getJSONObject(i);
                            DocumentListModel item = new DocumentListModel();
                            item.document = dataJsonObject.getString("document");
                            item.documentId = dataJsonObject.getString("document_id");
                            documentListModels.add(item);
                        }
                        documentListAdapter.notifyDataSetChanged();
                    } else {
                        //Toast.makeText(EnrollmentDetailActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {

            }
        }, null);
    }

    private void getEnrollmentDetail() {
        ProgressDialog progressDialog = new ProgressDialog(EnrollmentDetailActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", userPreference.getUserId());
        reqParams.put("enquiry_id", String.valueOf(enquiryId));

        requestApi(reqParams, Request.Method.POST, WebService.ENQUIRY_DETAILS, new RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        enquiryDetails = new Gson()
                                .fromJson(jsonObject.getJSONArray(WebService.Params.DATA).getJSONObject(0).toString(), EnquiryListModel.class);

                        binding.tvStudentName.setText(enquiryDetails.name);
                        binding.tvPhoneNumber.setText(enquiryDetails.phone_number);
                        binding.tvAddress.setText(enquiryDetails.address);
                        binding.tvDob.setText(Helper.isNullOrEmpty(enquiryDetails.birth_date) ? "" : Helper.changeDateFormat(enquiryDetails.birth_date, Helper.defaultDateFormat, "dd MMM yyyy"));
                        binding.tvCourse.setText(enquiryDetails.course_name);
                        binding.tvCountry.setText(enquiryDetails.country);
                        binding.tvBatchTime.setText(enquiryDetails.batch_time);
                        binding.tvApplyDate.setText(Helper.isNullOrEmpty(enquiryDetails.created_at) ? "" : Helper.changeDateFormat(enquiryDetails.created_at, Helper.defaultDateFormat, "dd MMM yyyy"));
                        Glide.with(EnrollmentDetailActivity.this)
                                .load(enquiryDetails.photo)
                                .into(binding.ivProfile);

                    } else {
                        progressDialog.cancel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                progressDialog.cancel();
            }
        }, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}
/*public class DownloadImage extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url =new URL(strings[0]);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }catch (IOException e){
                e.printStackTrace();
            }
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES +"/IgniteDocument");
            if (!path.exists())
                path.mkdirs();
            File imageFile = new File(path,String.valueOf(System.currentTimeMillis())+".png");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(imageFile);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(EnrollmentDetailActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
        }
    }*/

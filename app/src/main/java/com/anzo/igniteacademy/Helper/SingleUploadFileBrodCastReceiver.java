package com.anzo.igniteacademy.Helper;

import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.UnsupportedEncodingException;

//
public class SingleUploadFileBrodCastReceiver  extends UploadServiceBroadcastReceiver{
    private String mUploadID;
    private SingleFileUploadInterface uploadInterface;

    public void setUploadID(String uploadID) {
        mUploadID = uploadID;
    }

    public void addUploadListener(SingleFileUploadInterface uploadInterface) {
        this.uploadInterface = uploadInterface;
    }

    @Override
    public void onProgress(String uploadId, int progress) {
        if (uploadId.equals(mUploadID) && uploadInterface != null) {
            uploadInterface.onProgress(progress);
        }
    }

    @Override
    public void onProgress(String uploadId, long uploadedBytes, long totalBytes) {
        if (uploadId.equals(mUploadID) && uploadInterface != null) {
            uploadInterface.onProgress(uploadedBytes, totalBytes);
        }
    }

    @Override
    public void onError(String uploadId, Exception exception) {
        if (uploadId.equals(mUploadID) && uploadInterface != null) {
            uploadInterface.onError(exception);
        }
    }

    @Override
    public void onCompleted(String uploadId, int serverResponseCode, byte[] serverResponseBody) {
        if (uploadId.equals(mUploadID) && uploadInterface != null) {
            try {
                String str = new String(serverResponseBody, "UTF-8");
                //Helper.LOG("Response",str);
                uploadInterface.onCompleted(serverResponseCode, str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCancelled(String uploadId) {
        if (uploadId.equals(mUploadID) && uploadInterface != null) {
            uploadInterface.onCancelled();
        }
    }
}

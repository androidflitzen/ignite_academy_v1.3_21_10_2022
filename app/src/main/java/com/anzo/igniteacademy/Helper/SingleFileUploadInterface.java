package com.anzo.igniteacademy.Helper;

public interface SingleFileUploadInterface {
    void onProgress(int progress);
    void onProgress(long uploadedBytes, long totalBytes);
    void onError(Exception exception);
    void onCompleted(int serverResponseCode, String serverResponseBody);
    void onCancelled();
}

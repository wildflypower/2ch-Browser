package ua.in.quireg.chan.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;

import ua.in.quireg.chan.R;
import ua.in.quireg.chan.common.Factory;
import ua.in.quireg.chan.common.library.BackgroundDownloadFileView;
import ua.in.quireg.chan.common.library.DialogDownloadFileView;
import ua.in.quireg.chan.common.library.SingleMediaScanner;
import ua.in.quireg.chan.common.utils.IoUtils;
import ua.in.quireg.chan.common.utils.UriUtils;
import ua.in.quireg.chan.exceptions.DownloadFileException;
import ua.in.quireg.chan.interfaces.ICancelled;
import ua.in.quireg.chan.interfaces.ICloudflareCheckListener;
import ua.in.quireg.chan.interfaces.IDownloadFileView;
import ua.in.quireg.chan.interfaces.IProgressChangeListener;
import ua.in.quireg.chan.services.CacheDirectoryManager;
import ua.in.quireg.chan.services.CloudflareCheckService;
import ua.in.quireg.chan.services.http.DownloadFileService;
import ua.in.quireg.chan.settings.ApplicationSettings;

import java.io.File;

public class DownloadFileTask extends AsyncTask<String, Long, Boolean> implements ICancelled, IProgressChangeListener {
    public static final String TAG = "DownloadFileTask";
    private final DownloadFileService mDownloadFileService;
    private final Context mContext;
    private final Resources mResources;
    private final Uri mFrom;
    private final ApplicationSettings mSettings;
    private final IDownloadFileView mProgressView;
    private final CacheDirectoryManager mCacheDirectoryManager;
    private final boolean mUpdateGallery;
    private boolean retry = true; //after cloudflare check

    private File mSaveTo;
    private String mUserError = null;
    private long mContentLength = 0;

    {
        this.mDownloadFileService = Factory.getContainer().resolve(DownloadFileService.class);
        this.mSettings = Factory.getContainer().resolve(ApplicationSettings.class);
        this.mCacheDirectoryManager = Factory.getContainer().resolve(CacheDirectoryManager.class);
    }

    public DownloadFileTask(Context context, Uri from) {
        this(context, from, null, null, true);
    }

    public DownloadFileTask(Context context, Uri from, File to, IDownloadFileView progressView, boolean updateGallery) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mFrom = from;
        this.mSaveTo = to != null ? to : IoUtils.getSaveFilePath(this.mFrom, this.mSettings);
        this.mUpdateGallery = updateGallery;

        if (progressView == null) {
            this.mProgressView = this.mSettings.isDownloadInBackground()
                    ? new BackgroundDownloadFileView(this.mContext)
                    : new DialogDownloadFileView(this.mContext);
        } else {
            this.mProgressView = progressView;
        }

        this.mProgressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DownloadFileTask.this.retry = false;
                DownloadFileTask.this.cancel(true);
            }
        });
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            Uri from = this.getSaveFromUri();

            this.mDownloadFileService.downloadFile(from, this.mSaveTo, this, this);

            return true;
        } catch (DownloadFileException e) {
            this.mUserError = e.getMessage();
            return false;
        }
    }

    @Override
    public void onPreExecute() {
        // Не показывать диалог совсем, если файл существует
        if (this.mSaveTo.exists()) {
            this.cancel(false);
            this.mProgressView.showFileExists(this.mSaveTo);

            return;
        }

        this.mProgressView.showLoading(this.mContext.getString(R.string.notification_save_image_started, this.mSaveTo.getAbsolutePath()));
    }

    @Override
    public void onPostExecute(Boolean success) {

        if (success) {
            Uri uri = Uri.fromFile(this.mSaveTo);
            if (this.mUpdateGallery && (UriUtils.isImageUri(uri) || UriUtils.isVideoUri(uri))) {
                SingleMediaScanner scanner = new SingleMediaScanner(this.mContext, this.mSaveTo);
                scanner.scan();
            }
            this.mProgressView.hideLoading();
            this.mProgressView.showSuccess(this.mSaveTo);
        } else {
            if (this.mUserError.equals("503")) {
                String url = DownloadFileTask.this.mFrom.toString();
                new CloudflareCheckService(url, (Activity) this.mContext, new ICloudflareCheckListener(){
                    public void onTimeout() {
                        DownloadFileTask.this.mProgressView.hideLoading();
                        DownloadFileTask.this.mProgressView.showError(mResources.getString(R.string.error_cloudflare_check_timeout));
                    }
                    public void onSuccess() {
                        if (DownloadFileTask.this.retry)
                            new DownloadFileTask(
                                DownloadFileTask.this.mContext,
                                DownloadFileTask.this.mFrom,
                                null,
                                DownloadFileTask.this.mProgressView,
                                true).execute();
                    }
                    public void onStart() {
                        DownloadFileTask.this.mProgressView.showError(mResources.getString(R.string.notification_cloudflare_check_started));
                    }
                }).start();
            } else {
                this.mProgressView.hideLoading();
                this.mProgressView.showError(this.mUserError);
            }
        }
    }

    private Uri getSaveFromUri() {
        Uri from = this.mFrom;

        File cachedFile = this.mCacheDirectoryManager.getCachedMediaFileForRead(from);
        if (cachedFile.exists()) {
            from = Uri.fromFile(cachedFile);
        }

        return from;
    }

    @Override
    public void onProgressUpdate(Long... progress) {
        this.mProgressView.setCurrentProgress(progress[0].intValue());
    }

    @Override
    public void progressChanged(long newValue) {
        this.publishProgress(newValue / 1024);
    }

    @Override
    public void indeterminateProgress() {
        // nothing
    }

    @Override
    public void setContentLength(long value) {
        this.mContentLength = value;
        this.mProgressView.setMaxProgress((int) value / 1024);
    }

    @Override
    public long getContentLength() {
        return this.mContentLength;
    }
}

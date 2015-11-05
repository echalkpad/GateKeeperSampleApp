package co.blustor.gatekeeper.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileVault {
    private static final String TAG = FileVault.class.getSimpleName();

    private LocalFilestore mLocalFilestore;
    private RemoteFilestore mRemoteFilestore;

    public FileVault(LocalFilestore localFilestore, RemoteFilestore remoteFilestore) {
        mLocalFilestore = localFilestore;
        mRemoteFilestore = remoteFilestore;
    }

    public void listFiles(final ListFilesListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    listener.onListFiles(mRemoteFilestore.listFiles());
                } catch (IOException e) {
                    Log.e(TAG, "Problem listing Files with FilestoreClient", e);
                    listener.onListFilesError(e);
                }
                return null;
            }
        }.execute();
    }

    public void listFiles(VaultFile file, ListFilesListener listener) {
        mRemoteFilestore.navigateTo(file.getName());
        listFiles(listener);
    }

    public void getFile(final VaultFile file, final GetFileListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File targetPath;
                try {
                    targetPath = mLocalFilestore.makeTempPath();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to create local cache path", e);
                    listener.onGetFileError(e);
                    return null;
                }
                try {
                    file.setLocalPath(new File(targetPath, file.getName()));
                    mRemoteFilestore.getFile(file);
                    listener.onGetFile(file);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to get File", e);
                    listener.onGetFileError(e);
                }
                return null;
            }
        }.execute();
    }

    public void navigateUp() {
        mRemoteFilestore.navigateUp();
    }

    public void finish() {
        mRemoteFilestore.finish();
    }

    public void clearCache() {
        mLocalFilestore.clearCache();
    }

    public boolean isAtRoot() {
        return mRemoteFilestore.isAtRoot();
    }

    public interface ListFilesListener {
        void onListFiles(List<VaultFile> files);
        void onListFilesError(IOException e);
    }

    public interface GetFileListener {
        void onGetFile(VaultFile file);
        void onGetFileError(IOException e);
    }
}

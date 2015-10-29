package co.blustor.gatekeeper.data;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class LocalFilestore {
    File mCachePath;

    public LocalFilestore(File cachePath) {
        mCachePath = cachePath;
    }

    public java.io.File makeTempPath() throws IOException {
        String uniqueDir = UUID.randomUUID().toString();
        File path = new File(mCachePath, uniqueDir);
        path.mkdirs();
        return path;
    }

    public void clearCache() {
        File[] cacheItems = mCachePath.listFiles();
        for (File cacheItem : cacheItems) {
            deleteCacheItem(cacheItem);
        }
    }

    private void deleteCacheItem(File path) {
        if (path.isDirectory()) {
            File[] cacheItems = path.listFiles();
            for (File cacheItem : cacheItems) {
                deleteCacheItem(cacheItem);
            }
        }
        path.delete();
    }
}
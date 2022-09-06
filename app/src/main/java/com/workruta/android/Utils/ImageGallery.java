package com.workruta.android.Utils;

import android.content.Context;

import com.workruta.android.Interface.OnGalleryLoaded;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ImageGallery implements Runnable {

    Context context;
    String[] allPath;
    ArrayList<String> allMediaList;
    OnGalleryLoaded onGalleryLoaded;

    public ImageGallery(Context context){
        this.context = context;
        this.allMediaList = new ArrayList<>();
        this.allPath = StorageUtils.getStorageDirectories(context);
    }

    public void setOnGalleryLoaded(OnGalleryLoaded onGalleryLoaded){
        this.onGalleryLoaded = onGalleryLoaded;
    }

    private void loadDirectoryFiles(File directory){
        boolean notForbidden = false;
        String[] forbiddenPaths = new String[]{
                "/Android/data",
                "/Android/obb",
                "/LOST.DIR",
                "/.thumbnail"
        };
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (File file : fileList) {
                String filePath = file.getAbsolutePath();
                if (file.isDirectory()) {
                    for (String forbiddenPath : forbiddenPaths) {
                        if (filePath.contains(forbiddenPath)) {
                            notForbidden = true;
                            break;
                        }
                    }
                    if (!notForbidden)
                        loadDirectoryFiles(file);
                } else {
                    String pthPar = file.getParent();

                    String[] pthPars = Objects.requireNonNull(pthPar).split("/");
                    if (!(pthPars[pthPars.length - 1].equals("LOST.DIR") || pthPars[pthPars.length - 1].equals(".thumbnails"))) {
                        String name = file.getName().toLowerCase();
                        for (String ext : Constants.allowedExtImg) {
                            if (name.endsWith(ext)) {
                                allMediaList.add(filePath);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        for(String path: allPath){
            File storage = new File(path);
            loadDirectoryFiles(storage);
        }
        onGalleryLoaded.onFinished(allMediaList);
    }
}

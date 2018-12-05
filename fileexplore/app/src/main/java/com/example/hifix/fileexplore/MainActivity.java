package com.example.hifix.fileexplore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private void scanFile(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        final Map<String, Set<String>> CATEGORY_SUFFIX = new HashMap<>(FILE_CATEGORY_ICON.length);
        Set<String> set = new HashSet<>();
        set.add("mp4");
        set.add("avi");
        set.add("wmv");
        set.add("flv");
        CATEGORY_SUFFIX.put("video", set);

        set.add("txt");
        set.add("pdf");
        set.add("doc");
        set.add("docx");
        set.add("xls");
        set.add("xlsx");
        CATEGORY_SUFFIX.put("document", set);

        set = new HashSet<>();
        set.add("jpg");
        set.add("jpeg");
        set.add("png");
        set.add("bmp");
        set.add("gif");
        CATEGORY_SUFFIX.put("picture", set);

        set = new HashSet<>();
        set.add("mp3");
        set.add("ogg");
        CATEGORY_SUFFIX.put("music", set);

        set = new HashSet<>();
        set.add("apk");
        CATEGORY_SUFFIX.put("apk", set);

        set = new HashSet<>();
        set.add("zip");
        set.add("rar");
        set.add("7z");
        CATEGORY_SUFFIX.put("zip", set);

        //单一线程线程池
        ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
        singleExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //构建对象
                ScanFileCountUtil scanFileCountUtil = new ScanFileCountUtil
                        .Builder(mHandler)
                        .setFilePath(path)
                        .setCategorySuffix(CATEGORY_SUFFIX)
                        .create();
                scanFileCountUtil.scanCountFile();
            }
        });
    }
}

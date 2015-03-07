package momomo00.fileselector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class FileSelectorActivity extends FragmentActivity {
    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // main_layout は layoutディレクトリではなく、values/layouts.xml にあります
        setContentView(R.layout.main_layout);

        // SDカードが利用できる状態かどうかチェック
        String mediaStatus = Environment.getExternalStorageState();
        if (!mediaStatus.equals(Environment.MEDIA_MOUNTED) &&
                !mediaStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            // 使用できない場合はエラーメッセージを表示して終了
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_cannot_use_sdcard),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // FileListFragment を ルート要素に追加
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        FileListFragment fileListFragment = FileListFragment.newInstance(
                Environment.getExternalStorageDirectory());
        fileListFragment.setOnFileSelectedListener(mOnFileSelectedListener);
        transaction.add(R.id.file_list, fileListFragment);
        transaction.commit();
    }

    private FileListFragment.OnFileSelectedListener mOnFileSelectedListener
            = new FileListFragment.OnFileSelectedListener() {
        @Override
        public void onFileSelected(File file) {
            if(mFragmentManager == null) {
                return;
            }

            if(file.isDirectory()) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                FileListFragment fileListFragment = FileListFragment.newInstance(file);
                fileListFragment.setOnFileSelectedListener(mOnFileSelectedListener);
                transaction.replace(R.id.file_list, fileListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                String MIMEType = getMIMEType(file);
                if(MIMEType == null) {
                    Toast.makeText(getApplicationContext()
                                , "実行できるアプリケーションが見つかりません"
                                , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(file), MIMEType);
                startActivity(i);
            }
        }
    };

    private String getMIMEType(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        String extension = (index >= 0) ? fileName.substring(index + 1) : null;
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
package momomo00.fileselector;

import java.io.File;
import java.util.Comparator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileListFragment extends Fragment {
    // ファイル選択時に呼ばれるリスナ
    public interface OnFileSelectedListener {
        public void onFileSelected(File file);
    }

    private static final String BUNDLE_KEY_DIR = "dir";

    // Fragment は引数つきのコンストラクタを推奨していません。
    // そのため、このようにFactoryパターンを用いて初期値を定義します。
    // dir はnull禁止
    public static FileListFragment newInstance(File dir) {
        if (dir == null) {
            // あえて例外を発生させます
            throw(new NullPointerException());
        }
        FileListFragment instance = new FileListFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_DIR, dir);
        instance.setArguments(args);
        return instance;
    }

    private File            mCurrentDir;
    private TextView        mDirField;
    private ListView        mListView;
    private ImageButton     mUpButton;
    private OnFileSelectedListener  mOnFileSelectedListener;

    // XMLでの定義は出来なくなります
    private FileListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle state = savedInstanceState;
        if (state == null) {
            state = getArguments();
        }

        // 初期値の受け取り
        mCurrentDir = (File) state.getSerializable(BUNDLE_KEY_DIR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_list_fragment, container, false);

        mDirField = (TextView) view.findViewById(R.id.text_dir);

        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = (File)parent.getItemAtPosition(position);
                if((mOnFileSelectedListener != null) && (file != null)) {
                    mOnFileSelectedListener.onFileSelected(file);
                }
            }
        });

        mUpButton = (ImageButton)view.findViewById(R.id.button_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = mCurrentDir.getParentFile();
                if((mOnFileSelectedListener != null) && (file != null)) {
                    mOnFileSelectedListener.onFileSelected(file);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // リスト表示のためのアダプタ準備
        FileArrayAdapter adapter = new FileArrayAdapter(getActivity());
        if (mCurrentDir != null && mCurrentDir.isDirectory()) {
            // 現在のディレクトリを表示
            mDirField.setText(mCurrentDir.getPath());

            // ディレクトリに含まれるファイルをアダプタに追加
            for (File file : mCurrentDir.listFiles()) {
                if(file.isDirectory()) {
                    adapter.add(file);
                } else {
                    String mimeType = FileSelectorActivity.getMIMEType(file);
                    if((mimeType.equals("video/*"))
                            || (mimeType.equals("audio/*"))
                            || (mimeType.equals("image/*"))) {
                        adapter.add(file);
                    }
                }
            }

            // ファイルを名前順にソート
            adapter.sort(new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        } else {
            // mCurrentDir がディレクトリではない場合は何も表示しない
            mDirField.setText("");
        }

        // アダプタをセット
        mListView.setAdapter(adapter);
    }

    public void setOnFileSelectedListener(OnFileSelectedListener listener) {
        mOnFileSelectedListener = listener;
    }
}
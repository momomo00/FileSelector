package momomo00.fileselector;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<File> {
    private final LayoutInflater mInflater;
    private final Drawable mIconFolder;
    private final Drawable mIconFile;

    public FileArrayAdapter(Context context) {
        super(context, 0);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mIconFolder = context.getResources().getDrawable(R.drawable.ic_folder);
        mIconFile   = context.getResources().getDrawable(R.drawable.ic_file);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.file_list_item, null);
            holder = new ViewHolder();
            holder.iconView = (ImageView) view.findViewById(R.id.image_icon);
            holder.fileNameView = (TextView) view.findViewById(R.id.text_file_name);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        // アイコンとファイル名を表示
        File item = getItem(position);

        if (item.isDirectory()) {
            holder.iconView.setImageDrawable(mIconFolder);
        } else {
            holder.iconView.setImageDrawable(mIconFile);
        }

        holder.fileNameView.setText(item.getName());

        return view;
    }

    // findViewById をなるべく使わないで高速化するためのホルダー
    private class ViewHolder {
        public ImageView iconView;
        public TextView  fileNameView;
    }
}
package com.newvision.martin.clouddetect.common.widget.dialog;

import android.content.Context;
import android.widget.TextView;

import com.newvision.martin.clouddetect.R;

/**
 * Created by zhangsong on 17-4-27.
 */

public class DialogFactory {
    public static NVDialog create(Context context, String content) {
        NVDialog dialog = new NVDialog(context);
        dialog.addContentView(R.layout.martin_dialog_content_simple);
        ((TextView) dialog.findViewById(R.id.txt_content)).setText(content);
        return dialog;
    }

    public static NVDialog create(Context context, String title, String content) {
        NVDialog dialog = new NVDialog(context);
        dialog.addContentView(R.layout.martin_dialog_content_simple_2);
        ((TextView) dialog.findViewById(R.id.txt_title)).setText(title);
        ((TextView) dialog.findViewById(R.id.txt_content)).setText(content);
        return dialog;
    }
}

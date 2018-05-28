package com.newvision.zeus.glasscore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.newvision.zeus.glasscore.R;

/**
 * Created by yanjiatian on 2017/6/30.
 */

public class GlassQRCodeUtils {
    public static Bitmap getQRBitmap(Context context, String text) {
        Bitmap bitmap = new QREncode.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setParsedResultType(ParsedResultType.TEXT)
                .setContents(text)
                .build().encodeAsBitmap();
        return bitmap;
    }
}

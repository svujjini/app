package com.app.android.hashmap.view.siv;

import android.content.Context;
import android.util.AttributeSet;

import com.app.android.hashmap.R;
import com.app.android.hashmap.view.siv.shader.ShaderHelper;
import com.app.android.hashmap.view.siv.shader.SvgShader;

public class OctogonImageView extends ShaderImageView {

    public OctogonImageView(Context context) {
        super(context);
    }

    public OctogonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OctogonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_octogon);
    }
}

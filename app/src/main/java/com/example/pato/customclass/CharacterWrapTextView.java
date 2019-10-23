package com.example.pato.customclass;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

public class CharacterWrapTextView extends androidx.appcompat.widget.AppCompatTextView {
    public CharacterWrapTextView(Context context) {
        super(context);
    }

    public CharacterWrapTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CharacterWrapTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text.toString().replace(" ", "\u00A0").replace("-","\u2011"),type);
    }


}

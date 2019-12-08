package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.R;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * This is a slider with a description TextView.
 */
public class TextSliderView extends BaseSliderView {


    public TextSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text, null);

        RoundedImageView imageView_background = v.findViewById(R.id.imageView_background_render_type_text);
        TextView textView_name = v.findViewById(R.id.textView_name_render_type_text);
        TextView textView_view = v.findViewById(R.id.textView_view_render_type_text);

        textView_name.setText(getName());
        textView_view.setText(getSub_name());
        bindEventAndShow(v, imageView_background);

        return v;
    }
}

/*
 * Space Navigation library for Android
 * Copyright (c) 2016 Arman Chatikyan (https://github.com/armcha/Space-Navigation-View).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irfaan008.irbottomnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

@SuppressLint("ViewConstructor")
class BezierView extends RelativeLayout {

    private Paint paint;

    private Path path;

    private int bezierWidth, bezierHeight;

    private int backgroundColor;

    private Context context;

    private boolean isLinear=false;

    BlurMaskFilter maskFilter;


    BezierView(Context context, int backgroundColor) {
        super(context);
        this.context = context;
        this.backgroundColor = backgroundColor;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

//        path2 = new Path();
//        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//        paint2.setColor(0xff000000);
//        paint2.setStyle(Paint.Style.STROKE);
//        maskFilter = new BlurMaskFilter(10f, BlurMaskFilter.Blur.OUTER);
////        paint2.setMaskFilter(maskFilter);
//        setWillNotDraw(false);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, paint2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setBackgroundColor(ContextCompat.getColor(context, R.color.space_transparent));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /**
         * Set paint color to fill view
         */
        paint.setColor(backgroundColor);

        /**
         * Reset path before drawing
         */
        path.reset();
//        path2.reset();

        /**
         * Start point for drawing
         */
        path.moveTo(0, bezierHeight);
//        path2.moveTo(0, bezierHeight);

        if(!isLinear){
            /**
             * Seth half path of bezier view
             */
            path.cubicTo(bezierWidth / 4, bezierHeight, bezierWidth / 4, 0, bezierWidth / 2, 0);
//            path2.cubicTo(bezierWidth / 4, bezierHeight, bezierWidth / 4, 10, bezierWidth / 2, 10);
            /**
             * Seth second part of bezier view
             */
            path.cubicTo((bezierWidth / 4) * 3, 0, (bezierWidth / 4) * 3, bezierHeight, bezierWidth, bezierHeight);
//            path2.cubicTo((bezierWidth / 4) * 3, 10, (bezierWidth / 4) * 3, bezierHeight, bezierWidth, bezierHeight);
        }

        /**
         * Draw our bezier view
         */
//        canvas.drawPath(path2, paint2);
        canvas.drawPath(path, paint);

    }

    /**
     * Build bezier view with given width and height
     *
     * @param bezierWidth  Given width
     * @param bezierHeight Given height
     * @param isLinear True, if curves are not needed
     */
    void build(int bezierWidth, int bezierHeight,boolean isLinear) {
        this.bezierWidth = bezierWidth;
        this.bezierHeight = bezierHeight;
        this.isLinear=isLinear;
    }

    /**
     * Change bezier view background color
     *
     * @param backgroundColor Target color
     */
    void changeBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }
}


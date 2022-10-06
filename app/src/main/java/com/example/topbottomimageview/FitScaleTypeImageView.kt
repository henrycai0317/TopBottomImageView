package com.example.topbottomimageview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

@Suppress("PrivatePropertyName")
class FitScaleTypeImageView : AppCompatImageView {

    private var fitScaleType = -1
    private val FIT_TOP = 0       //向上縮放
    private val FIT_BOTTOM = 1    //向下縮放
    private val FIT_CENTER = 2    //向中間縮放

    constructor(
        context: Context, attrs: AttributeSet? = null
    ) : super(context, attrs) {
        init(attrs)
    }


    constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs, R.styleable.FitScaleTypeImageView, 0, 0)
                .apply {
                    fitScaleType =
                        getInt(R.styleable.FitScaleTypeImageView_fitScaleType, fitScaleType)
                }
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (fitScaleType != -1) {
            computeDrawableMatrix()
        }
    }

    private fun computeDrawableMatrix() {
        val viewWidth = (measuredWidth - (paddingLeft + paddingRight)).toFloat()
        val viewHeight = (measuredHeight - (paddingTop + paddingBottom)).toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val scale =
            if (drawableWidth * viewHeight > drawableHeight * viewWidth)
                viewHeight / drawableHeight
            else
                viewWidth / drawableWidth
        val offset = viewHeight / scale

        when (fitScaleType) {
            /**
             * scale 等比例縮放圖片、translate平移圖片  RectF(左,上,右,下)
             */
            FIT_TOP -> {
                imageMatrix.apply {
                    setScale(scale, scale)
                }
            }
            /**
             *   setRectToRect的作用是，
             *   將drawableRect圖片顯示位置，載入到viewRect螢幕顯示的位置。
             *  相關資源參考 https://cloud.tencent.com/developer/article/1705395
             */
            FIT_BOTTOM -> {
                val viewRect = RectF(0f, 0f, viewWidth, viewHeight)
                val drawableRect =
                    RectF(0f, drawableHeight - offset, drawableWidth, drawableHeight - 0.5f)
                imageMatrix.apply {
                    setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL)
                }

            }
            /**
             * dx,dy 是作為平移座標位置
             * 縮放比跟FIT_TOP一樣不變，只改變要顯示的位置
             */
            FIT_CENTER -> {
                val dx = (viewWidth - drawableWidth * scale) * 0.5f
                val dy = (viewHeight - drawableHeight * scale) * 0.5f
                imageMatrix.apply {
                    setScale(scale, scale)
                    postTranslate(dx, dy)
                }
            }
        }

    }
}
package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var progress = 0.0

    private var rect = Rect()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()    // redraw the screen
        requestLayout() // when rectangular progress dimension changes
    }

    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            this.context,
            R.animator.loading_animator
        ) as ValueAnimator
        valueAnimator.addUpdateListener(updateListener)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        rect = canvas?.getClipBounds()!!
        paint.setColor(resources.getColor(R.color.colorPrimary))
        canvas.drawRect(rect, paint).apply {
            textAlignment = TEXT_ALIGNMENT_CENTER
        }
        invalidate()

        val paintText = Paint().apply {
            color = resources.getColor(R.color.white)
            textAlignment = TEXT_ALIGNMENT_CENTER
            textSize = 70f
        }

        val paintClicked = Paint()
        if (buttonState == ButtonState.Clicked){
            paintClicked.setColor(resources.getColor(R.color.colorAccent))
            canvas.drawRect(0f, 0f, (widthSize*progress/100).toFloat(), heightSize.toFloat(), paintClicked)
            invalidate()

        }
        if (buttonState == ButtonState.Completed){
            valueAnimator.cancel()
            isClickable = true
            invalidate()

        }



        canvas.drawText(
            "Download",
            (widthSize/4).toFloat(),
            (heightSize/2).toFloat(),
            paintText
        )
        invalidate()
    }

    //action to do when the button is clicked
    override fun performClick(): Boolean {
        super.performClick()
        //Once the button is clicked, we make non-clickable until the state become completed
        isClickable = false
        valueAnimator.start()
        buttonState = ButtonState.Clicked
        invalidate()
        return  true
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = (widthSize*progress/100).toInt()
    }

    fun downloadFinished(){
        //Once download is completed, the button should be clickable again
        buttonState = ButtonState.Completed
        invalidate()
    }

    /*
    *When someone click onthe button without seleccting an item
    * we show a message to him inviting him to select something
    *
     */
    fun selectItem(){
        //we show a message then putting the button clickable again
        Toast.makeText(context, "Please select an item", Toast.LENGTH_SHORT).show()
        buttonState = ButtonState.Completed
        invalidate()
    }
}
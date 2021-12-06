package com.udacity

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates


class DownloadImage @JvmOverloads constructor(
    context:Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr){
    private var clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private var clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private var clipRectTop  = resources.getDimension(R.dimen.clipRectTop)
    private var clipRectLeft  = resources.getDimension(R.dimen.clipRectLeft)

    private var circleRadius = resources.getDimension(R.dimen.circleRadius)

    private var colonneOne by Delegates.notNull<Float>()
    private var colonneTwo by Delegates.notNull<Float>()
    private var colonneThree by Delegates.notNull<Float>()

    private var lineOne by Delegates.notNull<Float>()
    private var lineTwo by Delegates.notNull<Float>()
    private var lineThree by Delegates.notNull<Float>()




    private var path = Path()

    private var widthSize = 0
    private var heightSize = 0

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = canvas.getClipBounds()
        //val rect = RectF(clipRectLeft, clipRectTop,clipRectRight, clipRectBottom )
        val paint = Paint().apply {
            color = context.getColor(R.color.primaryColor)
        }



        colonneTwo = (rect.left+ (rect.right/4)*2).toFloat()
        colonneOne = rect.left.toFloat() + colonneTwo-circleRadius
        colonneThree = rect.left.toFloat() + colonneTwo+circleRadius

        lineTwo = rect.top.toFloat() + (rect.bottom/4)*2
        lineOne = rect.top.toFloat() + lineTwo-circleRadius
        lineThree = rect.top.toFloat() + lineTwo+circleRadius


        canvas.clipRect(rect.left, rect.top, rect.right, rect.bottom)
        canvas.drawColor(context.getColor(R.color.secondaryColor))

        val pathCircle = Path()
        pathCircle.addCircle(colonneTwo, lineOne, circleRadius, Path.Direction.CW)
        pathCircle.addCircle(colonneOne, lineTwo, circleRadius, Path.Direction.CW)
        pathCircle.addCircle(colonneThree, lineTwo, circleRadius, Path.Direction.CW)
        canvas.drawPath(pathCircle, paint)

        var oval = RectF(colonneOne, lineTwo, colonneThree, lineTwo + circleRadius)
        canvas.drawRect(oval, paint)


        paint.color = context.getColor(R.color.secondaryColor)
        canvas.drawRect(colonneTwo-circleRadius/4, lineTwo-circleRadius, colonneTwo+circleRadius/4, lineTwo-circleRadius/2, paint)

        val pointLeft = Point((colonneTwo-circleRadius/2).toInt(), (lineTwo-circleRadius/2).toInt())
        val pointRight = Point((colonneTwo+circleRadius/2).toInt(), (lineTwo-circleRadius/2).toInt())
        val pointBottom = Point(colonneTwo.toInt(), lineTwo.toInt())
        path.moveTo(pointLeft.x.toFloat(), pointLeft.y.toFloat())
        path.lineTo(pointBottom.x.toFloat(), pointBottom.y.toFloat())
        path.lineTo(pointRight.x.toFloat(), pointRight.y.toFloat())
        path.lineTo(pointLeft.x.toFloat(), pointLeft.y.toFloat())

        canvas.drawPath(path, paint)




    }

    fun drawCircleOne(canvas: Canvas){
        canvas.save()
        val paint = Paint().apply {
            color = resources.getColor(R.color.primaryColor, context.theme)
        }
        canvas.translate(colonneTwo, lineOne)

    }

    fun drawCircleTwo(canvas:Canvas){
        canvas.save()
        val paint = Paint().apply {
            color = resources.getColor(R.color.primaryColor, context.theme)
        }
        canvas.translate(colonneOne, lineTwo)
    }

    fun drawCircleThree(canvas: Canvas){
        canvas.drawColor(resources.getColor(R.color.primaryColor, context.theme))
        canvas.save()
        path.rewind()
        //path.addCircle(colonneOne, lineTwo)
        canvas.translate(colonneThree, lineTwo)
        path.addCircle(colonneThree, lineTwo, circleRadius, Path.Direction.CCW)
        canvas.restore()

    }
}
package net.kotlinandroid.customlibrary.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import net.kotlinandroid.customlibrary.R;

/**
 * Created by itgoyo on 17-6-20.
 */

public class SectorProgress extends View {

    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint();
    private RectF rectF = new RectF();
    private Rect mRect = new Rect();
    private float strokeWidth;//圆环宽度
    private float angle;//当前的角度
    private boolean isRestart;//是否每一次绘制都从0开始
    private long duration = 1800;//动画时间
    private int underColor, percentColor;//底色和数字颜色
    private float oldAngle;//前一次的角度
    private float indexFinalH;//百分比的高度
    private boolean isFirst = true;
    private float indexSize, symbolSize;//百分比的字体和百分比符号的大小
    private float lineWidth;//线的宽度，默认为0
    private float maxValue;//最大值
    private float minValue;//最小值
    private TimeInterpolator timeValue;//动画差值器
    private int[] shaderColor;//渲染颜色
    private Shader mShader;

    public SectorProgress(Context context) {
        super(context);
        init(null, 0);
    }

    public SectorProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SectorProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray array = getContext().obtainStyledAttributes(
                attrs, R.styleable.ProgressView, defStyle, 0);
        strokeWidth = array.getDimension(R.styleable.ProgressView_circleWidth, dip2px(20));
        underColor = array.getColor(R.styleable.ProgressView_underColor, Color.GRAY);
        percentColor = array.getColor(R.styleable.ProgressView_percentTextColor, Color.BLACK);
        indexSize = array.getDimension(R.styleable.ProgressView_percentTextSize, sp2px(30));
        isRestart = array.getBoolean(R.styleable.ProgressView_isRestartProgress, false);
        lineWidth = array.getDimension(R.styleable.ProgressView_lineWidth, 0);
        symbolSize = array.getDimension(R.styleable.ProgressView_symbolSize, sp2px(16));
        lineWidth = lineWidth < 0 ? 0 : lineWidth;
        array.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        mPaint.setAntiAlias(true);
        rectF.set(strokeWidth / 2f, strokeWidth / 2, width - strokeWidth / 2, width - strokeWidth / 2);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        //画环形底色
        mPaint.setColor(underColor);
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        //画环形
        mPaint.setShader(mShader);
        //mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth - lineWidth);
        canvas.drawArc(rectF, 270, angle, false, mPaint);
        mPaint.setShader(null);//画完后清除渲染

        //画中间的文字
        mPaint.setTextSize(indexSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(percentColor);
        int number = (int) (angle * 100 / 360f);
        number = number < 0 ? 0 : number;
        number = number > 100 ? 100 : number;
        String index = String.valueOf(number);
        float indexW = mPaint.measureText(index);
        mPaint.getTextBounds(index, 0, 1, mRect);//获取中间数字的高度
        float indexH = mRect.height();
        if (isFirst) {
            indexFinalH = indexH;
            isFirst = false;
        }
        canvas.drawText(index, (width - indexW) / 2.0f, (height + indexFinalH) / 2.0f, mPaint);

        //画百分比的符号
        mPaint.setTextSize(symbolSize);
        canvas.drawText("%", (width + indexW) / 2.0f, (height + indexFinalH) / 2.0f, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int[] color = new int[]{Color.parseColor("#FFBF80"), Color.parseColor("#FF8080")};
        color = shaderColor == null ? color : shaderColor;
        mMatrix.setRotate(-90, getWidth() / 2, getHeight() / 2);
        mShader = new SweepGradient(getWidth() / 2, getHeight() / 2, color, null);
        mShader.setLocalMatrix(mMatrix);
    }

    private void updateProgress(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        long time = (long) (duration * (Math.abs(end - start) / 360f));
        animator.setDuration(time < 600 ? 600 : time);
        timeValue = timeValue == null ? new OvershootInterpolator(0.8f) : timeValue;
        animator.setInterpolator(timeValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                angle = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;//wrap_content时宽高为120dp
        width = widthMode == MeasureSpec.EXACTLY ? widthSize : (int) dip2px(120);
        height = heightMode == MeasureSpec.EXACTLY ? heightSize : width;
        setMeasuredDimension(width, height);
    }

    private float sp2px(float spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    private float dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    /*------------------------对外的方法-------------------------*/
    public void setProgress(float progress) {
        if (maxValue > 0) {
            float value = 360 * (progress - minValue) / (maxValue - minValue);
            angle = value;
            updateProgress(oldAngle, angle);
            oldAngle = isRestart ? 0 : value;
        } else {
            throw new RuntimeException("setMinMaxValue(float minValue, " +
                    "float maxValue) method must be called firstly!");
        }
    }

    //获取进度，调用此方法前必须设置最大最小值
    public float getProgress() {
        return angle / 360;
    }

    //设置动画时间，真实动画时间根据百分比变更幅度计算，体验更好
    public void setAnimateDuration(long time) {
        duration = time;
    }

    //设置一个动画的差值器
    public void setInterpolator(TimeInterpolator value) {
        this.timeValue = value;
    }

    //设置渲染颜色
    public void setShaderColor(int[] colorArray) {
        shaderColor = colorArray;
    }

    //直接设置百分比
    public void setPercent(float percent) {
        angle = 360 * percent;
        updateProgress(oldAngle, angle);
        oldAngle = isRestart ? 0 : angle;
    }

    //设置每一的进度变更是否从0开始
    public void setIsDrawRestart(boolean isRestart) {
        this.isRestart = isRestart;
    }

    //设置最大最小值
    public void setMinMaxValue(float minValue, float maxValue) {
        this.maxValue = maxValue;
        //最大值必须大于等于最小值
        this.minValue = maxValue >= minValue ? minValue : maxValue;
    }

}

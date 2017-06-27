package org.seiko.panc.wiget.scale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Hiroshi on 2017/5/26. Y
 */

public class ZoomableRecyclerView extends RecyclerView implements OnScaleDragGestureListener,
        FlingRunnable.OnFlingRunningListener, GestureDetector.OnDoubleTapListener {

    public static final float MIN_SCALE = 1.0f;
    public static final float MID_SCALE = 2.0f;
    public static final float MAX_SCALE = 2.2f;
    public static final float SINGLE_MIN_SCALE = 0.7f;
    public static final float SINGLE_MAX_SCALE = 3.0f;

    private final Matrix mMatrix = new Matrix();
    private final RectF mTempRectF = new RectF();
    private final Rect mTempRect = new Rect();

    private ScaleDragDetector mScaleDragDetector;
    private GestureDetectorCompat mGestureDetector;
    private OnTapGestureListener mTapGestureListener;

    private boolean isVertical = true;
    private int mScaleCount = -5;

    private FlingRunnable mCurrentFlingRunnable;

    public ZoomableRecyclerView(Context context) {
        this(context, null, 0);
    }

    public ZoomableRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableRecyclerView(Context context, AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mScaleDragDetector = new ScaleDragDetector(context, this);
        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (mTapGestureListener != null) {
                    mTapGestureListener.onLongPress(e.getRawX(), e.getRawY());
                }
            }
        });
        mGestureDetector.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                cancelFling();
                break;
        }

        boolean wasScaling = mScaleDragDetector.isScaling();
        mScaleDragDetector.onTouchEvent(event);

        if (!wasScaling && !mScaleDragDetector.isScaling()) {
            if (mScaleCount <= -5) {
                super.onTouchEvent(event);
            } else {
                --mScaleCount;
            }
        } else {
            mScaleCount = 0;
        }

        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(mMatrix);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY, boolean isEnd) {
        float mScale = ViewUtils.calculateScale(mMatrix);
        if ((mScale > SINGLE_MIN_SCALE && mScale < SINGLE_MAX_SCALE) || isEnd) {
            mMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkBounds();
            invalidate();
        }
    }

    @Override
    public void onScaleEnd() {
        if (ViewUtils.calculateScale(mMatrix) < MIN_SCALE) {
//            checkBounds();
            RectF rect = getDisplayRect(mMatrix);
            post(new AnimatedScaleRunnable(MIN_SCALE, rect.centerX(), rect.centerY(), this, mMatrix, true, this));
        } else if (ViewUtils.calculateScale(mMatrix) > MAX_SCALE) {
            RectF rect = getDisplayRect(mMatrix);
            post(new AnimatedScaleRunnable(MAX_SCALE, rect.centerX(), rect.centerY(), this, mMatrix, true, this));
        }
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (mScaleCount <= -5) {
            if (isVertical) {
                mMatrix.postTranslate(dx, 0);
            } else {
                mMatrix.postTranslate(0, dy);
            }
            checkBounds();
            invalidate();
        }
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        checkBounds();
        RectF rect = getDisplayRect(mMatrix);
        mCurrentFlingRunnable = new FlingRunnable(getContext(), this, this);
        mCurrentFlingRunnable.fling(rect, ViewUtils.getViewWidth(this),
                ViewUtils.getViewHeight(this), (int) velocityX, (int) velocityY);
        post(mCurrentFlingRunnable);
    }

    @Override
    public void onFlingRunning(int dx, int dy) {
        if (isVertical) {
            mMatrix.postTranslate(dx, 0);
        } else {
            mMatrix.postTranslate(0, dy);
        }
        invalidate();
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mTapGestureListener != null) {
            mTapGestureListener.onSingleTap(e.getRawX(), e.getRawY());
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        try {
            float scale = ViewUtils.calculateScale(mMatrix);
            float x = event.getX();
            float y = event.getY();

            setScale(scale < MID_SCALE ? MID_SCALE : MIN_SCALE, x, y);
        } catch (Exception e) {
            // Can sometimes happen when getX() and getY() is called
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    public void setTapListenerListener(OnTapGestureListener listener) {
        mTapGestureListener = listener;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    private void setScale(float scale, float focusX, float focusY) {
        if (scale >= MIN_SCALE && scale <= MAX_SCALE) {
            post(new AnimatedScaleRunnable(scale, focusX, focusY, this, mMatrix, false, this));
        }
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    //===========================================
    public void checkBounds() {
        RectF rect = getDisplayRect(mMatrix);

        float height = rect.height();
        float width = rect.width();
        float deltaX = 0.0F;
        float deltaY = 0.0F;

        int viewHeight = ViewUtils.getViewHeight(this);
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        int viewWidth = ViewUtils.getViewWidth(this);
        if (width <= (float) viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
        } else if (rect.left > 0.0F) {
            deltaX = -rect.left;
        } else if (rect.right < (float) viewWidth) {
            deltaX = viewWidth - rect.right;
        }

        mMatrix.postTranslate(deltaX, deltaY);
    }

    private RectF getDisplayRect(Matrix matrix) {
        getLocalVisibleRect(mTempRect);
        mTempRectF.set(mTempRect);
        matrix.mapRect(mTempRectF);
        return mTempRectF;
    }

}

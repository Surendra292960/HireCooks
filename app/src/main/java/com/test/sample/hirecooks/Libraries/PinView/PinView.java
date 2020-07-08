package com.test.sample.hirecooks.Libraries.PinView;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import com.test.sample.hirecooks.R;
import java.util.Objects;

public class PinView extends AppCompatEditText {
    private static final boolean DBG = false;
    private static final int BLINK = 500;
    private static final int DEFAULT_COUNT = 4;
    private static final InputFilter[] NO_FILTERS = new InputFilter[ 0 ];
    private static final int[] HIGHLIGHT_STATES = new int[] {
            android.R.attr.state_selected };
    private static final int VIEW_TYPE_RECTANGLE = 0;
    private static final int VIEW_TYPE_LINE = 1;
    private int mViewType;
    private int mPinItemCount;
    private int mPinItemWidth;
    private int mPinItemHeight;
    private int mPinItemRadius;
    private int mPinItemSpacing;
    private final Paint mPaint;
    private final TextPaint mAnimatorTextPaint   = new TextPaint ( );
    private ColorStateList mLineColor;
    private int mCurLineColor = Color.BLACK;
    private int mLineWidth;
    private final Rect mTextRect = new Rect ( );
    private final RectF mItemBorderRect = new RectF ( );
    private final RectF mItemLineRect = new RectF ( );
    private final Path mPath = new Path ( );
    private final PointF mItemCenterPoint = new PointF ( );
    private ValueAnimator mDefaultAddAnimator;
    private boolean isAnimationEnable = false;
    private Blink mBlink;
    private boolean isCursorVisible;
    private boolean drawCursor;
    private float mCursorHeight;
    private int mCursorWidth;
    private int mCursorColor;
    private int mItemBackgroundResource;
    private Drawable mItemBackground;
    private boolean mHideLineWhenFilled;
    public PinView ( Context context ) {
        this ( context , null );
    }
    public PinView ( Context context , @Nullable AttributeSet attrs ) {
        this ( context , attrs , R.attr.pinViewStyle );
    }
    public PinView ( Context context , @Nullable AttributeSet attrs , int defStyleAttr ) {
        super ( context , attrs , defStyleAttr );

        final Resources res = getResources ( );
        mPaint = new Paint ( Paint.ANTI_ALIAS_FLAG );
        mPaint.setStyle ( Paint.Style.STROKE );
        mAnimatorTextPaint.set ( getPaint ( ) );
        final Resources.Theme theme = context.getTheme ( );
        TypedArray a = theme.obtainStyledAttributes ( attrs , R.styleable.PinView , defStyleAttr , 0 );
        mViewType = a.getInt ( R.styleable.PinView_viewType , VIEW_TYPE_RECTANGLE );
        mPinItemCount = a.getInt ( R.styleable.PinView_itemCount , DEFAULT_COUNT );
        mPinItemHeight = ( int ) a.getDimension ( R.styleable.PinView_itemHeight , res.getDimensionPixelSize ( R.dimen.pv_pin_view_item_size ) );
        mPinItemWidth = ( int ) a.getDimension ( R.styleable.PinView_itemWidth , res.getDimensionPixelSize ( R.dimen.pv_pin_view_item_size ) );
        mPinItemSpacing = a.getDimensionPixelSize ( R.styleable.PinView_itemSpacing , res.getDimensionPixelSize ( R.dimen.pv_pin_view_item_spacing ) );
        mPinItemRadius = ( int ) a.getDimension ( R.styleable.PinView_itemRadius , 0 );
        mLineWidth = ( int ) a.getDimension ( R.styleable.PinView_lineWidth , res.getDimensionPixelSize ( R.dimen.pv_pin_view_item_line_width ) );
        mLineColor = a.getColorStateList ( R.styleable.PinView_lineColor );
        isCursorVisible = a.getBoolean ( R.styleable.PinView_android_cursorVisible , true );
        mCursorColor = a.getColor ( R.styleable.PinView_cursorColor , getCurrentTextColor ( ) );
        mCursorWidth = a.getDimensionPixelSize ( R.styleable.PinView_cursorWidth ,
                res.getDimensionPixelSize ( R.dimen.pv_pin_view_cursor_width ) );
        mItemBackground = a.getDrawable ( R.styleable.PinView_android_itemBackground );
        mHideLineWhenFilled = a.getBoolean ( R.styleable.PinView_hideLineWhenFilled , false );
        a.recycle ( );
        if ( mLineColor != null ) {
            mCurLineColor = mLineColor.getDefaultColor ( );
        }
        updateCursorHeight ( );
        checkItemRadius ( );
        setMaxLength ( mPinItemCount );
        mPaint.setStrokeWidth ( mLineWidth );
        setupAnimator ( );
        super.setCursorVisible ( false );
        disableSelectionMenu ( );
    }
    @Override
    public void setTypeface ( Typeface tf , int style ) {
        super.setTypeface ( tf , style );
    }
    @Override
    public void setTypeface ( Typeface tf ) {
        super.setTypeface ( tf );
        if (mAnimatorTextPaint != null) {
            mAnimatorTextPaint.set(getPaint());
        }
    }
    private void setMaxLength ( int maxLength ) {
        if ( maxLength >= 0 ) {
            setFilters ( new InputFilter[] { new InputFilter.LengthFilter ( maxLength ) } );
        } else {
            setFilters ( NO_FILTERS );
        }
    }
    private void setupAnimator ( ) {
        mDefaultAddAnimator = ValueAnimator.ofFloat ( 0.5f , 1f );
        mDefaultAddAnimator.setDuration ( 150 );
        mDefaultAddAnimator.setInterpolator ( new DecelerateInterpolator ( ) );
        mDefaultAddAnimator.addUpdateListener ( animation -> {
            float scale = ( float ) animation.getAnimatedValue ( );
            int alpha = ( int ) ( 255 * scale );
            mAnimatorTextPaint.setTextSize ( getTextSize ( ) * scale );
            mAnimatorTextPaint.setAlpha ( alpha );
            postInvalidate ( );
        } );
    }
    private void checkItemRadius ( ) {
        if ( mViewType == VIEW_TYPE_LINE ) {
            float halfOfLineWidth = ( ( float ) mLineWidth ) / 2;
            if ( mPinItemRadius > halfOfLineWidth ) {
                throw new IllegalArgumentException ( "The itemRadius can not be greater than lineWidth when viewType is line" );
            }
        } else if ( mViewType == VIEW_TYPE_RECTANGLE ) {
            float halfOfItemWidth = ( ( float ) mPinItemWidth ) / 2;
            if ( mPinItemRadius > halfOfItemWidth ) {
                throw new IllegalArgumentException ( "The itemRadius can not be greater than itemWidth" );
            }
        }
    }
    @Override
    protected void onMeasure ( int widthMeasureSpec , int heightMeasureSpec ) {
        int widthMode = MeasureSpec.getMode ( widthMeasureSpec );
        int heightMode = MeasureSpec.getMode ( heightMeasureSpec );
        int widthSize = MeasureSpec.getSize ( widthMeasureSpec );
        int heightSize = MeasureSpec.getSize ( heightMeasureSpec );
        int width;
        int height;
        int boxHeight = mPinItemHeight;
        if ( widthMode == MeasureSpec.EXACTLY ) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {
            int boxesWidth = ( mPinItemCount - 1 ) * mPinItemSpacing + mPinItemCount * mPinItemWidth;
            width = boxesWidth + ViewCompat.getPaddingEnd ( this ) + ViewCompat.getPaddingStart ( this );
            if ( mPinItemSpacing == 0 ) {
                width -= ( mPinItemCount - 1 ) * mLineWidth;
            }
        }
        if ( heightMode == MeasureSpec.EXACTLY ) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        } else {
            height = boxHeight + getPaddingTop ( ) + getPaddingBottom ( );
        }
        setMeasuredDimension ( width , height );
    }
    @Override
    protected void onTextChanged ( CharSequence text , int start , int lengthBefore , int lengthAfter ) {
        if ( start != text.length ( ) ) {
            moveSelectionToEnd ( );
        }
        makeBlink ( );
        if ( isAnimationEnable ) {
            final boolean isAdd = lengthAfter - lengthBefore > 0;
            if ( isAdd ) {
                if ( mDefaultAddAnimator != null ) {
                    mDefaultAddAnimator.end ( );
                    mDefaultAddAnimator.start ( );
                }
            }
        }
    }
    @Override
    protected void onFocusChanged ( boolean focused , int direction , Rect previouslyFocusedRect ) {
        super.onFocusChanged ( focused , direction , previouslyFocusedRect );
        if ( focused ) {
            moveSelectionToEnd ( );
            makeBlink ( );
        }
    }
    @Override
    protected void onSelectionChanged ( int selStart , int selEnd ) {
        super.onSelectionChanged ( selStart , selEnd );
        if ( selEnd != Objects.requireNonNull ( getText ( ) ).length ( ) ) {
            moveSelectionToEnd ( );
        }
    }
    private void moveSelectionToEnd ( ) {
        setSelection ( Objects.requireNonNull ( getText ( ) ).length ( ) );
    }
    @Override
    protected void drawableStateChanged ( ) {
        super.drawableStateChanged ( );
        if ( mLineColor == null || mLineColor.isStateful ( ) ) {
            updateColors ( );
        }
    }
    @Override
    protected void onDraw ( Canvas canvas ) {
        canvas.save ( );
        updatePaints ( );
        drawPinView ( canvas );
        canvas.restore ( );
    }
    private void updatePaints ( ) {
        mPaint.setColor ( mCurLineColor );
        mPaint.setStyle ( Paint.Style.STROKE );
        mPaint.setStrokeWidth ( mLineWidth );
        getPaint ( ).setColor ( getCurrentTextColor ( ) );
    }
    private void drawPinView ( Canvas canvas ) {
        int highlightIdx = Objects.requireNonNull ( getText ( ) ).length ( );
        for ( int i = 0 ; i < mPinItemCount ; i++ ) {
            boolean highlight = isFocused ( ) && highlightIdx == i;
            mPaint.setColor ( highlight ? getLineColorForState ( HIGHLIGHT_STATES ) : mCurLineColor );
            updateItemRectF ( i );
            updateCenterPoint ( );
            canvas.save ( );
            if ( mViewType == VIEW_TYPE_RECTANGLE ) {
                updatePinBoxPath ( i );
                canvas.clipPath ( mPath );
            }
            drawItemBackground ( canvas , highlight );
            canvas.restore ( );
            if ( highlight ) {
                drawCursor ( canvas );
            }
            if ( mViewType == VIEW_TYPE_RECTANGLE ) {
                drawPinBox ( canvas , i );
            } else if ( mViewType == VIEW_TYPE_LINE ) {
                drawPinLine ( canvas , i );
            }
            if ( DBG ) {
                drawAnchorLine ( canvas );
            }
            if ( getText ( ).length ( ) > i ) {
                if ( isPasswordInputType ( getInputType ( ) ) ) {
                    drawCircle ( canvas , i );
                } else {
                    drawText ( canvas , i );
                }
            } else if ( ! TextUtils.isEmpty ( getHint ( ) ) && getHint ( ).length ( ) == mPinItemCount ) {
                drawHint ( canvas , i );
            }
        }
        // highlight the next item
        if ( isFocused ( ) && getText ( ).length ( ) != mPinItemCount && mViewType == VIEW_TYPE_RECTANGLE ) {
            int index = getText ( ).length ( );
            updateItemRectF ( index );
            updateCenterPoint ( );
            updatePinBoxPath ( index );
            mPaint.setColor ( getLineColorForState ( HIGHLIGHT_STATES ) );
            drawPinBox ( canvas , index );
        }
    }
    private int getLineColorForState ( int... states ) {
        return mLineColor != null ? mLineColor.getColorForState ( states , mCurLineColor ) : mCurLineColor;
    }
    private void drawItemBackground ( Canvas canvas , boolean highlight ) {
        if ( mItemBackground == null ) {
            return;
        }
        float delta = ( float ) mLineWidth / 2;
        int left = Math.round ( mItemBorderRect.left - delta );
        int top = Math.round ( mItemBorderRect.top - delta );
        int right = Math.round ( mItemBorderRect.right + delta );
        int bottom = Math.round ( mItemBorderRect.bottom + delta );
        mItemBackground.setBounds ( left , top , right , bottom );
        mItemBackground.setState ( highlight ? HIGHLIGHT_STATES : getDrawableState ( ) );
        mItemBackground.draw ( canvas );
    }
    private void updatePinBoxPath ( int i ) {
        boolean drawRightCorner = false;
        boolean drawLeftCorner = false;
        if ( mPinItemSpacing != 0 ) {
            drawLeftCorner = drawRightCorner = true;
        } else {
            if ( i == 0 && i != mPinItemCount - 1 ) {
                drawLeftCorner = true;
            }
            if ( i == mPinItemCount - 1 && i != 0 ) {
                drawRightCorner = true;
            }
        }
        updateRoundRectPath ( mItemBorderRect , mPinItemRadius , mPinItemRadius , drawLeftCorner , drawRightCorner );
    }
    private void drawPinBox ( Canvas canvas , int i ) {
        if ( mHideLineWhenFilled && i < Objects.requireNonNull ( getText ( ) ).length ( ) ) {
            return;
        }
        canvas.drawPath ( mPath , mPaint );
    }
    private void drawPinLine ( Canvas canvas , int i ) {
        if ( mHideLineWhenFilled && i < Objects.requireNonNull ( getText ( ) ).length ( ) ) {
            return;
        }
        boolean l, r;
        l = r = true;
        if ( mPinItemSpacing == 0 && mPinItemCount > 1 ) {
            if ( i == 0 ) {
                // draw only left circular_image_category
                r = false;
            } else if ( i == mPinItemCount - 1 ) {
                // draw only right circular_image_category
                l = false;
            } else {
                // draw rect
                l = r = false;
            }
        }
        mPaint.setStyle ( Paint.Style.FILL );
        mPaint.setStrokeWidth ( ( ( float ) mLineWidth ) / 10 );
        float halfLineWidth = ( ( float ) mLineWidth ) / 2;
        mItemLineRect.set (
                mItemBorderRect.left - halfLineWidth ,
                mItemBorderRect.bottom - halfLineWidth ,
                mItemBorderRect.right + halfLineWidth ,
                mItemBorderRect.bottom + halfLineWidth );
        updateRoundRectPath ( mItemLineRect , mPinItemRadius , mPinItemRadius , l , r );
        canvas.drawPath ( mPath , mPaint );
    }
    private void drawCursor ( Canvas canvas ) {
        if ( drawCursor ) {
            float cx = mItemCenterPoint.x;
            float cy = mItemCenterPoint.y;
            float y = cy - mCursorHeight / 2;
            int color = mPaint.getColor ( );
            float width = mPaint.getStrokeWidth ( );
            mPaint.setColor ( mCursorColor );
            mPaint.setStrokeWidth ( mCursorWidth );
            canvas.drawLine ( cx , y , cx , y + mCursorHeight , mPaint );
            mPaint.setColor ( color );
            mPaint.setStrokeWidth ( width );
        }
    }
    private void updateRoundRectPath ( RectF rectF , float rx , float ry , boolean l , boolean r ) {
        updateRoundRectPath ( rectF , rx , ry , l , r , r , l );
    }
    private void updateRoundRectPath ( RectF rectF , float rx , float ry ,
                                       boolean tl , boolean tr , boolean br , boolean bl ) {
        mPath.reset ( );
        float l = rectF.left;
        float t = rectF.top;
        float r = rectF.right;
        float b = rectF.bottom;
        float w = r - l;
        float h = b - t;
        float lw = w - 2 * rx;// line width
        float lh = h - 2 * ry;// line height
        mPath.moveTo ( l , t + ry );
        if ( tl ) {
            mPath.rQuadTo ( 0 , - ry , rx , - ry );// top-left corner
        } else {
            mPath.rLineTo ( 0 , - ry );
            mPath.rLineTo ( rx , 0 );
        }
        mPath.rLineTo ( lw , 0 );
        if ( tr ) {
            mPath.rQuadTo ( rx , 0 , rx , ry );// top-right corner
        } else {
            mPath.rLineTo ( rx , 0 );
            mPath.rLineTo ( 0 , ry );
        }
        mPath.rLineTo ( 0 , lh );
        if ( br ) {
            mPath.rQuadTo ( 0 , ry , - rx , ry );// bottom-right corner
        } else {
            mPath.rLineTo ( 0 , ry );
            mPath.rLineTo ( - rx , 0 );
        }
        mPath.rLineTo ( - lw , 0 );
        if ( bl ) {
            mPath.rQuadTo ( - rx , 0 , - rx , - ry );// bottom-left corner
        } else {
            mPath.rLineTo ( - rx , 0 );
            mPath.rLineTo ( 0 , - ry );
        }
        mPath.rLineTo ( 0 , - lh );
        mPath.close ( );
    }
    private void updateItemRectF ( int i ) {
        float halfLineWidth = ( ( float ) mLineWidth ) / 2;
        float left = getScrollX ( ) + ViewCompat.getPaddingStart ( this ) + i * ( mPinItemSpacing + mPinItemWidth ) + halfLineWidth;
        if ( mPinItemSpacing == 0 && i > 0 ) {
            left = left - ( mLineWidth ) * i;
        }
        float right = left + mPinItemWidth - mLineWidth;
        float top = getScrollY ( ) + getPaddingTop ( ) + halfLineWidth;
        float bottom = top + mPinItemHeight - mLineWidth;
        mItemBorderRect.set ( left , top , right , bottom );
    }
    private void drawText ( Canvas canvas , int i ) {
        Paint paint = getPaintByIndex ( i );
        // 1, Rect(4, -39, 20, 0)
        // 您, Rect(2, -47, 51, 3)
        // *, Rect(0, -39, 23, -16)
        // =, Rect(4, -26, 26, -10)
        // -, Rect(1, -19, 14, -14)
        // +, Rect(2, -32, 29, -3)
        drawTextAtBox ( canvas , paint , Objects.requireNonNull ( getText ( ) ) , i );
    }
    private void drawHint ( Canvas canvas , int i ) {
        Paint paint = getPaintByIndex ( i );
        paint.setColor ( getCurrentHintTextColor ( ) );
        drawTextAtBox ( canvas , paint , getHint ( ) , i );
    }
    private void drawTextAtBox ( Canvas canvas , Paint paint , CharSequence text , int charAt ) {
        paint.getTextBounds ( text.toString ( ) , charAt , charAt + 1 , mTextRect );
        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;
        float x = cx - Math.abs ( ( float ) mTextRect.width ( ) ) / 2 - mTextRect.left;
        float y = cy + Math.abs ( ( float ) mTextRect.height ( ) ) / 2 - mTextRect.bottom;// always center vertical
        canvas.drawText ( text , charAt , charAt + 1 , x , y , paint );
    }
    private void drawCircle ( Canvas canvas , int i ) {
        Paint paint = getPaintByIndex ( i );
        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;
        canvas.drawCircle ( cx , cy , paint.getTextSize ( ) / 2 , paint );
    }
    private Paint getPaintByIndex ( int i ) {
        if ( isAnimationEnable && i == Objects.requireNonNull ( getText ( ) ).length ( ) - 1 ) {
            mAnimatorTextPaint.setColor ( getPaint ( ).getColor ( ) );
            return mAnimatorTextPaint;
        } else {
            return getPaint ( );
        }
    }
    /**
     * For seeing the font position
     */
    private void drawAnchorLine ( Canvas canvas ) {
        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;
        mPaint.setStrokeWidth ( 1 );
        cx -= mPaint.getStrokeWidth ( ) / 2;
        cy -= mPaint.getStrokeWidth ( ) / 2;
        mPath.reset ( );
        mPath.moveTo ( cx , mItemBorderRect.top );
        mPath.lineTo ( cx , mItemBorderRect.top + Math.abs ( mItemBorderRect.height ( ) ) );
        canvas.drawPath ( mPath , mPaint );
        mPath.reset ( );
        mPath.moveTo ( mItemBorderRect.left , cy );
        mPath.lineTo ( mItemBorderRect.left + Math.abs ( mItemBorderRect.width ( ) ) , cy );
        canvas.drawPath ( mPath , mPaint );
        mPath.reset ( );
        mPaint.setStrokeWidth ( mLineWidth );
    }
    private void updateColors ( ) {
        boolean inval = false;
        int color;
        if ( mLineColor != null ) {
            color = mLineColor.getColorForState ( getDrawableState ( ) , 0 );
        } else {
            color = getCurrentTextColor ( );
        }
        if ( color != mCurLineColor ) {
            mCurLineColor = color;
            inval = true;
        }
        if ( inval ) {
            invalidate ( );
        }
    }
    private void updateCenterPoint ( ) {
        float cx = mItemBorderRect.left + Math.abs ( mItemBorderRect.width ( ) ) / 2;
        float cy = mItemBorderRect.top + Math.abs ( mItemBorderRect.height ( ) ) / 2;
        mItemCenterPoint.set ( cx , cy );
    }
    private static boolean isPasswordInputType ( int inputType ) {
        final int variation =
                inputType & ( EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION );
        return variation
                == ( EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD )
                || variation
                == ( EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD )
                || variation
                == ( EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD );
    }
    public void setLineColor ( @ColorInt int color ) {
        mLineColor = ColorStateList.valueOf ( color );
        updateColors ( );
    }
    public void setLineColor ( ColorStateList colors ) {
        if ( colors == null ) {
            throw new NullPointerException ( );
        }
        mLineColor = colors;
        updateColors ( );
    }

    public ColorStateList getLineColors ( ) {
        return mLineColor;
    }
    @ColorInt
    public int getCurrentLineColor ( ) {
        return mCurLineColor;
    }
    public void setLineWidth ( @Px int borderWidth ) {
        mLineWidth = borderWidth;
        checkItemRadius ( );
        requestLayout ( );
    }
    public int getLineWidth ( ) {
        return mLineWidth;
    }
    public void setItemCount ( int count ) {
        mPinItemCount = count;
        setMaxLength ( count );
        requestLayout ( );
    }
    public int getItemCount ( ) {
        return mPinItemCount;
    }
    public void setItemRadius ( @Px int itemRadius ) {
        mPinItemRadius = itemRadius;
        checkItemRadius ( );
        requestLayout ( );
    }
    public int getItemRadius ( ) {
        return mPinItemRadius;
    }
    public void setItemSpacing ( @Px int itemSpacing ) {
        mPinItemSpacing = itemSpacing;
        requestLayout ( );
    }
    @Px
    public int getItemSpacing ( ) {
        return mPinItemSpacing;
    }
    public void setItemHeight ( @Px int itemHeight ) {
        mPinItemHeight = itemHeight;
        updateCursorHeight ( );
        requestLayout ( );
    }
    public int getItemHeight ( ) {
        return mPinItemHeight;
    }
    public void setItemWidth ( @Px int itemWidth ) {
        mPinItemWidth = itemWidth;
        checkItemRadius ( );
        requestLayout ( );
    }
    public int getItemWidth ( ) {
        return mPinItemWidth;
    }
    public void setAnimationEnable ( boolean enable ) {
        isAnimationEnable = enable;
    }
    public void setHideLineWhenFilled ( boolean hideLineWhenFilled ) {
        this.mHideLineWhenFilled = hideLineWhenFilled;
    }
    @Override
    public void setTextSize ( float size ) {
        super.setTextSize ( size );
        updateCursorHeight ( );
    }
    @Override
    public void setTextSize ( int unit , float size ) {
        super.setTextSize ( unit , size );
        updateCursorHeight ( );
    }
    public void setItemBackgroundResources ( @DrawableRes int resId ) {
        if ( resId != 0 && mItemBackgroundResource != resId ) {
            return;
        }
        mItemBackground = ResourcesCompat.getDrawable ( getResources ( ) , resId , getContext ( ).getTheme ( ) );
        setItemBackground ( mItemBackground );
        mItemBackgroundResource = resId;
    }
    public void setItemBackgroundColor ( @ColorInt int color ) {
        if ( mItemBackground instanceof ColorDrawable ) {
            ( ( ColorDrawable ) mItemBackground.mutate ( ) ).setColor ( color );
            mItemBackgroundResource = 0;
        } else {
            setItemBackground ( new ColorDrawable ( color ) );
        }
    }
    public void setItemBackground ( Drawable background ) {
        mItemBackgroundResource = 0;
        mItemBackground = background;
        invalidate ( );
    }
    public void setCursorWidth ( @Px int width ) {
        mCursorWidth = width;
        if ( isCursorVisible ( ) ) {
            invalidateCursor ( true );
        }
    }
    public int getCursorWidth ( ) {
        return mCursorWidth;
    }
    public void setCursorColor ( @ColorInt int color ) {
        mCursorColor = color;
        if ( isCursorVisible ( ) ) {
            invalidateCursor ( true );
        }
    }
    public int getCursorColor ( ) {
        return mCursorColor;
    }
    @Override
    public void setCursorVisible ( boolean visible ) {
        if ( isCursorVisible != visible ) {
            isCursorVisible = visible;
            invalidateCursor ( isCursorVisible );
            makeBlink ( );
        }
    }
    @Override
    public boolean isCursorVisible ( ) {
        return isCursorVisible;
    }
    @Override
    public void onScreenStateChanged ( int screenState ) {
        super.onScreenStateChanged ( screenState );
        switch (screenState) {
            case View.SCREEN_STATE_ON:
                resumeBlink ( );
                break;
            case View.SCREEN_STATE_OFF:
                suspendBlink ( );
                break;
        }
    }
    @Override
    protected void onAttachedToWindow ( ) {
        super.onAttachedToWindow ( );
        resumeBlink ( );
    }
    @Override
    protected void onDetachedFromWindow ( ) {
        super.onDetachedFromWindow ( );
        suspendBlink ( );
    }
    private boolean shouldBlink ( ) {
        return isCursorVisible ( ) && isFocused ( );
    }
    private void makeBlink ( ) {
        if ( shouldBlink ( ) ) {
            if ( mBlink == null ) {
                mBlink = new Blink ( );
            }
            removeCallbacks ( mBlink );
            drawCursor = false;
            postDelayed ( mBlink , BLINK );
        } else {
            if ( mBlink != null ) {
                removeCallbacks ( mBlink );
            }
        }
    }
    private void suspendBlink ( ) {
        if ( mBlink != null ) {
            mBlink.cancel ( );
            invalidateCursor ( false );
        }
    }
    private void resumeBlink ( ) {
        if ( mBlink != null ) {
            mBlink.uncancel ( );
            makeBlink ( );
        }
    }
    private void invalidateCursor ( boolean showCursor ) {
        if ( drawCursor != showCursor ) {
            drawCursor = showCursor;
            invalidate ( );
        }
    }
    private void updateCursorHeight ( ) {
        int delta = 2 * dpToPx ( 2 );
        mCursorHeight = mPinItemHeight - getTextSize ( ) > delta ? getTextSize ( ) + delta : getTextSize ( );
    }
    private class Blink implements Runnable {
        private boolean mCancelled;
        @Override
        public void run ( ) {
            if ( mCancelled ) {
                return;
            }
            removeCallbacks ( this );
            if ( shouldBlink ( ) ) {
                invalidateCursor ( ! drawCursor );
                postDelayed ( this , BLINK );
            }
        }
        private void cancel ( ) {
            if ( ! mCancelled ) {
                removeCallbacks ( this );
                mCancelled = true;
            }
        }
        void uncancel ( ) {
            mCancelled = false;
        }
    }
    private void disableSelectionMenu ( ) {
        setCustomSelectionActionModeCallback ( new ActionMode.Callback ( ) {
            @Override
            public boolean onCreateActionMode ( ActionMode mode , Menu menu ) {
                return false;
            }
            @Override
            public boolean onPrepareActionMode ( ActionMode mode , Menu menu ) {
                return false;
            }
            @Override
            public boolean onActionItemClicked ( ActionMode mode , MenuItem item ) {
                return false;
            }
            @Override
            public void onDestroyActionMode ( ActionMode mode ) {
                // no-op
            }
        } );
        setLongClickable ( false );
    }
    @Override
    public boolean isSuggestionsEnabled ( ) {
        return false;
    }
    //endregion
    private int dpToPx ( float dp ) {
        return ( int ) ( dp * getResources ( ).getDisplayMetrics ( ).density + 0.5f );
    }
}
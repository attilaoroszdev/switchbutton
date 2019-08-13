/*
 * SwitchButton v1.0
 *
 * Copyright (c) 2019 Attila Orosz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.atlanticomnibus.switchbutton;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.annotation.SuppressLint;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class SwitchButton extends LinearLayout {


    /**************************************Public constants****************************************/
    private static final int SWITCH_START=0;
    private static final int SWITCH_END=1;

    /*************************************Private constants****************************************/
    private final int DEFAULT_SWITCH_PADDING,
                      VIEW_VERTICAL_PADDING;



    /*************************************Some annotations*****************************************/

    /**@hide */
    @IntDef({TypedValue.COMPLEX_UNIT_PX, TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP, TypedValue.COMPLEX_UNIT_PT, TypedValue.COMPLEX_UNIT_IN, TypedValue.COMPLEX_UNIT_MM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ValidSizeUnit{}

    /**@hide */
    @IntDef({SWITCH_START, SWITCH_END})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SwitchTogglePosition{}


    /*************************************Component views******************************************/
    //private ConstraintLayout compoundContainer;
    private Switch switchToggle;
    private TextView buttonTextView;


    /*************************************Member variables*****************************************/
    private String buttonText;
    private int switchPosition;
    private int switchPadding;
    private boolean isChecked;
    private boolean isEnabled;
    private boolean isDynamicallyCreated;


    /************************************A good listener always listens****************************/
    private OnCheckedChangeListener checkedChangeListener;


    /***************************************Constructors*******************************************/

    /**
     * <p>This constructor will be used to dynamically create a SwitchButton from code. Its sole parameter is the context in which
     * it will be added. Everything gets set to a default value and must be changed with the proper setter functions.</p>
     *
     * @param context The context
     */
    public SwitchButton(Context context) {
        super(context);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        switchPosition=SWITCH_START;
        switchPadding =DEFAULT_SWITCH_PADDING;
        isDynamicallyCreated=true;
        initSwitchButton(context);
    }

    /**
     * Default constructors from super class, extended with the necessary bits, These will get called when addimng in XML
     * Using them from code is not tested.
     */


    /**
     * <p>Same as in {@link android.widget.LinearLayout}'s similar constructors, also setting some extra values</p>
     * @param context See super class
     * @param position See super class
     */
    public SwitchButton(Context context, int position) {
        super(context);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        switchPosition=position;
        isDynamicallyCreated=true;
        initSwitchButton(context);
    }

    /**
     * <p>Same as in {@link android.widget.LinearLayout}'s similar constructors, also setting some extra values</p>
     * @param context See super class
     * @param attrs See super class
     */
    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        getAttributes(context, attrs);
        initSwitchButton(context);
    }

    /**
     * <p>Same as in {@link android.widget.LinearLayout}'s similar constructors, also setting some extra values</p>
     * @param context See super class
     * @param attrs See super class
     * @param defStyleAttr See super class
     */
    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        getAttributes(context, attrs);
        initSwitchButton(context);
    }

    /**
     * <p>Reads the attributes set on the XML-added SwitchButton and stores them. This gets called automatically when inflating from XML</p>
     *
     * @param context The context
     * @param attrs the attributes to get
     */
    private void getAttributes(Context context, AttributeSet attrs){

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwitchButton,
                0, 0);
        try {
            switchPosition=a.getInteger(R.styleable.SwitchButton_switchPosition, SWITCH_START);
            isChecked=a.getBoolean(R.styleable.SwitchButton_checked, false);
            isEnabled=a.getBoolean(R.styleable.SwitchButton_enabled, true);
            buttonText=a.getString(R.styleable.SwitchButton_text);
            switchPadding =a.getDimensionPixelSize(R.styleable.SwitchButton_toggleSwitchPadding, DEFAULT_SWITCH_PADDING);
        } finally {
            a.recycle();
        }

    }

    /**
     * <p>Inflates compound component views, so that you don't have to. Gets (or should at least get) called automatically from the constructor(s)</p>
     *
     * @param context The context
     */
    private void initSwitchButton(Context context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.switch_button, this,true);

        if(this.isDynamicallyCreated){
            onFinishInflate();
        }

    }

    /**
     * <p>When inflating ont he component views finishes, this function will finish setting them up and applying any attributes to them
     * Called automatically when layout is added form XLM. Also called from dynamic constructor manually.</p>
     *
     * <p>The {@link SuppressLint} annotation is required to address Android bug <a href="https://issuetracker.google.com/37065042">#37065042</a> "setForeground()
     * incorrectly flagged as requiring API 23 (NewApi) for ViewGroups extending FrameLayout"</p>
     */
    @SuppressLint("NewApi")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, VIEW_VERTICAL_PADDING,0,VIEW_VERTICAL_PADDING);

        switchToggle=findViewById(R.id.switch_toggle);
        buttonTextView =findViewById(R.id.switcbutton_text);

        setSwitchPosition(switchPosition);
        buttonTextView.setText(buttonText);
        buttonTextView.setTextColor(resolvePrimaryColour());
        switchToggle.setChecked(isChecked);
        switchToggle.setEnabled(isEnabled);

        if(!isEnabled){
            this.setAlpha(0.5f);
        } else {
            this.setAlpha(1.0f);
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(
                    android.R.attr.selectableItemBackground, outValue, true);
            this.setForeground(getContext().getDrawable(outValue.resourceId));
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN && isEnabled){
                    toggle();
                }
                return false;
            }
        });

        switchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkedChangeListener!=null) {
                    checkedChangeListener.onCheckedChanged(SwitchButton.this, isChecked);
                }
            }
        });
    }


    /**
     * <p>An attempt to resolve application's primary colour ina failsafe way. if nothing helps, use the declared default</p>
     *
     * <p>Credit for this solution goes to this <a href=2https://mbcdev.com/2017/01/16/resolving-android-theme-colours-programmatically>MBCDEV blogpost</a></p>
     *
     * @return int value of primary color
     */
    private int resolvePrimaryColour(){

        TypedValue outValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        boolean wasResolved =
                theme.resolveAttribute(
                        android.R.attr.colorPrimary, outValue, true);
        if (wasResolved) {
            return outValue.resourceId == 0
                    ? outValue.data
                    : ContextCompat.getColor(
                    getContext(), outValue.resourceId);
        } else {
            // fallback colour handling
            return getResources().getColor(R.color.colorPrimary);
        }
    }

    /**
     * <p>The {@link android.widget.Button} class beyond {@link android.os.Build.VERSION_CODES#N} turns the pointer into a hand. So we do the same here too</p>
     *
     * @param event MotionEvenet to capture
     * @param pointerIndex The index of the pointer
     * @return the PointerIcon
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        if (getPointerIcon() == null && isClickable() && isEnabled()) {
            return PointerIcon.getSystemIcon(getContext(), PointerIcon.TYPE_HAND);
        }
        return super.onResolvePointerIcon(event, pointerIndex);
    }


    /****************Getters and setters and stuff (mostly stuff)**********************************/

    /**
     * <p>Mimics the {@link android.widget.Switch} class's {@link android.widget.Switch#toggle()} method. OK, not really, but has the same result, calling {@link android.widget.Switch#toggle()} on the
     * {@link android.widget.Switch} component view</p>
     */
    private void toggle(){
        switchToggle.toggle();
        isChecked=switchToggle.isChecked();
    }

    /**
     * <p>Gets the position of the {@link android.widget.Switch} component relative to the text (either start or end). Evaluate against public constants
     * {@link this#SWITCH_START} ({@value SWITCH_START}) and {@link this#SWITCH_END} ({@value SWITCH_END})</p>
     * <p>Example usage. to check whether the switch is at the start of the text:
     *
     *      <code>if(switchButton.getSwitchPosition() == SwitchButton.SWITCH_START) {...}</code>
     * </p>
     * @return {@value SWITCH_START} ({@link this#SWITCH_START}) for left position and {@value SWITCH_END} ({@link this#SWITCH_END}) for right position
     */
    public int getSwitchPosition() {
        return switchPosition;
    }

    /**
     * <p>Allows you to dynamically set the position of the {@link android.widget.Switch} component. Accepts a single integer parameter, that should use public constants
     * {@link this#SWITCH_START} ({@value SWITCH_START}) and {@link this#SWITCH_END} ({@value SWITCH_END}). If {@link this#SWITCH_START} (or {@value SWITCH_START}) is passed,
     * it will position the {@link android.widget.Switch} component ot the start of the text, any other value will place it at the end of the text.
     * No left or right absolute positioning for now.</p>
     *
     * <p>Example usage. To set the switch is at the end of the text:<br />
     *     switchButton.setSwitchPosition(SwitchButton.SWITCH_END)
     * </p>
     *
     * @param switchPosition Either {@link this#SWITCH_START} ({@value SWITCH_START}), or {@link this#SWITCH_END} ({@value SWITCH_END})
     */
    private void setSwitchPosition(@SwitchTogglePosition int switchPosition) {

        /**
         * Note for the discerning developer: The original approach was to have SwitchButton extend ConstraintLayot
         * and play around with the contsraints when switch position is changed. Certainly better then removing and applying views, but
         * Unfortunately constraint chains and wrap content do not work together well. If the TextView was to be shown correctly it
         * needed a 0dp width, in which case the container (thus the SwitchButton itself) could not use WRAP_CONTENT as width, which
         * is not maintainable without a lot of additional hacker in onMeasure(tm). Besides, changing constraints needs twice as much code
         * (plus the aforementioned hackery), so this approach is ultimately simpler, if not cheaper
         */

        this.switchPosition = switchPosition;

        removeAllViews();
        MarginLayoutParams switchViewParams = (LinearLayout.MarginLayoutParams) switchToggle.getLayoutParams();

        if (switchPosition == SWITCH_START) {
            addView(switchToggle);
            addView(buttonTextView);
            switchViewParams.setMarginEnd(switchPadding);
            switchViewParams.setMarginStart(0);
        } else {
            addView(buttonTextView);
            addView(switchToggle);
            switchViewParams.setMarginEnd(0);
            switchViewParams.setMarginStart(switchPadding);
        }

        requestLayout();
    }

    /**
     * <p>Gets the current button text as a {@link String}</p>
     *
     * @return The button text as a {@link String}
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * <p>Sets the supplied {@link String} as the button text</p>
     *
     * @param buttonText the text to set, as {@link String}
     */
    private void setButtonText(String buttonText) {
        setText(buttonText);
    }

    /**
     * <p>Sets the {@link String} referenced by the supplied resId as button text</p>
     *
     * @param resId id of the String resource to set as button text
     */
    public void setButtonText(int resId){
        setText(getContext().getString(resId));
    }

    /**
     * <p>Internal function called by the {@link this#setButtonText(int)} and {@link this#setButtonText(String)} methods, performs the actual text setting</p>
     *
     * @param text The text {@link String} received to be set
     */
    private void setText(String text){
        this.buttonText = text;
        buttonTextView.setText(buttonText);
    }

    /**
     * <p>Retruns the checked state of the {@link android.widget.Switch} component</p>
     *
     * @return true when {@link android.widget.Switch} is engaged, false when it is not
     */
    public boolean isChecked() {
        return this.switchToggle.isChecked();
    }

    /**
     * <p>Set the checked state of the widget. Just like {@link this#toggle()} but here you explicitly set it true or false</p>
     *
     * @param checked boolean for checked state to be set
     */
    public void setChecked(boolean checked) {
        isChecked = checked;
        switchToggle.setChecked(isChecked);
    }


    /**
     * <p>Returns the enabled state of the widget</p>
     *
     * @return enabled state as a boolean
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * <p>Sets the enabled state of the widget either true or false. This will set the enabled state of each component view,
     * and the alpha value of the whole. Also makes every thing unclickable when disabled.</p>
     *
     * <p>Note: The {@link SuppressLint} annotation is required to address Android bug <a hrtef=" https://issuetracker.google.com/37065042">#37065042</a>
     * ("setForeground() incorrectly flagged as requiring API 23 (NewApi) for ViewGroups extending FrameLayout")/p>
     *
     * @param enabled boolean for the state to be set
     */
    @SuppressLint("NewApi")
    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;

        switchToggle.setEnabled(isEnabled);

        if(!isEnabled){
            this.setAlpha(0.5f);
            this.setClickable(false);
        } else {
            this.setClickable(true);
            this.setAlpha(1.0f);
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(
                    android.R.attr.selectableItemBackground, outValue, true);
            this.setForeground(getContext().getDrawable(outValue.resourceId));
        }
    }

    /**
     * <p>Returns a listener that listens for changes in checked state, if one is set</p>
     *
     * @return {@link OnCheckedChangeListener} object, or null if none is set
     */
    public OnCheckedChangeListener getCheckedChangeListener() {
        return checkedChangeListener;
    }

    /**
     * <p>Sets a listener to listen for changes in checked status changes</p>
     *
     * @param checkedChangeListener an {@link OnCheckedChangeListener} object to be set
     */
    public void setCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    /**
     * <p>Set The {@link android.widget.Switch} component's padding (its distance from the button text) in {@link TypedValue#COMPLEX_UNIT_DIP}</p>
     *
     * @param padding @link android.widget.Switch} component's padding in {@link TypedValue#COMPLEX_UNIT_DIP}
     */
    public void setSwitchPadding(@IntRange(from=0) int padding){
        switchPadding=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
    }

    /**
     * <p>Set The {@link android.widget.Switch} component's padding (its distance from the button text) in any valid unit</p>
     *
     * @param unit Any valid unit
     * @param padding Switch's adding in the above unit
     */
    public void setSwitchPadding(@ValidSizeUnit int unit, @IntRange(from=0) int padding){
        switchPadding=(int) TypedValue.applyDimension(unit, padding, getResources().getDisplayMetrics());
    }

    /**
     * <p>Get the {@link android.widget.Switch} component's padding (its distance from the button text) in {@link TypedValue#COMPLEX_UNIT_DIP}</p>
     *
     * @return The {@link android.widget.Switch} component's padding in {@link TypedValue#COMPLEX_UNIT_DIP}
     */
    public int getSwitchPaddingDp(){
        return Math.round(switchPadding / getResources().getDisplayMetrics().density);
    }

    /**
     * <p>Returns the {@link android.widget.Switch} component's padding (its distance from the button text) in raw {@link TypedValue#COMPLEX_UNIT_PX}</p>
     *
     * @return the {@link android.widget.Switch} component's padding in raw {@link TypedValue#COMPLEX_UNIT_PX}
     */
    public int getSwitchPaddingRaw(){
        return switchPadding;
    }

    /**
     * <p>Pass through the {@link android.widget.TextView} component of the compound view, so it can be manipulated for any unexposed methods</p>
     *
     * @return {@link android.widget.TextView} component of the compound view
     */
    public TextView getTextView(){
        return buttonTextView;
    }

    /**
     * <p>Pass through the {@link android.widget.Switch} component of the compound view, so it can be manipulated for any unexposed methods</p>
     *
     * @return {@link android.widget.Switch} component of the compound view
     */
    public Switch getSwitch(){
        return switchToggle;
    }


    /**********************************Save/restore instance***************************************/

    /**
     * Probably superfluous static constants for the bundle values
     */
    private static final String SWITCH_POSITION_VALUE="SwitchPosition";
    private static final String BUTTON_TEXT_VALUE="ButtonText";
    private static final String IS_CHECKED_VALUE="IsChecked";
    private static final String IS_ENABLED_VALUE="IsEnabled";
    private static final String IS_DYNAMICALLY_CREATED_VALUE="IsDynamicallyCreated";

    private static final String STATE_SUPER_CLASS = "SuperClass";

    /**
     * <p>We don't really like the original {@link super#onSaveInstanceState} so we override it, and tell it to save everything of interest.<br />
     * And it does.</p>
     *
     * @return a Parcelable object (The newly created Bundle)
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_SUPER_CLASS, super.onSaveInstanceState());
        bundle.putInt(SWITCH_POSITION_VALUE, this.switchPosition);
        bundle.putString(BUTTON_TEXT_VALUE, this.buttonText);
        bundle.putBoolean(IS_CHECKED_VALUE, this.isChecked);
        bundle.putBoolean(IS_ENABLED_VALUE, this.isEnabled);
        bundle.putBoolean(IS_DYNAMICALLY_CREATED_VALUE, this.isDynamicallyCreated);
        return bundle;
    }

    /**
     * <p>We don't really like the original {@link super#onRestoreInstanceState} so we override it, and tell to to restore everything of interest.<br />
     * And it does.</p>
     *
     * @param state a {@link android.os.Parcelable} object containing the tate we are restoring
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_CLASS));
            this.switchPosition=bundle.getInt(SWITCH_POSITION_VALUE);
            this.buttonText=bundle.getString(BUTTON_TEXT_VALUE);
            this.isChecked=bundle.getBoolean(IS_CHECKED_VALUE);
            this.isEnabled=bundle.getBoolean(IS_ENABLED_VALUE);
            this.isDynamicallyCreated=bundle.getBoolean(IS_DYNAMICALLY_CREATED_VALUE);

            setSwitchPosition(switchPosition);
            setButtonText(buttonText);
            setChecked(isChecked);
            setEnabled(isEnabled);
        }
    }

    /**
     * <p>It's not like we don't want to handle the saving of component views ourselves, is it? So we do.</p>
     *
     * @param container Yeah, the container, same as in super
     */
    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        super.dispatchFreezeSelfOnly(container);
    }

    /**
     * <p>It's not like we don't want to handle the restoring of component views ourselves, is it? So we do.</p>
     *
     * @param container Yeah, the container, same as in super
     */
    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchThawSelfOnly(container);
    }


    /**************************************Listener(s?)*******************************************/

    /**
     * <p>The one and only OnCheckedChangedListener (for now) listens for changes in the checked state of the Switch button.
     * use it at your own risk.</p>
     *
     * </p>The developer is not responsible for any changes in checked state. Or any property damage. Or anything</p>
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(SwitchButton buttonView, boolean isChecked);
    }

}

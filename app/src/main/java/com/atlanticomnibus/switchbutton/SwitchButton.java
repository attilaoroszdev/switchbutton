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
    public static final int SWITCH_START=0,
            SWITCH_END=1;

    /*************************************Private constants****************************************/
    private final int DEFAULT_SWITCH_PADDING,
            VIEW_VERTICAL_PADDING;



    /*************************************Some annotations*****************************************/
    /** @hide */
    @IntDef({TypedValue.COMPLEX_UNIT_PX, TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP, TypedValue.COMPLEX_UNIT_PT, TypedValue.COMPLEX_UNIT_IN, TypedValue.COMPLEX_UNIT_MM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ValidSizeUnit{}

    /** @hide */
    @IntDef({SWITCH_START, SWITCH_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SwitchTogglePosition{}


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
     * this constructor will be used to dynamically create a SwitchButton from code. Its sole parameter is the context in which
     * it will be added. Everything gets set to a default value and must be changed with the proper setter functions.
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
     * Default constructors from RelativeLayout super class, extended with the necessary bits, These will get called when addimng in XML
     * USing them from code is not tested.
     */

    public SwitchButton(Context context, int position) {
        super(context);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        switchPosition=position;
        isDynamicallyCreated=true;
        initSwitchButton(context);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        getAttributes(context, attrs);
        initSwitchButton(context);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DEFAULT_SWITCH_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        VIEW_VERTICAL_PADDING=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        getAttributes(context, attrs);
        initSwitchButton(context);
    }

    /**
     * reads the attributes set on the XML-added SwitchButton and store them. This gets called automativcally when inflating from XML
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
     * Inflates Ribbon component views, s that oyu don1t have to. Gets (or should at least get) called automatically from the cnstructor(s)
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

    /*Called normally when layout is added form XLM. Also called from dynamic constructor manually*/
    /*The annotation is required to address Android bug #37065042 - https://issuetracker.google.com/37065042 "setForeground() incorrectly flagged as requiring API 23 (NewApi) for ViewGroups extending FrameLayout" */

    /**
     * When inflating ont he component views finishes, this function will finish setting them upand applying any atributes to them
     * Called automatically when layout is added form XLM. Also called from dynamic constructor manually.
     *
     * The SuppressLint annotation is required to address Android bug #37065042 - https://issuetracker.google.com/37065042 "setForeground() incorrectly flagged as requiring API 23 (NewApi) for ViewGroups extending FrameLayout"
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
     * An attempt to resolve application's primary colour ina failsafe way. if nothign helps, use the declared default
     *
     * Credit for this solution goes to <https://mbcdev.com/2017/01/16/resolving-android-theme-colours-programmatically/>
     * @return
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
     * Buton class byond API 24 turns the pointer into a hand. so we do this here too
     * @param event MotionEvenet to capture
     * @param pointerIndex The index of the pointer
     * @return
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
     * Mimics the Switch class's own toggle(). Not really, but has the same result, calling Switch#toggle() on the
     * Switch component view
     */
    public void toggle(){
        switchToggle.toggle();
        isChecked=switchToggle.isChecked();
    }

    /**
     * returns the position of the swith view relative to the text (either left r right). Evaluate against public constants
     * SWITCH_LEFT and SWITCH_RIGHT
     * @return 0 for left position and 1 for right position
     */
    public int getSwitchPosition() {
        return switchPosition;
    }

    /**
     * Allows you to dynamically set the position of the switch. Accepts a single integer. Use public constants SWITCH_START and SWITCH_END
     * If SWITCH_START (or 0) is passed, it will position the Switch ot the start of the text
     * any other value will place it at the END of the text.
     *
     * @param switchPosition either SWITCH_START, or SWITCH_END
     */
    public void setSwitchPosition(@SwitchTogglePosition int switchPosition) {

        /**
         * Note for the discerning developer: The original approach was to have SwitchButton extend ContraintLayout
         * and play around with the contraints when switch position is changed. Certainly better then removing and applying views, but
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
     * Gets the currentbutton text
     * @return The button text as a String
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * Sets the supplied String as the button text
     * @param buttonText the text to set, as String
     */
    public void setButtonText(String buttonText) {
        setText(buttonText);
    }

    /**
     * Sets the string referenced by the supplied resId as button text
     * @param resId id of the String resource to set as button text
     */
    public void setButtonText(int resId){
        setText(getContext().getString(resId));
    }

    /**
     * Internal function called by the setButtonText() methods, performs the actual text setting
     * @param text The text received to be set
     */
    private void setText(String text){
        this.buttonText = text;
        buttonTextView.setText(buttonText);
    }

    /**
     * Retruns the checked state of the widget
     * @return checked state as a boolean
     */
    public boolean isChecked() {
        return this.switchToggle.isChecked();
    }

    /**
     * Set the chedked state pof a widget. just like toggle() but here you explicitly set it true or false
     * @param checked boolean for checked state to be set
     */
    public void setChecked(boolean checked) {
        isChecked = checked;
        switchToggle.setChecked(isChecked);
    }


    /**
     * Returns the enabled state of the widget
     * @return enabled state as a boolean
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets the enabled state of the widget either true or false. This will set the enabled state of each component view,
     * and the alpha valueof the whole. Alos makes everythign unclickable when disabled.
     *
     * The SuppressLint annotation is required to address Android bug #37065042 - https://issuetracker.google.com/37065042 "setForeground() incorrectly flagged as requiring API 23 (NewApi) for ViewGroups extending FrameLayout"
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
     * Returns a listener that listens for changes in checked state
     * @return OnCheckedChangeListener object
     */
    public OnCheckedChangeListener getCheckedChangeListener() {
        return checkedChangeListener;
    }

    /**
     * Sets a listener to listen for changes in checked status changes
     * @param checkedChangeListener an OnCheckedChangeListener object
     */
    public void setCheckedChangeListener(OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }

    /**
     * Set The switches padding (its distance from the button text) in DIP
     * @param padding Switch's padding in DIP
     */
    public void setSwitchPadding(@IntRange(from=0) int padding){
        switchPadding=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
    }

    /**
     * Set The switches padding (its distance from the button text) in any valid unit
     * @param unit Any valid unit
     * @param padding Switch's adding in the above uniot
     */
    public void setSwitchPadding(@ValidSizeUnit int unit, @IntRange(from=0) int padding){
        switchPadding=(int) TypedValue.applyDimension(unit, padding, getResources().getDisplayMetrics());
    }

    /**
     * Returns the Switch's padding in DIP
     * @return the Switch's padding in DIP
     */
    public int getSwitchPaddingDp(){
        return Math.round(switchPadding / getResources().getDisplayMetrics().density);
    }

    /**
     * Returns the Switch's padding in raw PX
     * @return the Switch's padding in raw PX
     */
    public int getSwitchPaddingRaw(){
        return switchPadding;
    }

    /**
     * Directly get the TextView component of the compound view
     * @return TextView component of the compound view
     */
    public TextView getTextView(){
        return buttonTextView;
    }

    /**
     * Directly get the Switch component of the compound view
     * @return Switch component of the compound view
     */
    public Switch getSwitch(){
        return switchToggle;
    }


    /**********************************Save/restore instance***************************************/

    /**
     * Probably superfluous static constants for the bundle values
     */
    private static String SWITCH_POSITION_VALUE="SwitchPosition",
            BUTTON_TEXT_VALUE="ButtonText",
            IS_CHECKED_VALUE="IsChecked",
            IS_ENABLED_VALUE="IsEnabled",
            IS_DYNAMICALLY_CREATED_VALUE="IsDynamicallyCreated";

    private static String STATE_SUPER_CLASS = "SuperClass";

    /**
     * We don't really like the original onSaveInstanceState so we override it, and tell to to save everything of interest.
     * And it does.
     * @return a Parcelale object (The newly created Bundle)
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
     * We don't really like the original onRestoreInstanceState so we override it, and tell to to restore everything of interest.
     * And it does.
     * @param state a Parcelable object containing the tate we are restoring
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
     * It's not like we don1t wantto handle the saving of component views ourselves, is it? So we do.
     * @param container
     */
    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        super.dispatchFreezeSelfOnly(container);
    }

    /**
     * It's not like we don1t wantto handle the restoring of component views ourselves, is it? So we do.
     * @param container
     */
    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        // Makes sure that the state of the child views
        // are not restored since we handle the state in the
        // onSaveInstanceState.
        super.dispatchThawSelfOnly(container);
    }


    /**************************************Listener(s?)*******************************************/

    /**
     * The one and only OnCheckedChangedListener listems for changes in teh checked state of the Switch button.
     * use it at your own risk. The developer is not responsible for any changes in checked
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(SwitchButton buttonView, boolean isChecked);
    }

}

package com.codeoregonapp.patrickleonard.tempestatibus.appwidget;

import android.content.Context;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;
import com.codeoregonapp.patrickleonard.tempestatibus.TempestatibusApplicationSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles dynamic changes to the the size of the app widget by the user
 * by providing the necessary layout information at runtime.
 * Create the DynamicWidgetLayoutController > Set Widget Information > Get Layout ID > Check for different mHas*
 * Then repeat setting the widget information and retrieving layout information.
 * Created by Patrick Leonard on 8/14/2016.
 */
public class DynamicWidgetLayoutController {

    private static final String TAG = DynamicWidgetLayoutController.class.getSimpleName();
    private int mMaxHeight;
    private int mMaxWidth;
    private String mWidgetTheme;
    private Map<String,Integer> m2x1LayoutMap;
    private Map<String,Integer> m2x2LayoutMap;
    private Map<String,Integer> m3x1LayoutMap;
    private Map<String,Integer> m3x2LayoutMap;
    private Map<String,Integer> m3x3LayoutMap;
    private Map<String,Integer> m4x1LayoutMap;
    private Map<String,Integer> m4x3LayoutMap;
    private boolean mHasDataTable;
    private boolean mHasSummary;
    private boolean mHasDateTime;
    private boolean mHasAddress;
    private boolean mHasGridView;
    private TempestatibusApplicationSettings mTempestatibusApplicationSettings;

    public TempestatibusApplicationSettings getTempestatibusApplicationSettings() {
        if(mTempestatibusApplicationSettings == null) {
            setTempestatibusApplicationSettings(new TempestatibusApplicationSettings());
        }
        return mTempestatibusApplicationSettings;
    }

    public void setTempestatibusApplicationSettings(TempestatibusApplicationSettings tempestatibusApplicationSettings) {
        mTempestatibusApplicationSettings = tempestatibusApplicationSettings;
    }

    public DynamicWidgetLayoutController(Context context) {
        //create the proper layout mappings to depend on the theme of the widget.
        getTempestatibusApplicationSettings().createSharedPreferenceContext(context);
        create2x1LayoutMap();create2x2LayoutMap();create3x1LayoutMap();create3x2LayoutMap();
        create3x3LayoutMap();create4x1LayoutMap();create4x3LayoutMap();
    }

    public DynamicWidgetLayoutController setWidgetInformation(int widgetId, int maxHeight, int maxWidth) {
        mWidgetTheme = getTempestatibusApplicationSettings().getWidgetThemePreference(widgetId);
        mMaxHeight = maxHeight; mMaxWidth = maxWidth;
        Log.d(DynamicWidgetLayoutController.TAG,"WidgetID: " + widgetId);
        Log.d(DynamicWidgetLayoutController.TAG,"Widget Theme: " + mWidgetTheme);
        Log.d(DynamicWidgetLayoutController.TAG,"Max Height: " + mMaxHeight);
        Log.d(DynamicWidgetLayoutController.TAG,"Max Width: " + mMaxWidth);
        return this;
    }

    public boolean getHasDataTable() {
        return mHasDataTable;
    }

    public boolean getHasDateTime() {
        return mHasDateTime;
    }

    public boolean getHasAddress() {
        return mHasAddress;
    }

    public boolean getHasSummary() {
        return mHasSummary;
    }

    public boolean getHasGridView() {
        return mHasGridView;
    }
    
    public int getLayoutId() {
        int heightCells = getCells(mMaxHeight);
        int widthCells = getCells(mMaxWidth);
        mHasDataTable = false;
        mHasDateTime = false;
        mHasSummary = false;
        mHasAddress = false;
        mHasGridView = false;
        switch(heightCells) {
            case 1: {
                return getHeight1Layout(widthCells);
            }
            case 2: {
                return getHeight2Layout(widthCells);
            }
            case 3: {
                return getHeight3Layout(widthCells);
            }
            default: {
                return getHeight4Layout(widthCells);
            }
        }
    }

    private int getHeight4Layout(int widthCells) {
        switch (widthCells) {
            case 2: {
                return get2x2Layout();
            }
            case 3: {
                return get3x3Layout();
            }
            default: {
                return get4x3Layout();
            }
        }
    }

    private int getHeight3Layout(int widthCells) {
        switch (widthCells) {
            case 2: {
                get2x2Layout();
            }
            case 3: {
                return get3x3Layout();
            }
            default: {
                return get4x3Layout();
            }
        }
    }

    private int getHeight2Layout(int widthCells) {
        switch (widthCells) {
            case 2: {
               get2x2Layout();
            }
            default: {
                return get3x2Layout();
            }
        }
    }

    private int getHeight1Layout(int widthCells) {
        switch (widthCells) {
            case 2: {
                return get2x1Layout();
            }
            case 3: {
                return get3x1Layout();
            }
            default: {
                return get4x1Layout();
            }
        }
    }

    private int get3x1Layout() {
        mHasSummary = true;
        return m3x1LayoutMap.get(mWidgetTheme);
    }

    private int get2x1Layout() {
        return m2x1LayoutMap.get(mWidgetTheme);
    }

    private int get2x2Layout() {
        mHasSummary = true;
        mHasDateTime = true;
        mHasDataTable = true;
        return m2x2LayoutMap.get(mWidgetTheme);
    }

    private int get3x2Layout() {
        mHasDateTime = true;
        mHasSummary = true;
        mHasDataTable = true;
        return m3x2LayoutMap.get(mWidgetTheme);
    }

    private int get3x3Layout() {
        mHasSummary = true;
        mHasDateTime = true;
        mHasDataTable = true;
        return m3x3LayoutMap.get(mWidgetTheme);
    }

    private int get4x1Layout() {
        mHasSummary = true;
        mHasDataTable = true;
        return m4x1LayoutMap.get(mWidgetTheme);
    }

    private int get4x3Layout() {
        mHasSummary = true;
        mHasDateTime = true;
        mHasAddress = true;
        mHasDataTable = true;
        mHasGridView = true;
        return m4x3LayoutMap.get(mWidgetTheme);
    }

    private int getCells(int pixels) {
        return (int)Math.floor((pixels+30)/90);
    }


    private void create2x1LayoutMap() {
        m2x1LayoutMap = new HashMap<>();
        m2x1LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_2x1_widget_layout);
        m2x1LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_2x1_widget_layout);
        m2x1LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_2x1_widget_layout);
        m2x1LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_2x1_widget_layout);
        m2x1LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_2x1_widget_layout);
        m2x1LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_2x1_widget_layout);
    }

    private void create2x2LayoutMap() {
        m2x2LayoutMap = new HashMap<>();
        m2x2LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_2x2_widget_layout);
        m2x2LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_2x2_widget_layout);
        m2x2LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_2x2_widget_layout);
        m2x2LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_2x2_widget_layout);
        m2x2LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_2x2_widget_layout);
        m2x2LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_2x2_widget_layout);
    }

    private void create3x1LayoutMap() {
        m3x1LayoutMap = new HashMap<>();
        m3x1LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_3x1_widget_layout);
        m3x1LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_3x1_widget_layout);
        m3x1LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_3x1_widget_layout);
        m3x1LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_3x1_widget_layout);
        m3x1LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_3x1_widget_layout);
        m3x1LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_3x1_widget_layout);
    }

    private void create3x2LayoutMap() {
        m3x2LayoutMap = new HashMap<>();
        m3x2LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_3x2_widget_layout);
        m3x2LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_3x2_widget_layout);
        m3x2LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_3x2_widget_layout);
        m3x2LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_3x2_widget_layout);
        m3x2LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_3x2_widget_layout);
        m3x2LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_3x2_widget_layout);
    }

    private void create3x3LayoutMap() {
        m3x3LayoutMap = new HashMap<>();
        m3x3LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_3x3_widget_layout);
        m3x3LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_3x3_widget_layout);
        m3x3LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_3x3_widget_layout);
        m3x3LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_3x3_widget_layout);
        m3x3LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_3x3_widget_layout);
        m3x3LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_3x3_widget_layout);
    }

    private void create4x1LayoutMap() {
        m4x1LayoutMap = new HashMap<>();
        m4x1LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_4x1_widget_layout);
        m4x1LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_4x1_widget_layout);
        m4x1LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_4x1_widget_layout);
        m4x1LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_4x1_widget_layout);
        m4x1LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_4x1_widget_layout);
        m4x1LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_4x1_widget_layout);
    }

    private void create4x3LayoutMap() {
        m4x3LayoutMap = new HashMap<>();
        m4x3LayoutMap.put(TempestatibusApplicationSettings.AUTUMN_THEME_PREFERENCE,R.layout.autumn_4x3_widget_layout);
        m4x3LayoutMap.put(TempestatibusApplicationSettings.SUMMER_THEME_PREFERENCE,R.layout.summer_4x3_widget_layout);
        m4x3LayoutMap.put(TempestatibusApplicationSettings.SPRING_THEME_PREFERENCE,R.layout.spring_4x3_widget_layout);
        m4x3LayoutMap.put(TempestatibusApplicationSettings.WINTER_THEME_PREFERENCE,R.layout.winter_4x3_widget_layout);
        m4x3LayoutMap.put(TempestatibusApplicationSettings.CLEAR_BLACK_THEME_PREFERENCE,R.layout.clear_black_4x3_widget_layout);
        m4x3LayoutMap.put(TempestatibusApplicationSettings.CLEAR_WHITE_THEME_PREFERENCE,R.layout.clear_white_4x3_widget_layout);
    }

}

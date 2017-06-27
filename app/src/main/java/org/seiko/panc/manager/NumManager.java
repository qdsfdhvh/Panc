package org.seiko.panc.manager;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.seiko.panc.App;

/**
 * Created by Seiko on 2017/6/21/021. Y
 */

public class NumManager {

    private static final int COUNT_TWO = getCount(2);
    private static final int COUNT_THREE = getCount(3);
//    private static final int COUNT_FOUR = getCount(4);

    public static final int READER_NUM = COUNT_THREE;
    public static final int SITED_NUM = COUNT_TWO;
    public static final int HOTS_NUM = COUNT_TWO;
    public static final int TAGS_NUM = COUNT_THREE;

    private static int getCount(int count) {
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float scale = App.getContext().getResources().getDisplayMetrics().density;
        int widthDps = (int) (dm.widthPixels / scale + 0.5f);

        int num1;
        int num2;

        switch (count) {
            case 4:  num1 = 120; num2 = 60; break;
            case 3:  num1 = 140; num2 = 60; break;
            default: num1 = 180; num2 = 60; break;
        }

        while (widthDps / count > num1) count++;
        while (widthDps / count < num2) count--;

        return count;
    }
}

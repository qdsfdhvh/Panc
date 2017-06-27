package org.seiko.panc.base.mvp;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by xianicai on 2017/3/13. Y
 */

public interface IView {

     /** 一些其他的运行步骤 */
     void initViews(Bundle bundle);

     /** 界面id */
     int LayoutResID();

     /** 界面名称 */
     String LayoutTitle();

     Context getContext();

}

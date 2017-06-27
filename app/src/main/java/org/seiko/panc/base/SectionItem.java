package org.seiko.panc.base;

import android.view.View;
import android.view.ViewGroup;

/**
 * Author: Season(ssseasonnn@gmail.com)
 * Date: 2016/9/22
 * Time: 15:33
 * FIXME
 */
public interface SectionItem {

    View createView(ViewGroup parent);

    void onBind();
}

package org.seiko.panc.contract;

import org.seiko.panc.base.mvp.IView;
import org.seiko.panc.bean.ComicBean;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class BookContract {

    public interface View extends IView {
        void onSuccess(ComicBean content);
        void onFailed();
    }

    public interface Presenter {
        void getData(String source, String url);
    }

}

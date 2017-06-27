package org.seiko.panc.contract;

import org.seiko.panc.base.mvp.IView;
import org.seiko.panc.bean.SectionBean;

import java.util.List;

/**
 * Created by Seiko on 2017/6/15/015. Y
 */

public class SectionContract {
    public interface View extends IView {
        void onSuccess(List<SectionBean> list);
        void onFailed();
    }

    public interface Presenter {
        void getData(String source, String url);
    }
}

package org.seiko.panc.contract;

import org.seiko.panc.bean.SitedBean;
import org.seiko.panc.base.mvp.IView;
import java.util.List;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class SitedContract {

    public interface View extends IView {
        void onSuccess(List<SitedBean> list);
        void onFailed();
    }

    public interface Presenter {
        void getData();
    }

}

package org.seiko.panc.contract;

import org.seiko.panc.bean.SearchBean;
import org.seiko.panc.base.mvp.IView;
import java.util.List;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class SearchContract {

    public interface View extends IView {
        void onSuccess(List<SearchBean> list);
        void onFailed();
    }

    public interface Presenter {
        void getData(String keyword);
    }

}

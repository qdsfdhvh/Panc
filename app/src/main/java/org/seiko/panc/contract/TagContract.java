package org.seiko.panc.contract;

import org.seiko.panc.base.mvp.IView;
import org.seiko.panc.bean.TagBean;
import org.seiko.panc.bean.TagsBean;

import java.util.List;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class TagContract {

    public interface View extends IView {
        void onSuccess(List<TagBean> list);
        void onFailed(int page);
    }

    public interface Presenter {
        void getData(TagsBean bean, int page);
    }

}

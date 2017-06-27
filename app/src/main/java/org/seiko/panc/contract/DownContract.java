package org.seiko.panc.contract;

import org.seiko.panc.base.mvp.IView;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.DownloadBean;
import java.util.List;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class DownContract {

    public interface View extends IView {
        void onSuccess(List<DownloadBean> list);
    }

    public interface Presenter {
        void getData(ComicBean comic);
    }

}

package org.seiko.panc.contract;

import org.seiko.panc.base.mvp.IView;
import org.seiko.panc.bean.BookSectionBean;
import org.seiko.panc.bean.ComicBean;

import java.util.List;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class BookDownContract {

    public interface View extends IView {
        void onSuccess(List<BookSectionBean> list);
        void onClose();
    }

    public interface Presenter {
        void getData(ComicBean comic);
        void onDownload(List<BookSectionBean>  list);
    }

}

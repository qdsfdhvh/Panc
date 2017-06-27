package org.seiko.panc.manager;

import android.support.annotation.Nullable;
import android.util.SparseArray;
import org.seiko.panc.App;
import org.seiko.panc.rx.ToAnotherList;
import org.seiko.panc.sited.YhSource;
import org.seiko.panc.utils.FileUtil;
import java.io.File;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class SourceManager {

    private static SourceManager instance;
    private final SparseArray<YhSource> _sourceList;

    private SourceManager() {_sourceList = new SparseArray<>();}

    public static SourceManager getInstance() {
        if (instance == null) {
            synchronized (SourceManager.class) {
                if (instance == null) {
                    instance = new SourceManager();
                }
            }
        }
        return instance;
    }

    public List<String> getSourceNames() {
        return  FileUtil.loadFile(PathManager.sitePath)
                .compose(new ToAnotherList<>(new Function<File, String>() {
                    @Override
                    public String apply(@NonNull File file) throws Exception {
                        return file.getName().replace(".sited", "");
                    }
                })).blockingFirst();
    }


    //===============================================
    public Flowable<YhSource> rxgetSource(final String source) {
        YhSource sd = getSource(source);
        return Flowable.just(sd).subscribeOn(Schedulers.io());
    }

    public YhSource getSource(String source) {
        YhSource sd =  _sourceList.get(source.hashCode());
        if (sd == null) {
            String sited = FileUtil.readTextFromSDcard(PathManager.getSitePath(source));
            sd = loadSource(sited);
        }
        return sd;
    }

    public YhSource loadSource(final String sited) {
        YhSource sd = parseSource(sited);
        if (sd != null) {
            _sourceList.put(sd.Title().hashCode(), sd);
        }
        return sd;
    }

    //===============================================
    /** 加载sited引擎 */
    @Nullable
    private YhSource parseSource(final String sited){
        try {
            return new YhSource(App.getInstance(), sited);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

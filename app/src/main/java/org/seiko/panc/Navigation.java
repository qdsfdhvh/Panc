package org.seiko.panc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.TagsBean;
import org.seiko.panc.ui.book.BookActivity;
import org.seiko.panc.ui.book.BookDownActivity;
import org.seiko.panc.ui.down.DownActivity;
import org.seiko.panc.ui.home.HomeActivity;
import org.seiko.panc.ui.search.SearchActivity;
import org.seiko.panc.ui.section.SectionActivity;
import org.seiko.panc.ui.tag.TagActivity;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class Navigation {

    public static void showHome(Context from, String source) {
        Intent intent = new Intent(from, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_SOURCE, source);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void showSearch(Context from) {
        Intent intent = new Intent(from, SearchActivity.class);
        from.startActivity(intent);
    }

    public static void showTag(Context from, TagsBean bean) {
        Intent intent = new Intent(from, TagActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_TAGS, bean);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void showBook(Context from,String source, String url, String logo) {
        Intent intent = new Intent(from, BookActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        bundle.putString("url", url);
        bundle.putString("logo", logo);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void showSection(Context from, ComicBean comic) {
        Intent intent = new Intent(from, SectionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_COMIC, comic);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void showBookDown(Context from, ComicBean comic) {
        Intent intent = new Intent(from, BookDownActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_COMIC, comic);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void showDown(Context from, ComicBean comic) {
        Intent intent = new Intent(from, DownActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_COMIC, comic);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    public static void showOutWeb(final Context from, final String url) {
        final Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        from.startActivity(it);
    }

}

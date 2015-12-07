package eu.kanade.mangafeed.ui.setting;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import java.util.List;

import javax.inject.Inject;

import eu.kanade.mangafeed.App;
import eu.kanade.mangafeed.data.chaptersync.BaseChapterSync;
import eu.kanade.mangafeed.data.chaptersync.ChapterSyncManager;
import eu.kanade.mangafeed.data.source.SourceManager;
import eu.kanade.mangafeed.data.source.base.Source;
import eu.kanade.mangafeed.ui.setting.preference.ChapterSyncLoginDialog;
import eu.kanade.mangafeed.ui.setting.preference.SourceLoginDialog;
import rx.Observable;

public class SettingsAccountsFragment extends SettingsNestedFragment {

    @Inject SourceManager sourceManager;
    @Inject ChapterSyncManager syncManager;

    public static SettingsNestedFragment newInstance(int resourcePreference, int resourceTitle) {
        SettingsNestedFragment fragment = new SettingsAccountsFragment();
        fragment.setBundle(resourcePreference, resourceTitle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(getActivity()).getComponent().inject(this);

        PreferenceScreen screen = getPreferenceScreen();

        List<Source> sourceAccounts = getSourcesWithLogin();

        PreferenceCategory sourceCategory = new PreferenceCategory(screen.getContext());
        sourceCategory.setTitle("Sources");
        screen.addPreference(sourceCategory);

        for (Source source : sourceAccounts) {
            SourceLoginDialog dialog = new SourceLoginDialog(
                    screen.getContext(), preferences, source);
            dialog.setTitle(source.getName());

            sourceCategory.addPreference(dialog);
        }

        PreferenceCategory chapterSyncCategory = new PreferenceCategory(screen.getContext());
        chapterSyncCategory.setTitle("Sync");
        screen.addPreference(chapterSyncCategory);

        for (BaseChapterSync sync : syncManager.getChapterSyncServices()) {
            ChapterSyncLoginDialog dialog = new ChapterSyncLoginDialog(
                    screen.getContext(), preferences, sync);
            dialog.setTitle(sync.getName());

            chapterSyncCategory.addPreference(dialog);
        }

    }

    private List<Source> getSourcesWithLogin() {
        return Observable.from(sourceManager.getSources())
                .filter(Source::isLoginRequired)
                .toList()
                .toBlocking()
                .single();
    }

}

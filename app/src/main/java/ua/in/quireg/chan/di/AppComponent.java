package ua.in.quireg.chan.di;

import dagger.Subcomponent;
import ua.in.quireg.chan.boards.makaba.MakabaApiReader;
import ua.in.quireg.chan.boards.makaba.MakabaUrlBuilder;
import ua.in.quireg.chan.mvp.models.BoardsListInteractor;
import ua.in.quireg.chan.mvp.presenters.BoardsListPresenter;
import ua.in.quireg.chan.mvp.routing.MainNavigator;
import ua.in.quireg.chan.services.CacheDirectoryManager;
import ua.in.quireg.chan.services.PostSender;
import ua.in.quireg.chan.services.presentation.OpenTabsManager;
import ua.in.quireg.chan.ui.activities.MainActivity;
import ua.in.quireg.chan.ui.adapters.HistoryAdapter;
import ua.in.quireg.chan.ui.adapters.ThreadsListAdapter;
import ua.in.quireg.chan.ui.fragments.AppPreferenceFragment;
import ua.in.quireg.chan.ui.fragments.BaseListFragment;
import ua.in.quireg.chan.ui.fragments.BoardsListFragment;
import ua.in.quireg.chan.ui.fragments.FavoritesFragment;
import ua.in.quireg.chan.ui.fragments.HistoryFragment;
import ua.in.quireg.chan.ui.fragments.PostsListFragment;
import ua.in.quireg.chan.ui.fragments.ThreadsListFragment;


@AppScope
@Subcomponent(modules = {NetModule.class, DataRepositoryModule.class, WebsiteModule.class})
public interface AppComponent {

    void inject(MainActivity activity);
    void inject(BoardsListFragment fragment);
    void inject(CacheDirectoryManager cacheDirectoryManager);
    void inject(AppPreferenceFragment appPreferenceFragment);
    void inject(BoardsListPresenter boardsListPresenter);
    void inject(OpenTabsManager openTabsManager);

    void inject(BoardsListInteractor boardsListInteractor);
    void inject(HistoryFragment historyFragment);
    void inject(HistoryAdapter historyAdapter);

    void inject(FavoritesFragment favoritesFragment);
    void inject(ThreadsListFragment threadsListFragment);
    void inject(MakabaUrlBuilder makabaUrlBuilder);
    void inject(MakabaApiReader makabaApiReader);
    void inject(PostSender postSender);
    void inject(BaseListFragment baseListFragment);
    void inject(ThreadsListAdapter threadsListAdapter);

    void inject(MainNavigator mainNavigator);

    void inject(PostsListFragment postsListFragment);



}

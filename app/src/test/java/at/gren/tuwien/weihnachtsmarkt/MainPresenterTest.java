package at.gren.tuwien.weihnachtsmarkt;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;
import rx.Observable;
import at.gren.tuwien.weihnachtsmarkt.data.DataManager;
import at.gren.tuwien.weihnachtsmarkt.data.model.Ribot;
import at.gren.tuwien.weihnachtsmarkt.test.common.TestDataFactory;
import at.gren.tuwien.weihnachtsmarkt.ui.main.MainMvpView;
import at.gren.tuwien.weihnachtsmarkt.ui.main.MainPresenter;
import at.gren.tuwien.weihnachtsmarkt.util.RxSchedulersOverrideRule;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock MainMvpView mMockMainMvpView;
    @Mock DataManager mMockDataManager;
    private MainPresenter mMainPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        mMainPresenter = new MainPresenter(mMockDataManager);
        mMainPresenter.attachView(mMockMainMvpView);
    }

    @After
    public void tearDown() {
        mMainPresenter.detachView();
    }

    @Test
    public void loadRibotsReturnsRibots() {
        List<Weihnachtsmarkt> märkte = TestDataFactory.makeListMärkte(10);
        when(mMockDataManager.getMärkte())
                .thenReturn(Observable.just(märkte));

        mMainPresenter.loadMärkte();
        verify(mMockMainMvpView).showAdventmaerkte(märkte);
        verify(mMockMainMvpView, never()).showAdventmaerkteEmpty();
        verify(mMockMainMvpView, never()).showError();
    }

    @Test
    public void loadRibotsReturnsEmptyList() {
        when(mMockDataManager.getRibots())
                .thenReturn(Observable.just(Collections.<Ribot>emptyList()));

        mMainPresenter.loadMärkte();
        verify(mMockMainMvpView).showAdventmaerkteEmpty();
        verify(mMockMainMvpView, never()).showAdventmaerkte(anyListOf(Weihnachtsmarkt.class));
        verify(mMockMainMvpView, never()).showError();
    }

    @Test
    public void loadRibotsFails() {
        when(mMockDataManager.getRibots())
                .thenReturn(Observable.<List<Ribot>>error(new RuntimeException()));

        mMainPresenter.loadMärkte();
        verify(mMockMainMvpView).showError();
        verify(mMockMainMvpView, never()).showAdventmaerkteEmpty();
        verify(mMockMainMvpView, never()).showAdventmaerkte(anyListOf(Weihnachtsmarkt.class));
    }

}
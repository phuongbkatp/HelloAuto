package com.appian.manutdvietnam.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appian.manutdvietnam.R;
import com.appian.manutdvietnam.app.adapter.NavigationListAdapter;
import com.appian.manutdvietnam.app.home.presenter.SeasonLeagueTeamPresenter;
import com.appian.manutdvietnam.app.home.view.SeasonLeagueTeamView;
import com.appian.manutdvietnam.app.league.LeagueFragment;
import com.appian.manutdvietnam.app.more.MoreFragment;
import com.appian.manutdvietnam.app.newstopic.NewsTopicFragment;
import com.appian.manutdvietnam.app.setting.SettingActivity;
import com.appian.manutdvietnam.app.team.TeamFragment;
import com.appian.manutdvietnam.app.user.LogInActivity;
import com.appian.manutdvietnam.app.user.OnBtnLogoutClickListener;
import com.appian.manutdvietnam.app.user.UserFragment;
import com.appian.manutdvietnam.data.account.AccountManager;
import com.appian.manutdvietnam.data.account.UserAccount;
import com.appian.manutdvietnam.data.app.AppConfig;
import com.appian.manutdvietnam.data.app.AppConfigManager;
import com.appian.manutdvietnam.data.app.Language;
import com.appian.manutdvietnam.network.ConnectivityEvent;
import com.appian.manutdvietnam.receiver.ReceiverHelper;
import com.appian.manutdvietnam.util.BottomNavigationViewHelper;
import com.appian.manutdvietnam.util.ImageLoader;
import com.appnet.android.ads.admob.InterstitialAdMob;
import com.appnet.android.football.fbvn.data.LeagueSeason;
import com.appnet.android.social.auth.OnLogoutListener;
import com.github.fernandodev.easyratingdialog.library.EasyRatingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ToolbarViewListener, SeasonLeagueTeamView {
    private static final String TAG_FRAGMENT_LEAGUE = "fragment_league";
    private static final String TAG_FRAGMENT_HOME = "fragment_home";
    private static final String TAG_FRAGMENT_TOPIC = "fragment_topic";
    private static final String TAG_FRAGMENT_SQUAD = "fragment_squad";
    private static final String TAG_FRAGMENT_PROFILE = "fragment_profile";
    private static final String TAG_FRAGMENT_SETTING = "fragment_setting";

    private boolean doubleBackToExitPressedOnce = false;

    private static final int RC_SETTING = 1;
    private static final int RC_LOGIN = 2;

    private TextView txtTitle;

    private List<LeagueSeason> mLeagueSesons;

    private InterstitialAdMob mInterstitialAdMob;

    private EasyRatingDialog easyRatingDialog;

    private BroadcastReceiver mUserProfileChangedReceiver;

    private SeasonLeagueTeamPresenter mSeasonLeagueTeamPresenter;

    BottomNavigationView bottomNavigation;
    private boolean mIShowAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLeagueSesons = new ArrayList<>();
        txtTitle = findViewById(R.id.txtTitle);

        easyRatingDialog = new EasyRatingDialog(this);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigationView);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onBottomNavigationSelect(item);
                return true;
            }
        });

        switchFragment(TAG_FRAGMENT_HOME, null);

        mSeasonLeagueTeamPresenter = new SeasonLeagueTeamPresenter();
        mSeasonLeagueTeamPresenter.attachView(this);
        loadLeftMenu();
        AppConfig appConfig = AppConfigManager.getInstance().getAppConfig(this);
        mInterstitialAdMob = new InterstitialAdMob(this, appConfig.getAdmobInterstitial());
    }

    @Override
    protected void onStart() {
        super.onStart();
        easyRatingDialog.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        easyRatingDialog.showIfNeeded();
        mInterstitialAdMob.loadAd();
        mIShowAds = false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        //show ad after pressing back
        FragmentManager fm = getSupportFragmentManager();
        Fragment homeFragment = fm.findFragmentByTag(TAG_FRAGMENT_HOME);
        if (homeFragment != null && homeFragment.isVisible()) {
            if(!mIShowAds) {
                mInterstitialAdMob.show();
                mIShowAds = true;
            }
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.confirm_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            switchFragment(TAG_FRAGMENT_HOME);
        }
    }

    private void loadLeftMenu() {
        AppConfig appConfig = AppConfigManager.getInstance().getAppConfig(this);
        mSeasonLeagueTeamPresenter.loadSeasonLeaguesByTeam(appConfig.getCurrentSeasonId(), appConfig.getTeamId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConnectivityEvent event) {
        if (event.isConnected()) {
            if (mLeagueSesons != null && mLeagueSesons.isEmpty()) {
                loadLeftMenu();
            }
        }
    }

    private void onBottomNavigationSelect (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.llNewsFeedMenu:
                switchFragment(TAG_FRAGMENT_HOME);
                break;
            case R.id.llSquadMenu:
                switchFragment(TAG_FRAGMENT_SQUAD);
                break;
            case R.id.llLeagueMenu:
                LeagueSeason leagueSeason = mLeagueSesons.get(0);
                Bundle args = new Bundle();
                args.putInt("league_id", leagueSeason.getSofaLeagueId());
                args.putInt("season_id", leagueSeason.getSofaId());
                args.putString("league_name", leagueSeason.getLeagueName());
                switchFragment(TAG_FRAGMENT_LEAGUE, args);
                break;
            case R.id.rlSetting:
                switchFragment(TAG_FRAGMENT_SETTING);
                break;
            default:
                switchFragment(TAG_FRAGMENT_HOME);
                break;

        }
    }

    private void switchFragment(String tag) {
        switchFragment(tag, null);
    }

    private void switchFragment(String tag, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (TAG_FRAGMENT_HOME.equals(tag)) {
            if (fragment == null) {
                fragment = new HomeFragment();
            }
        } else if (TAG_FRAGMENT_TOPIC.equals(tag)) {
            if (fragment == null) {
                fragment = new NewsTopicFragment();
            }
        } else if (TAG_FRAGMENT_LEAGUE.equals(tag)) {
            if (fragment == null) {
                fragment = LeagueFragment.newInstance(args);
            } else if (fragment instanceof LeagueFragment) {
                ((LeagueFragment) fragment).updateLeagueSeason(args);
            }
        } else if (TAG_FRAGMENT_SQUAD.equals(tag)) {
            if (fragment == null) {
                fragment = new TeamFragment();
            }
        } else if (TAG_FRAGMENT_PROFILE.equals(tag)) {
            if (fragment == null) {
                fragment = new UserFragment();
            }
        } else if (TAG_FRAGMENT_SETTING.equals(tag)) {
            if (fragment == null) {
                fragment = new MoreFragment();
            }
        }
        if (fragment != null) {
            fm.beginTransaction().replace(R.id.main_layout_container, fragment, tag).commit();
        }
    }

    private void switchActivity(int requestCode) {
        Intent intent;
        switch (requestCode) {
            case RC_SETTING:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case RC_LOGIN:
                intent = new Intent(this, LogInActivity.class);
                startActivityForResult(intent, RC_LOGIN);
                break;
        }
    }

    @Override
    public void changeToolbarTitle(String title) {
        txtTitle.setText(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiverHelper.unregisterReceiver(this, mUserProfileChangedReceiver);
        mSeasonLeagueTeamPresenter.detachView();
    }

    @Override
    public void showSeasonLeagueTeam(List<LeagueSeason> data) {
        mLeagueSesons.clear();
        mLeagueSesons.addAll(data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Language.onAttach(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        registerEventBus(false);
    }

    private void registerEventBus(boolean isRegister) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        if (isRegister) {
            EventBus.getDefault().register(this);
        } else {
            EventBus.getDefault().unregister(this);
        }
    }
}
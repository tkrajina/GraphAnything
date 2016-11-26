package info.puzz.graphanything.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.List;
import java.util.Map;

import info.puzz.graphanything.R;
import info.puzz.graphanything.dao.DAO;
import info.puzz.graphanything.models2.Graph;
import info.puzz.graphanything.models2.GraphColumn;
import info.puzz.graphanything.utils.DialogUtils;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    DAO dao;

    private boolean navigationSet;

    private boolean walkAwayAllowed = true;
    private String walkAwayMessage = "";

    public DAO getDAO() {
        if (this.dao == null) {
            this.dao = new DAO(this);
            this.dao.open();
        }
        return this.dao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Opening connection");

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigation();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Closing connection");
        super.onPause();

        if (this.dao != null) {
            this.dao.close();
            this.dao = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void disallowWalkAway(int res) {
        this.walkAwayAllowed = false;
        this.walkAwayMessage = this.getString(res);
    }

    protected void allowWalkAway() {
        this.walkAwayAllowed = true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            PackageInfo info = null;
            try {
                info = getPackageManager().getPackageInfo(this.getPackageName(), 0);

                HelpActivity.start(this, getString(R.string.about), getString(R.string.info_contents, info.versionName, info.versionCode));
            } catch (PackageManager.NameNotFoundException e) {
                throw new Error(e.getMessage(), e);
            }
        } else if (id == R.id.nav_graphs) {
            GraphListActivity.start(this);
        } else if (id == R.id.nav_code) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tkrajina/GraphAnything")));
        } else if (id == R.id.nav_help) {
            HelpActivity.start(this, getString(R.string.help), getString(R.string.help_contents));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void startActivity(final Intent intent) {
        if (!walkAwayAllowed) {
            DialogUtils.showYesNoButton(this, walkAwayMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == Dialog.BUTTON_POSITIVE) {
                        BaseActivity.super.startActivity(intent);
                    }
                }
            });
        } else {
            super.startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (!walkAwayAllowed) {
            DialogUtils.showYesNoButton(this, walkAwayMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == Dialog.BUTTON_POSITIVE) {
                        BaseActivity.super.onBackPressed();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    protected void setupNavigation() {
        if (navigationSet) {
            return;
        }
        navigationSet = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Map<Long, GraphColumn> firstColumns = getDAO().getFirstColumnsByGraphId();
        List<Graph> graphs = getDAO().getGraphsByTimerActiveAndUpdatedDesc();
        if (graphs.size() > 0 && !this.getClass().equals(GraphListActivity.class)) {
            SubMenu submenu = navigationView.getMenu().addSubMenu(R.string.graphs);
            for (final Graph graph : graphs) {
                MenuItem menu = submenu.add(graph.name).setIcon(graph.getActivityIcon(firstColumns.get(graph._id)));
                menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        GraphActivity.start(BaseActivity.this, graph._id, 0);
                        return true;
                    }
                });
            }
        }
        navigationView.setNavigationItemSelectedListener(this);
    }
}

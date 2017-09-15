package com.mihir.paginationdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mihir.paginationdemo.api.ApiClient;
import com.mihir.paginationdemo.api.ApiInterface;
import com.mihir.paginationdemo.models.UserList;
import com.mihir.paginationdemo.models.UsersResponse;
import com.mihir.paginationdemo.models.sorting.SortByFirstName;
import com.mihir.paginationdemo.models.sorting.SortByLastName;
import com.mihir.paginationdemo.utils.PaginationAdapterCallback;
import com.mihir.paginationdemo.utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements PaginationAdapterCallback {

    private static final String TAG = "HomeActivity";
    private static final String LIST_STATE_KEY = "RecyclerViewInstance";

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    private List<UserList> userList = new ArrayList<>();

    private ApiInterface apiInterface;
    private String[] pageUrls = {"59b3f0b0100000e30b236b7e", "59ac28a9100000ce0bf9c236", "59ac293b100000d60bf9c239"};
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imgSort = (ImageView) toolbar.findViewById(R.id.imgSort);
        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThemeSelectorPopup(v);
            }
        });

        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        btnRetry = (Button) findViewById(R.id.error_btn_retry);
        txtError = (TextView) findViewById(R.id.error_txt_cause);

        adapter = new PaginationAdapter(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mListState != null) {
            linearLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();

        callUserListApi().enqueue(new Callback<UsersResponse>() {
            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                // Got data. Send it to adapter
                hideErrorView();

                userList.addAll(fetchResults(response));
                progressBar.setVisibility(View.GONE);
                adapter.addAll(fetchResults(response));
                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callUserListApi().enqueue(new Callback<UsersResponse>() {
            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                userList.addAll(fetchResults(response));
                adapter.addAll(fetchResults(response));

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    /**
     * @param response extracts List<{@link UserList >} from response
     * @return
     */
    private List<UserList> fetchResults(Response<UsersResponse> response) {
        UsersResponse usersResponse = response.body();
        return usersResponse.getUserList();
    }

    private Call<UsersResponse> callUserListApi() {
        return apiInterface.getUsers(currentPage);
    }


    @Override
    public void retryPageLoad() {
        loadNextPage();
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        mListState = linearLayoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        // Retrieve list state and list/item positions
        if (state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    private void showThemeSelectorPopup(View v) {
        PopupMenu popup = new PopupMenu(HomeActivity.this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup_sorting, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sortingByFirstName:
                        Collections.sort(userList, new SortByFirstName());
                        adapter.clear();
                        adapter.addAll(userList);
                        return true;
                    case R.id.sortingByLastName:
                        Collections.sort(userList, new SortByLastName());
                        adapter.clear();
                        adapter.addAll(userList);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

}

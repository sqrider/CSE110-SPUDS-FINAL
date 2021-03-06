package com.spuds.eventapp.Search;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spuds.eventapp.Firebase.UserFirebase;
import com.spuds.eventapp.R;
import com.spuds.eventapp.Shared.MainActivity;
import com.spuds.eventapp.Shared.UserSearchResult;

import java.util.ArrayList;

/**
 * Created by David on 5/28/16.
 */

/*---------------------------------------------------------------------------
Class Name:                SearchUsersFragment
Description:               Contains information about SearchUsersFragment
---------------------------------------------------------------------------*/
public class SearchUsersFragment extends Fragment {
    private ArrayList<UserSearchResult> users;
    public SearchUsersRVAdapter adapter;
    UserFirebase userFirebase;
    String userId;

    /*---------------------------------------------------------------------------
    Function Name:                SearchEventsFragment
    Description:                  Required default no-argument constructor
    Input:                        None.
    Output:                       None.
    ---------------------------------------------------------------------------*/
    public SearchUsersFragment() {
        // Required empty public constructor
    }

    /*---------------------------------------------------------------------------
    Function Name:                onCreate
    Description:                  called when the fragment is created
    Input:                        Bundle savedInstanceState
    Output:                       None.
    ---------------------------------------------------------------------------*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null)
            userId = bundle.getString(getString(R.string.user_id));
        userFirebase = new UserFirebase();
    }

    /*---------------------------------------------------------------------------
    Function Name:                onCreateView()
    Description:                  Inflates View layout and sets fonts programmatically
    Input:                        LayoutInflater inflater - inflates layout
                                  ViewGroup container - parent view group
                                  Bundle savedInstanceState
    Output:                       View to be inflated
    ---------------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.recycler_no_refresh, container, false);
        final RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);


        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        rv.setLayoutManager(llm);

        users = new ArrayList<>();

        adapter = new SearchUsersRVAdapter(users, this);
        rv.setAdapter(adapter);

        if (userId != null) {
            if (userId.equals(UserFirebase.uId)) {

                users.add(new UserSearchResult(UserFirebase.thisUser, false));
                adapter.notifyItemInserted(0);

            } else {
                userFirebase.getAnotherUser(userId);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (!userFirebase.threadCheckAnotherUser) {
                            try {
                                Thread.sleep(77);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        userFirebase.isSubscribed(userFirebase.anotherUser.getUserId());
                        new Thread(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void run() {
                                Log.d("idIsGoing2", String.valueOf(userFirebase.idIsSubscribed));
                                while (userFirebase.idIsSubscribed == 0) {
                                    Log.d("profilehere", "profilehere");
                                    try {
                                        Thread.sleep(75);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }

                                boolean following;
                                if (userFirebase.idIsSubscribed == 1)
                                    following = false;
                                else
                                    following = true;

                                users.add(new UserSearchResult(userFirebase.anotherUser, following));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemInserted(0);

                                    }
                                });
                            }
                        }).start();

                    }
                }).start();

            }
        }


        //calls the function to refresh the page.
        //setupRefresh(v);

        return v;
    }

    /*---------------------------------------------------------------------------
    Function Name:                refreshing
    Description:                  called when user pulls down to refresh the page
    Input:                        View view
    Output:                       View
    ---------------------------------------------------------------------------*/
    public void refreshing(View view) {
        final SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        users.clear();

                        if (userId.equals(UserFirebase.uId)) {

                            users.add(new UserSearchResult(userFirebase.anotherUser, false));
                            adapter.notifyItemInserted(0);


                        } else {
                            userFirebase.getAnotherUser(userId);

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    while (!userFirebase.threadCheckAnotherUser) {
                                        try {
                                            Thread.sleep(77);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    userFirebase.isSubscribed(userFirebase.anotherUser.getUserId());
                                    new Thread(new Runnable() {
                                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void run() {
                                            Log.d("idIsGoing2", String.valueOf(userFirebase.idIsSubscribed));
                                            while (userFirebase.idIsSubscribed == 0) {
                                                Log.d("profilehere", "profilehere");
                                                try {
                                                    Thread.sleep(75);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            boolean following;
                                            if (userFirebase.idIsSubscribed == 1)
                                                following = false;
                                            else
                                                following = true;

                                            users.add(new UserSearchResult(userFirebase.anotherUser, following));
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyItemInserted(0);

                                                }
                                            });
                                        }
                                    }).start();

                                }
                            }).start();
                        }
                    }
                });
    }

    /*---------------------------------------------------------------------------
    Function Name:                onResume()
    Description:                  called every time the fragment is resumed
    Input:                        None.
    Output:                       None.
    ---------------------------------------------------------------------------*/
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).addSearchToolbar();
    }

}
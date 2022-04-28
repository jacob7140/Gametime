package com.example.gametime;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CollectionInboxFragment extends Fragment {

    public CollectionInboxFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ViewPager2 viewPager2;
    TabLayoutMediator tabLayoutMediator;
    InboxCollectionAdapter inboxCollectionAdapter;
    ImageView imageViewBackButtonHome;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox_collection, container, false);
        inboxCollectionAdapter = new InboxCollectionAdapter(this);
        viewPager2 = view.findViewById(R.id.pager);
        viewPager2.setAdapter(inboxCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        String tabNames[] = {
                "Notifications",
                "Messages"
        };
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(tabNames[position])
        ).attach();


        imageViewBackButtonHome = view.findViewById(R.id.imageViewBackButtonToHome);
        imageViewBackButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoHome();
            }
        });

        return view;
    }

    CollectionInboxListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CollectionInboxListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener");
        }
    }

    interface CollectionInboxListener{
        void gotoHome();
    }
}



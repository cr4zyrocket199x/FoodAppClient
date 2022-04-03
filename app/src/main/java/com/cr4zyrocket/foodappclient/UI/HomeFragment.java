package com.cr4zyrocket.foodappclient.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cr4zyrocket.foodappclient.Common.Common;
import com.cr4zyrocket.foodappclient.Model.Banner;
import com.cr4zyrocket.foodappclient.Model.Category;
import com.cr4zyrocket.foodappclient.R;
import com.cr4zyrocket.foodappclient.ViewHolder.MenuViewHolder;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class HomeFragment extends Fragment {
    LayoutAnimationController controller;
    RecyclerView recyclerMenu;
    View view;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    SliderLayout sliderLayout;
    HashMap<String,String> image_list;
    FirebaseDatabase database;
    DatabaseReference category;
    SwipeRefreshLayout srlHome;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_home,container,false);
        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category").child(Common.currentLanguage);
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder holder, int position, @NonNull Category model) {
                holder.txtMenuName.setText(model.getName());
                Picasso.with(getContext()).load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.imageView.setImageResource(R.drawable.image_notfound);
                    }
                });
                holder.setItemClickListener((view, position1, isLongClick) -> {
                    //Get CategoryID and send to new Activity
                    Intent foodListIntent=new Intent(getActivity(), FoodListActivity.class);
                    foodListIntent.putExtra("CategoryID",adapter.getRef(position1).getKey());
                    startActivity(foodListIntent);
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };

        //View
        srlHome=view.findViewById(R.id.srlHome);
        srlHome.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        //Load menu
        recyclerMenu=view.findViewById(R.id.recycler_menu);
        controller= AnimationUtils.loadLayoutAnimation(recyclerMenu.getContext(),R.anim.layout_fall_down);
        recyclerMenu.setLayoutAnimation(controller);
        recyclerMenu.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerMenu.startAnimation(controller.getAnimation());
        srlHome.setOnRefreshListener(this::loadMenu);
        loadMenu();
        setUpSlider();
        return view;
    }

    private void setUpSlider() {
        sliderLayout=view.findViewById(R.id.sliderHome);
        image_list=new HashMap<>();
        final DatabaseReference banners=database.getReference("Banner").child(Common.currentLanguage);
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Banner banner=dataSnapshot.getValue(Banner.class);
                    assert banner != null;
                    image_list.put(banner.getName()+"_"+banner.getId(),banner.getImage());
                }
                for (String key:image_list.keySet()){
                    String[] keySplit=key.split("_");
                    String foodName=keySplit[0];
                    final String foodID=keySplit[1];
                    //Create slider
                    final TextSliderView textSliderView=new TextSliderView(getContext());
                    textSliderView
                            .description(foodName)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(slider -> {
                                Intent intent=new Intent(getActivity(), FoodDetailActivity.class);
                                intent.putExtras(textSliderView.getBundle());
                                startActivity(intent);
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodID",foodID);
                    sliderLayout.addSlider(textSliderView);

                    //Remove event after finish
                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }
    private void loadMenu(){

        adapter.startListening();
        recyclerMenu.setAdapter(adapter);
        srlHome.setRefreshing(false);
        recyclerMenu.scheduleLayoutAnimation();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
//        FirebaseAuth.getInstance().signOut();
    }
}
package com.project.uneed.adapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.uneed.R;
import com.project.uneed.activities.MainActivity;
import com.project.uneed.fragments.AnnouncesFragment;
import com.project.uneed.fragments.ConfigFragment;
import com.project.uneed.fragments.CreateAnnouncesFragment;
import com.project.uneed.fragments.CreateProposalFragment;
import com.project.uneed.fragments.MyAnnouncesFragment;
import com.project.uneed.fragments.MyProposalsFragment;
import com.project.uneed.fragments.ProfileFragment;
import com.project.uneed.model.User;

import java.net.URL;

/**
 * Created by wellington.fucks on 27/07/15.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private Context context;

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private int lastPosition = -1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private User currentUser;

    private int mSelectedItem = 1;

    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;
        FrameLayout container;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            itemView.setClickable(true);

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                container = (FrameLayout) itemView.findViewById(R.id.drawer_row_container);
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {
                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }


    }

    /**
     * @param Titles
     * @param Icons
     * @param user
     * @param context
     */
    public DrawerAdapter(String Titles[], int Icons[], User user, Context context) { // DrawerAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        this.context = context;
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        currentUser = user;
    }

    /**
     * @param mSelectedItem
     */
    public void setSelectedItem(int mSelectedItem) {
        this.mSelectedItem = mSelectedItem;
    }

    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position - 1]);// Settimg the image with array of our icons

            if (position == mSelectedItem) {
                holder.textView.setTextColor(context.getResources().getColor(R.color.drawerSelection));
                holder.textView.setTypeface(Typeface.DEFAULT_BOLD);
                holder.imageView.getDrawable().clearColorFilter();
                holder.imageView.getDrawable().setColorFilter(new LightingColorFilter(context.getResources().getColor(R.color.drawerSelection), context.getResources().getColor(R.color.drawerSelection)));
            } else {
                holder.textView.setTypeface(Typeface.DEFAULT);
                holder.textView.setTextColor(context.getResources().getColor(R.color.drawerTextItem));
                holder.imageView.getDrawable().clearColorFilter();
            }

        } else {
            try {
                holder.profile.setImageBitmap(currentUser.getPhoto());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.Name.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            holder.email.setText("");
        }

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}

package com.project.uneed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.uneed.R;
import com.project.uneed.adapter.DrawerAdapter;
import com.project.uneed.model.Announces;
import com.project.uneed.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AnnouncesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Announces> mDataset;

    private OnFragmentInteractionListener mListener;

    int[] resources = {R.drawable.ic_beauty, R.drawable.ic_cleaning, R.drawable.ic_food, R.drawable.ic_kids, R.drawable.ic_cleaning};

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AnnouncesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param quantity
     */
    public void addItens(int quantity) {
        for (int i = 0; i < quantity; i++) {
            mDataset.add(new Announces("Lorem ipsum dolor siamet", SessionUtil.currentUser.getFirstName(),
                    BitmapFactory.decodeResource(getResources(), resources[i]),
                    "Lorem ipsum dolor siamet lorem ipsum ipsum"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SessionUtil.printLog("Initializing Announces listage fragment");

        View view = inflater.inflate(R.layout.fragment_announces_list, container, false);
        mDataset = new ArrayList<>();

        addItens(4);

        SessionUtil.printLog("Announces created!");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AnnouncesAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Toast.makeText(getActivity(),"Position : "+recyclerView.getChildLayoutPosition(child), Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                swipeView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        addItens(4);
                        mAdapter.notifyDataSetChanged();
                    }
                }, 3000);
            }
        });

        return view;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public class AnnouncesAdapter extends RecyclerView.Adapter<AnnouncesAdapter.ViewHolder> {
        private List<Announces> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {

            // each data item is just a string in this case
            TextView mAnnounceTitle;
            TextView mAnnounceDesc;
            CircleImageView mAnnounceImage;

            FrameLayout mContent;

            public ViewHolder(View itemView) {
                super(itemView);

                mAnnounceTitle = (TextView) itemView.findViewById(R.id.announce_title);
                mAnnounceDesc = (TextView) itemView.findViewById(R.id.announce_desc);
                mAnnounceImage = (CircleImageView) itemView.findViewById(R.id.announce_image_category);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public AnnouncesAdapter(List<Announces> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public AnnouncesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.announces_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.mAnnounceImage.setImageBitmap(mDataset.get(position).getAnnouncesImage());
            holder.mAnnounceTitle.setText(mDataset.get(position).getTitle());
            holder.mAnnounceDesc.setText(mDataset.get(position).getAnnouncessDescription());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}

package com.example.colorharmony;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


public class FavoriteColorsFragment extends Fragment {
    private SQLiteHelper db=null;
    private Cursor current = null;
    private MyCursorAdapter adapter;
    private AsyncTask task = null;
    private Paint p = new Paint();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public ArrayList<FavoriteColor> loadedFavColors;
    private SQLiteDatabase myDatabase;
    private final Object syncLock = new Object();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.from(container.getContext()).inflate(R.layout.favorite_colors_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        myDatabase = SQLiteHelper.getInstance(getActivity()).getWritableDatabase();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((int) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        //test maybe delete later

        //enableSwipe();
        return rootView;
    }

    //TODO: change that the cursor is loaded into an Array and then the array is used for the Recycler, so
    //TODO: there are only 2 big SQL Operations, one opening(loading all items into array) and one closing
    //TODO: (remove all the items from the table that were removed

    @Override
    public void onStart() {
        super.onStart();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        EventBus.getDefault().register(this);
        db = SQLiteHelper.getInstance(getActivity());
        db.loadFavorites();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();



                if (direction == ItemTouchHelper.LEFT) {
                    final int deletedPosition = position;

                    removeItem((int) viewHolder.itemView.getTag());

                    // showing snack bar with Undo option
                    //Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), " removed from Recyclerview!", Snackbar.LENGTH_LONG);
                   /* Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.LinearLayout1), " removed from Recyclerview!", Snackbar.LENGTH_LONG);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                    params.setMargins(0, 0, 0, new MyContextWrapper(getActivity()).getStatusBarHeight());
                    snackbar.getView().setLayoutParams(params);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            adapter.restoreItem(position,loadedFavColors.get(position));
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();*/
                } else {
                    final int deletedPosition = position;
                    adapter.removeItem(position, loadedFavColors.get(position).getId());
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.LinearLayout1), " removed from Recyclerview!", Snackbar.LENGTH_LONG);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                    params.setMargins(0, 0, 0, new MyContextWrapper(getActivity()).getStatusBarHeight());
                    snackbar.getView().setLayoutParams(params);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // undo is selected, restore the deleted item
                            adapter.restoreItem(position, loadedFavColors.get(position));
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);

                    snackbar.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = getBitmapFromVectorDrawable(getActivity(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = getBitmapFromVectorDrawable(getActivity(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDataUpdated(UpdatedEvent event){
        Log.d("CURSOR", " got updated!!!");
        adapter.swapCursor(event.getUpdatedCursor());



    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoritesLoaded(FavoritesLoadedEvent event){
            current = event.getCursor();
            adapter = new MyCursorAdapter(getActivity(), current);
            FavoriteColor myColor;
            recyclerView.swapAdapter(adapter, false);
            String tDescription = "not given";
            Log.d("onFavoritesLoaded", " got called");
            loadedFavColors = new ArrayList<>();


        //load all items into the Array
            current.moveToFirst();
            while(current.moveToNext()) {
                int tId = current.getInt(0);
                String tTitle = current.getString(current.getColumnIndex(SQLiteHelper.TITLE));
                try {
                    tDescription = current.getString(current.getColumnIndex(SQLiteHelper.DESCRIPTION));
                }
               catch(Exception e){

                    }
                String tType = current.getString(current.getColumnIndex(SQLiteHelper.HARMONY_TYPE));
                String tHex1 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_1));
                String tHex2 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_2));
                String tHex3 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_3));
                String tHex4 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_4));

                switch(tType){
                    case ("Complementary"):
                        myColor = new FavoriteColor(tId, tTitle, tType, tDescription, 0, tHex1, tHex2);
                        break;
                    case ("Monochromatic"):
                        myColor = new FavoriteColor(tId, tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3, tHex4);
                        break;
                    case ("Analogous"):
                        myColor = new FavoriteColor(tId,tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3);
                        break;
                    case ("Split Complementary"):
                        myColor = new FavoriteColor(tId, tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3);
                        break;
                    case ("Triadic"):
                        myColor = new FavoriteColor(tId,tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3);
                        break;
                    case ("Tetradic"):
                        myColor = new FavoriteColor(tId,tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3, tHex4);
                        break;
                    default:
                        myColor = null;
                }
                loadedFavColors.add(myColor);
                Log.d("Favorite colors", "added new item:" + myColor.mtitle);

            }
    }

    private void removeItem(int id){
        SQLiteHelper.getInstance(getContext()).removeFavorite(id);
        adapter.swapCursor(getAllItems());
        notifier();
    }

    synchronized void notifier() {

        synchronized (adapter) {
            adapter.notifyAll();
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Cursor getAllItems() {
        return myDatabase.query(
                SQLiteHelper.TABLE,
                null,
                null,
                null,
                null,
                null,
                "_id" + " DESC"
        );
    }
}


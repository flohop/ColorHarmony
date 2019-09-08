package com.example.colorharmony;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class FavoriteColorsFragment extends Fragment {
    private SQLiteHelper db = null;
    private Cursor current = null;
    private MyCursorAdapter adapter;
    private AsyncTask task = null;
    private Paint p = new Paint();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public ArrayList<FavoriteColor> loadedFavColors;
    private SQLiteDatabase myDatabase;
    private final Object syncLock = new Object();
    public String currentColorTextFormat;
    private static final String PREF_CHOOSE_FORMATNG = "list";
    public FavoritesActivity activity;

    private AdView bannerAd;



    public static interface ClickListener {
         void onClick(View view, int position);

         void onLongClick(View view, int position);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentColorTextFormat = prefs.getString(PREF_CHOOSE_FORMATNG, "Hex");
        setRetainInstance(true);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.from(container.getContext()).inflate(R.layout.favorite_colors_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        bannerAd = (AdView) rootView.findViewById(R.id.fragment_banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (loadedFavColors.get(position).getHarmonyType()) {
                    case "Complementary":
                        openFavoriteComplementaryDialog(loadedFavColors.get(position), position);
                        break;
                    case "Split Complementary":
                    case "Analogous":
                    case "Triadic":
                        openFavoriteThreeColorsDialog(loadedFavColors.get(position), position);
                        break;
                    case "Tetradic":
                    case "Monochromatic":
                        openFavoriteFourColorsDialog(loadedFavColors.get(position), position);


                }

            }


            @Override
            public void onLongClick(View view, int position) {
                try {
                    Toast.makeText(getContext(), "Clicked long on: " + loadedFavColors.get(position).getTitle(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("Error", "onLongClick:  " + e.getStackTrace());
                }
            }
        }));
        myDatabase = SQLiteHelper.getInstance(getActivity()).getWritableDatabase();

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        enableSwipe();
        return rootView;

        //TODO: Pressing undo, gives back the wrong color, find out why and fix it!
    }


    @Override
    public void onStart() {
        super.onStart();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        EventBus.getDefault().register(this);
        db = SQLiteHelper.getInstance(getActivity());
        db.loadFavorites();
        if(db != null) {
            db.close();
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        Log.d("STOPPED", " Fragment got stopped");
        if (current != null) {
            current.close();
        }
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

                    removeItem(position, (int) viewHolder.itemView.getTag());

                    //disabled as long as i cant figure out why it triggers weird behavior in the app

                    /*
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.LinearLayout1), " removed from Recyclerview!", Snackbar.LENGTH_LONG);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                    params.setMargins(0, 0, 0, new MyContextWrapper(getActivity()).getStatusBarHeight());
                    snackbar.getView().setLayoutParams(params);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            Log.d("Size measure:", "Array size: " + loadedFavColors.size() + "| Position:" + position);

                            switch (loadedFavColors.get(position).getHarmonyType()) {
                                case "Complementary":
                                    SQLiteHelper.getInstance(getActivity())
                                            .updateFavorites(loadedFavColors.get(position - 1).getTitle(), loadedFavColors.get(position - 1).getDescription(),
                                                    loadedFavColors.get(position - 1).getHarmonyType(), loadedFavColors.get(position - 1).getHex1(),
                                                    loadedFavColors.get(position - 1).getHex2());
                                    break;
                                case "Analogous":
                                case "Split Complementary":
                                case "Triadic":
                                    SQLiteHelper.getInstance(getActivity())
                                            .updateFavorites(loadedFavColors.get(position - 1).getTitle(), loadedFavColors.get(position - 1).getDescription(),
                                                    loadedFavColors.get(position - 1).getHarmonyType(), loadedFavColors.get(position - 1).getHex1(),
                                                    loadedFavColors.get(position - 1).getHex2(), loadedFavColors.get(position - 1).getHex3());
                                    break;
                                case "Monochromatic":
                                case "Tetradic":
                                    SQLiteHelper.getInstance(getActivity())
                                            .updateFavorites(loadedFavColors.get(position - 1).getTitle(), loadedFavColors.get(position - 1).getDescription(),
                                                    loadedFavColors.get(position - 1).getHarmonyType(), loadedFavColors.get(position - 1).getHex1(),
                                                    loadedFavColors.get(position - 1).getHex2(), loadedFavColors.get(position - 1).getHex3(), loadedFavColors.get(position - 1).getHex4());
                            }

                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();*/
                } else {
                    removeItem(position, (int) viewHolder.itemView.getTag());


                    // showing snack bar with Undo option(disabled because it triggers weird things in the list
                    /*Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.LinearLayout1), " removed from Recyclerview!", Snackbar.LENGTH_LONG);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                    params.setMargins(0, 0, 0, new MyContextWrapper(getActivity()).getStatusBarHeight());
                    snackbar.getView().setLayoutParams(params);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Size measure:", "Array size: " + loadedFavColors.size() + "| Position:" + position);
                            // undo is selected, restore the deleted item
                            switch (loadedFavColors.get(position - 1).getHarmonyType()) {
                                case "Complementary":
                                    SQLiteHelper.getInstance(getActivity())
                                            .updateFavorites(loadedFavColors.get(position - 1).getTitle(), loadedFavColors.get(position - 1).getDescription(),
                                                    loadedFavColors.get(position - 1).getHarmonyType(), loadedFavColors.get(position - 1).getHex1(),
                                                    loadedFavColors.get(position - 1).getHex2());
                                    break;
                                case "Analogous":
                                case "Split Complementary":
                                case "Triadic":
                                    SQLiteHelper.getInstance(getActivity())
                                            .updateFavorites(loadedFavColors.get(position - 1).getTitle(), loadedFavColors.get(position - 1).getDescription(),
                                                    loadedFavColors.get(position - 1).getHarmonyType(), loadedFavColors.get(position - 1).getHex1(),
                                                    loadedFavColors.get(position - 1).getHex2(), loadedFavColors.get(position - 1).getHex3());
                                    break;
                                case "Monochromatic":
                                case "Tetradic":
                                    SQLiteHelper.getInstance(getActivity())
                                            .updateFavorites(loadedFavColors.get(position - 1).getTitle(), loadedFavColors.get(position - 1).getDescription(),
                                                    loadedFavColors.get(position - 1).getHarmonyType(), loadedFavColors.get(position - 1).getHex1(),
                                                    loadedFavColors.get(position - 1).getHex2(), loadedFavColors.get(position - 1).getHex3(), loadedFavColors.get(position - 1).getHex4());
                            }
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);

                    snackbar.show();*/
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataUpdated(UpdatedEvent event) {
        Log.d("CURSOR", " got updated!!!");

        current.close();
        adapter.swapCursor(event.getUpdatedCursor());

        notifier();

    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onRowChanged(FavoritesUpdatedEvent event){
        Log.d(TAG, "onRowChanged: was called from the EventBus");
        current = event.getCursor();
        adapter.swapCursor(current);


    }


    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoritesLoaded(FavoritesLoadedEvent event) {
        current = event.getCursor();
        adapter = new MyCursorAdapter(getActivity(), current);
        FavoriteColor myColor;
        recyclerView.swapAdapter(adapter, false);
        String tDescription = "not given";
        Log.d("onFavoritesLoaded", " got called");
        loadedFavColors = new ArrayList<>();


        //load all items into the Array
        for (current.moveToFirst(); !current.isAfterLast(); current.moveToNext()) {
            int tId = current.getInt(0);
            String tTitle = current.getString(current.getColumnIndex(SQLiteHelper.TITLE));
            try {
                tDescription = current.getString(current.getColumnIndex(SQLiteHelper.DESCRIPTION));
            } catch (Exception e) {

            }
            String tType = current.getString(current.getColumnIndex(SQLiteHelper.HARMONY_TYPE));
            String tHex1 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_1));
            String tHex2 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_2));
            String tHex3 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_3));
            String tHex4 = current.getString(current.getColumnIndex(SQLiteHelper.COLOR_VALUE_4));

            switch (tType) {
                case ("Complementary"):
                    myColor = new FavoriteColor(tId, tTitle, tType, tDescription, 0, tHex1, tHex2);
                    break;
                case ("Monochromatic"):
                case ("Tetradic"):
                    myColor = new FavoriteColor(tId, tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3, tHex4);
                    break;
                case ("Analogous"):
                case ("Split Complementary"):
                case ("Triadic"):
                    myColor = new FavoriteColor(tId, tTitle, tType, tDescription, 0, tHex1, tHex2, tHex3);
                    break;
                default:
                    myColor = null;
            }
            loadedFavColors.add(myColor);
            Log.d("Favorite colors", "added new item:" + myColor.toString());

        }
    }

    private void removeItem(int position, int id) {
        adapter.removeItem(position, id);

    }

    synchronized void notifier() {

        synchronized (adapter) {
            adapter.notifyAll();
            adapter.notifyDataSetChanged();
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
        try {
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (NullPointerException e) {
            Log.d("FavoriteColorFragment", "getBitmapFromVectorDrawable error: " + e.getStackTrace());
        }

        return bitmap;
    }

    private Cursor getAllItems() {

        Cursor newCursor = myDatabase.query(
                SQLiteHelper.TABLE,
                null,
                null,
                null,
                null,
                null,
                "_id" + " DESC");

        return newCursor;

    }

    //implementation of RecyclerView.OnItemTouchListener
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    private void openFavoriteComplementaryDialog(final FavoriteColor theColor, final int position) {

        AlertDialog favoriteDialog = new AlertDialog.Builder(getActivity()).create();
        final android.content.ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        LayoutInflater factory = LayoutInflater.from(getActivity());

        final EditText favorite_header;
        View cool_color_1;
        View cool_color_2;
        TextView cool_color_text_1;
        TextView cool_color_text_2;
        final EditText favorite_description;
        ImageButton harmonyType;
        final ImageButton shareButton2;
        final TextView banner;
        final ImageView banner_icon;


        final View favoriteView = factory.inflate(R.layout.favoirte_color_dialog_2_colors, null);

        favorite_header = favoriteView.findViewById(R.id.title_top_text);
        banner = favoriteView.findViewById(R.id.banner);
        banner_icon = favoriteView.findViewById(R.id.banner_icon);

        cool_color_1 = favoriteView.findViewById(R.id.cool_color_2);
        cool_color_2 = favoriteView.findViewById(R.id.cool_color_1);
        cool_color_text_1 = favoriteView.findViewById(R.id.colorViewValue);
        cool_color_text_2 = favoriteView.findViewById(R.id.colorViewValue2);
        favorite_description = favoriteView.findViewById(R.id.favorite_description);
        harmonyType = favoriteView.findViewById(R.id.imageButton); // maybe not button
        shareButton2 = favoriteView.findViewById(R.id.imageButton2);

        //Bind OnTouchListeners

        shareButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton2.setVisibility(View.INVISIBLE);
                banner.setVisibility(View.VISIBLE);
                banner_icon.setVisibility(View.VISIBLE);

                sendBitmapToWhatsApp("Created with Color Harmony", getBitmapFromView(favoriteView));
                shareButton2.setVisibility(View.VISIBLE);
                banner.setVisibility(View.INVISIBLE);
                banner_icon.setVisibility(View.INVISIBLE);
            }
        });

        cool_color_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex1()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        cool_color_2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex2()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //set the content
        favorite_header.setText(theColor.getTitle());
        cool_color_1.setBackgroundColor(Color.parseColor("#" + theColor.getHex1()));
        cool_color_text_1.setText(getCorrectColorFormat(theColor.getHex1()));
        cool_color_2.setBackgroundColor(Color.parseColor("#" + theColor.getHex2()));
        cool_color_text_2.setText(getCorrectColorFormat(theColor.getHex2()));
        favorite_description.setText(theColor.getDescription(), TextView.BufferType.EDITABLE);
        harmonyType.setImageResource(R.drawable.ic_complementary_harmony);

        favoriteDialog.setView(favoriteView);


        favoriteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SQLiteHelper.getInstance(getActivity()).updateFavoritesValues(favorite_header.getText().toString(),
                        favorite_description.getText().toString(), theColor.getId(), position);
                theColor.setMtitle(favorite_header.getText().toString());
                theColor.setMdescription(favorite_description.getText().toString());

                loadedFavColors.set(position, theColor);


                Log.d("Updated", "updated values with SQL");

            }
        });


        new Dialog(getActivity().getApplicationContext());
        favoriteDialog.show();

        // Set Properties for OK Button
        final Button okBT = favoriteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        okBT.setTextColor(ContextCompat.getColor(getActivity(),R.color.Coral));
        okBT.setLayoutParams(neutralBtnLP);

    }

    private void openFavoriteThreeColorsDialog(final FavoriteColor theColor, final int position) {
        AlertDialog favoriteDialog = new AlertDialog.Builder(getActivity()).create();
        final android.content.ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        LayoutInflater factory = LayoutInflater.from(getActivity());

        final EditText favorite_header;
        View cool2_color_1;
        View cool2_color_2;
        View cool2_color_3;
        TextView cool_color_text_1;
        TextView cool_color_text_2;
        TextView cool_color_text_3;
        final EditText favorite_description;
        ImageButton harmonyType;
        final ImageButton shareButton3;
        final TextView banner;
        final ImageView banner_icon;


        final View favoriteView = factory.inflate(R.layout.favoirte_color_dialog_3, null);

        favorite_header = favoriteView.findViewById(R.id.title_top_text_3colors);
        banner = favoriteView.findViewById(R.id.banner_3colors);
        banner_icon = favoriteView.findViewById(R.id.banner_icon_3colors);

        cool2_color_1 = favoriteView.findViewById(R.id.cool2_color_1);
        cool2_color_2 = favoriteView.findViewById(R.id.cool2_color_2);
        cool2_color_3 = favoriteView.findViewById(R.id.cool2_color_3);
        cool_color_text_1 = favoriteView.findViewById(R.id.colorViewValue_3colors1);
        cool_color_text_2 = favoriteView.findViewById(R.id.colorViewValue_3colors2);
        cool_color_text_3 = favoriteView.findViewById(R.id.colorViewValue_3colors3);
        favorite_description = favoriteView.findViewById(R.id.favorite_description_3colors);
        harmonyType = favoriteView.findViewById(R.id.imageButton_3colors); // maybe not button
        shareButton3 = favoriteView.findViewById(R.id.imageButton2_3colors);

        //Bind OnTouchListeners

        shareButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton3.setVisibility(View.INVISIBLE);
                banner.setVisibility(View.VISIBLE);
                banner_icon.setVisibility(View.VISIBLE);

                sendBitmapToWhatsApp("Created with Color Harmony", getBitmapFromView(favoriteView));
                shareButton3.setVisibility(View.VISIBLE);
                banner.setVisibility(View.INVISIBLE);
                banner_icon.setVisibility(View.INVISIBLE);
            }
        });

        cool2_color_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex1()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        cool2_color_2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex2()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        cool2_color_3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex2()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //set the content
        favorite_header.setText(theColor.getTitle());
        cool2_color_1.setBackgroundColor(Color.parseColor("#" + theColor.getHex1()));
        cool_color_text_1.setText(getCorrectColorFormat(theColor.getHex1()));
        cool2_color_2.setBackgroundColor(Color.parseColor("#" + theColor.getHex2()));
        cool_color_text_2.setText(getCorrectColorFormat(theColor.getHex2()));
        cool2_color_3.setBackgroundColor(Color.parseColor("#" + theColor.getHex3()));
        cool_color_text_3.setText(getCorrectColorFormat(theColor.getHex3()));

        favorite_description.setText(theColor.getDescription(), TextView.BufferType.EDITABLE);
        harmonyType.setImageResource(theColor.getHarmonyTypeResource());

        favoriteDialog.setView(favoriteView);


        favoriteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SQLiteHelper.getInstance(getActivity()).updateFavoritesValues(favorite_header.getText().toString(), favorite_description.getText().toString(), theColor.getId(), position);
                theColor.setMtitle(favorite_header.getText().toString());
                theColor.setMdescription(favorite_description.getText().toString());

                loadedFavColors.set(position, theColor);

                Log.d("Updated", "updated values with SQL");


            }
        });


        new Dialog(getActivity().getApplicationContext());
        favoriteDialog.show();

        // Set Properties for OK Button
        final Button okBT = favoriteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        okBT.setTextColor(ContextCompat.getColor(getActivity(), R.color.Coral));
        okBT.setLayoutParams(neutralBtnLP);

    }


    private void openFavoriteFourColorsDialog(final FavoriteColor theColor, final int position){
        AlertDialog favoriteDialog = new AlertDialog.Builder(getActivity()).create();
        final android.content.ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        LayoutInflater factory = LayoutInflater.from(getActivity());

        final EditText favorite_header;
        View cool3_color_1;
        View cool3_color_2;
        View cool3_color_3;
        View cool3_color_4;
        TextView cool_color_text_1;
        TextView cool_color_text_2;
        TextView cool_color_text_3;
        TextView cool_color_text_4;
        final EditText favorite_description;
        ImageButton harmonyType;
        final ImageButton shareButton4;
        final TextView banner;
        final ImageView banner_icon;


        final View favoriteView = factory.inflate(R.layout.favorite_color_dialog_4, null);

        favorite_header = favoriteView.findViewById(R.id.title_top_text_4colors);
        banner = favoriteView.findViewById(R.id.banner_4colors);
        banner_icon = favoriteView.findViewById(R.id.banner_icon_4colors);

        cool3_color_1 = favoriteView.findViewById(R.id.cool4_color_1);
        cool3_color_2 = favoriteView.findViewById(R.id.cool4_color_2);
        cool3_color_3 = favoriteView.findViewById(R.id.cool4_color_3);
        cool3_color_4 = favoriteView.findViewById(R.id.cool4_color_4);
        cool_color_text_1 = favoriteView.findViewById(R.id.colorViewValue_4colors1);
        cool_color_text_2 = favoriteView.findViewById(R.id.colorViewValue_4colors2);
        cool_color_text_3 = favoriteView.findViewById(R.id.colorViewValue_4colors3);
        cool_color_text_4 = favoriteView.findViewById(R.id.colorViewValue_4colors4);
        favorite_description = favoriteView.findViewById(R.id.favorite_description_4colors);
        harmonyType = favoriteView.findViewById(R.id.imageButton_4colors); // maybe not button
        shareButton4 = favoriteView.findViewById(R.id.imageButton2_4colors);

        //Bind OnTouchListeners

        shareButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton4.setVisibility(View.INVISIBLE);
                banner.setVisibility(View.VISIBLE);
                banner_icon.setVisibility(View.VISIBLE);

                sendBitmapToWhatsApp("Created with Color Harmony", getBitmapFromView(favoriteView));
                shareButton4.setVisibility(View.VISIBLE);
                banner.setVisibility(View.INVISIBLE);
                banner_icon.setVisibility(View.INVISIBLE);
            }
        });

        cool3_color_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex1()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        cool3_color_2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex2()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        cool3_color_3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex2()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        cool3_color_4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData colorData = ClipData.newPlainText("Color value", getCorrectColorFormat(theColor.getHex2()));
                clipboardManager.setPrimaryClip(colorData);
                Toast.makeText(getActivity(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //set the content
        favorite_header.setText(theColor.getTitle());
        cool3_color_1.setBackgroundColor(Color.parseColor("#" + theColor.getHex1()));
        cool_color_text_1.setText(getCorrectColorFormat(theColor.getHex1()));
        cool3_color_2.setBackgroundColor(Color.parseColor("#" + theColor.getHex2()));
        cool_color_text_2.setText(getCorrectColorFormat(theColor.getHex2()));
        cool3_color_3.setBackgroundColor(Color.parseColor("#" + theColor.getHex3()));
        cool_color_text_3.setText(getCorrectColorFormat(theColor.getHex3()));
        cool3_color_4.setBackgroundColor(Color.parseColor("#" + theColor.getHex4()));
        cool_color_text_4.setText(getCorrectColorFormat(theColor.getHex4()));

        favorite_description.setText(theColor.getDescription(), TextView.BufferType.EDITABLE);
        harmonyType.setImageResource(theColor.getHarmonyTypeResource());

        favoriteDialog.setView(favoriteView);


        favoriteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SQLiteHelper.getInstance(getActivity()).updateFavoritesValues(favorite_header.getText().toString(),
                        favorite_description.getText().toString(), theColor.getId(), position);
                theColor.setMtitle(favorite_header.getText().toString());
                theColor.setMdescription(favorite_description.getText().toString());

                loadedFavColors.set(position, theColor);

                Log.d("Updated", "updated values with SQL");

            }
        });


        new Dialog(getActivity().getApplicationContext());
        favoriteDialog.show();

        // Set Properties for OK Button
        final Button okBT = favoriteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        okBT.setTextColor(ContextCompat.getColor(getActivity(),R.color.Coral));
        okBT.setLayoutParams(neutralBtnLP);

    }

    private String getCorrectColorFormat(String originalHex){

        switch(currentColorTextFormat){
            case "Hex":
                return "#" + originalHex;
            case "RGB":
                return CalculateHarmonyCalculator.RGBFromHex(originalHex);
            case "HSV":
                return CalculateHarmonyCalculator.HSVFromHex(originalHex);
            case "CMYK":
                return CalculateHarmonyCalculator.CMYKFromHex(originalHex);
            default:
                return null;
        }
    }


    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public void sendBitmapToWhatsApp(String pack, Bitmap bitmap) {
        Log.d(TAG, "sendBitmapToWhatsApp: got called");
        PackageManager pm = getActivity().getPackageManager();
        try {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
             != PackageManager.PERMISSION_GRANTED){
                //permission not granted
                if(Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
            else {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
                Uri imageUri = Uri.parse(path);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                sendIntent.putExtra(Intent.EXTRA_TEXT, pack);
                sendIntent.setType("image/*");
                startActivity(Intent.createChooser(sendIntent, "Select app"));
            }
        } catch (Exception e) {
            Log.e("Error on sharing", e + " ");
            Toast.makeText(getActivity(), "App not Installed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 3:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
                else{
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}


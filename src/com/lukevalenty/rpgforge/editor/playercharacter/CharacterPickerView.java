package com.lukevalenty.rpgforge.editor.playercharacter;

import java.util.ArrayList;

import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.CharacterSetData;
import com.lukevalenty.rpgforge.data.NpcEventData;
import com.lukevalenty.rpgforge.editor.map.DrawTileEvent;
import com.lukevalenty.rpgforge.editor.map.MapEditActivity;
import com.lukevalenty.rpgforge.engine.Direction;
import com.lukevalenty.rpgforge.engine.NumberRef;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class CharacterPickerView extends View {
    public interface CharacterSelectedListener {
        public void onCharacterSelected(final CharacterData characterData);
    }
    
    public CharacterPickerView(
        final Context context, 
        final AttributeSet attrs, 
        final int defStyle
    ) {
        super(context, attrs, defStyle);
        init();
    }
    
    public CharacterPickerView(
        final Context context, 
        final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }
    
    public CharacterPickerView(
        final Context context
    ) {
        super(context);
        init();
    }
    
    public void init() {
        paint.setFlags(paint.getFlags() & ~Paint.FILTER_BITMAP_FLAG);
        
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCharacterSelectionDialog();
            }
        });
    }
    
    private final Rect src = new Rect();
    private final Rect dst = new Rect();
    private final Paint paint = new Paint();
    private CharacterSelectedListener characterSelectedListener;
    private int currentSelectedChar = 0;
    
    public void setCharacterSelectedListener(final CharacterSelectedListener listener) {
        this.characterSelectedListener = listener;
    }
    
    @Override
    public void onDraw(
        final Canvas c
    ) {
        final CharacterData charData =
            getCharacterData(getCurrentSelectedChar());
        
        if (charData != null) {
            src.top = charData.src().top;
            src.left = charData.src().left + 32;
            src.bottom = charData.src().top + 48;
            src.right = charData.src().right - 32;
            
            dst.top = 0;
            dst.left = 0;
            dst.bottom = c.getHeight();
            dst.right = c.getWidth();
            
            c.drawBitmap(charData.bitmap(), src, dst, paint);
        }
    }
    
    private int getCharacterDataSize() {
        int size = 0;
        
        if (RpgForgeApplication.getDb() != null) {
            for (final CharacterSetData charSetData : RpgForgeApplication.getDb().getCharacterSets()) {
                size += charSetData.getCharacters().size();
            }
        }
        
        return size;
    }
    
    private CharacterData getCharacterData(int i) {
        if (RpgForgeApplication.getDb() != null) {
            for (final CharacterSetData charSetData : RpgForgeApplication.getDb().getCharacterSets()) {
                if (i < charSetData.getCharacters().size()) {
                    return charSetData.getCharacters().get(i);
                    
                } else {
                    i = i - charSetData.getCharacters().size();
                }
            }
        }
        
        return null;
    }
    
    private void showCharacterSelectionDialog() {      
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(getContext());
        
        final GridView charDataList =
            new GridView(getContext());
        
        charDataList.setBackgroundColor(Color.BLACK);
        charDataList.setNumColumns(4);
        charDataList.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        charDataList.setGravity(Gravity.CENTER);
        
        /*
        android:id="@+id/npcSpriteList"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#000000"
        android:gravity="center"
        android:numColumns="4"
        android:stretchMode="spacingWidthUniform" >
        */
        
        final BaseAdapter npcListAdapter = new BaseAdapter() {
            @Override
            public View getView(
                final int position, 
                final View convertView, 
                final ViewGroup parent
            ) {
                final CharacterData charData =
                    getCharacterData(position);

                ImageView tileView;
                
                if (convertView == null) {
                    tileView = 
                        new ImageView(getContext());
                    
                } else {
                    tileView = 
                        (ImageView) convertView;
                }
                
                if (getCurrentSelectedChar() == position) {
                    tileView.setBackgroundColor(Color.WHITE);
                } else {
                    tileView.setBackgroundColor(Color.TRANSPARENT);
                }
                
                tileView.setLayoutParams(new GridView.LayoutParams(dpToPx(64), dpToPx(96)));
                tileView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tileView.setPadding(4, 4, 4, 4);
                
                tileView.setImageDrawable(new Drawable() {   
                    private Rect src = new Rect();
                    
                    {
                        src.left = charData.src().left + 32;
                        src.right = charData.src().right - 32;
                        src.top = charData.src().top;
                        src.bottom = charData.src().top + 48; 
                    }
                    
                    @Override
                    public void setColorFilter(ColorFilter cf) {
                        // do nothing
                    }
                    
                    @Override
                    public void setAlpha(int alpha) {
                        // do nothing
                    }
                    
                    @Override
                    public int getOpacity() {
                        return PixelFormat.TRANSPARENT;
                    }
                    
                    @Override
                    public void draw(final Canvas canvas) {
                        canvas.drawBitmap(charData.bitmap(), src,  getBounds(), null);
                    }
                });
                
                return tileView;
            }
 
            private int dpToPx(final int dp) {
                return (int) (getResources().getDisplayMetrics().density * dp);
            }
            
            @Override
            public long getItemId(int position) {
                return System.identityHashCode(getItem(position));
            }
            
            @Override
            public Object getItem(int position) {
                return getCharacterData(position);
            }
            
            @Override
            public int getCount() {
                return getCharacterDataSize();
            }
        };
        
        charDataList.setAdapter(npcListAdapter);
        
      
        charDataList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(
                final AdapterView<?> parent, 
                final View view,
                final int position, 
                final long id
            ) {
                setCurrentSelectedChar(position);
                npcListAdapter.notifyDataSetChanged();
                CharacterPickerView.this.invalidate();
            }
        });
        
        
        
        builder.setView(charDataList)
            .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int id) {
                    // nothing to do here
                }
            });
        
        final AlertDialog dialog = 
            builder.create();
        
        dialog.show();
        
    }

    public int getCurrentSelectedChar() {
        return currentSelectedChar;
    }

    public void setCurrentSelectedChar(int currentSelectedChar) {
        this.currentSelectedChar = currentSelectedChar;
        
        if (characterSelectedListener != null) {
            characterSelectedListener.onCharacterSelected(getCharacterData(currentSelectedChar));
        }
    }

    public CharacterData getCharacterData() {
        return getCharacterData(getCurrentSelectedChar());
    }

    public void setCurrentSelectedChar(final CharacterData charData) {
        int i = 0;
        
        if (RpgForgeApplication.getDb() != null) {
            for (final CharacterSetData charSetData : RpgForgeApplication.getDb().getCharacterSets()) {
                for (final CharacterData charDataRhs : charSetData.getCharacters()) {
                    if (charData == charDataRhs) {
                        setCurrentSelectedChar(i);
                        return;
                        
                    } else {
                        i++;
                    }
                }
                
            }
        }
    }

}

package com.lukevalenty.rpgforge.editor.map;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.DialogUtil;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.DialogUtil.StringPromptListener;
import com.lukevalenty.rpgforge.data.BuiltinData;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.CharacterSetData;
import com.lukevalenty.rpgforge.data.DoorEventData;
import com.lukevalenty.rpgforge.data.EventData;
import com.lukevalenty.rpgforge.data.NpcEventData;
import com.lukevalenty.rpgforge.data.RpgDatabase;
import com.lukevalenty.rpgforge.data.RpgDatabaseLoader;
import com.lukevalenty.rpgforge.data.TileData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.editor.map.MapView.OnTileClickListener;
import com.lukevalenty.rpgforge.engine.Direction;
import com.lukevalenty.rpgforge.engine.GameActivity;
import com.lukevalenty.rpgforge.engine.NumberRef;
import com.lukevalenty.rpgforge.engine.ObjectRef;

import de.greenrobot.event.EventBus;

import roboguice.inject.InjectView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MapEditActivity extends BaseActivity {
    public static final String TAG = MapEditActivity.class.getName();
    
    @InjectView(R.id.tilePalette) private View tilePalette; 
    @InjectView(R.id.npcSpriteList) private GridView tileList;
    @InjectView(R.id.tileDrawerSpinner) private Spinner tileDrawerSpinner; 
    @InjectView(R.id.mapView) private MapView mapView;
    
    @Inject private EventBus eventBus;

    
    private Tool currentTool = Tool.MOVE;
    private BaseAdapter tilePaletteAdapter;

    private ArrayList<? extends PaletteItem> allTiles;
    private BaseAdapter mapSelectionAdapter;

    private ArrayList<EventData> eventTilePalette;

    private PaletteItem currentPaletteItem;

    


    
    
    public void onEvent(
        final ResizeMapEvent e
    ) {
        currentMap.resize(e.width(), e.height(), RpgForgeApplication.getDb().getDefaultTile());
        mapView.setMap(currentMap);
    }
    

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "MapEditActivity.onNewIntent");
        handleIntent(intent);
    }
    
    private void handleIntent(Intent intent) {
        Log.d(TAG, "MapEditActivity.handleIntent - " + intent);
        
        final int assetIndex = intent.getIntExtra("ASSET_INDEX", 0);
        eventBus.post(new SelectMapEvent(assetIndex, RpgForgeApplication.getDb().getMaps().get(assetIndex)).setMapListChanged());
            
        Log.d(TAG, "HANDLE INTENT FINISHED");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "== ON CREATE ==" + this.hashCode());
        setContentView(R.layout.map_edit_layout);

        eventBus.register(this);
        
        mapView.setDebug(true);
        mapView.start();
        

        updateFromDatabase(); 
        
        mapView.setOnTileClickListener(new OnTileClickListener() {
            @Override
            public boolean onTileClick(
                final int tileX, 
                final int tileY
            ) {
                if (currentTool == Tool.EYEDROP) {
                    eventBus.post(new PaletteItemSelectedEvent(currentMap.getTile(tileX, tileY)));
                
                } else if (currentTool == Tool.DRAW) {     
                    if (currentPaletteItem instanceof EventData) {
                        eventBus.post(new DrawTileEvent(((EventData) currentPaletteItem).create(), tileX, tileY));
                        
                    } else {
                        eventBus.post(new DrawTileEvent(currentPaletteItem, tileX, tileY));
                    }
                    
                    
                } else if (currentTool == Tool.FILL) {

                    if (currentPaletteItem instanceof TileData) {
                        final TileData tile = 
                            (TileData) currentPaletteItem;
                            
                        if (fillInProgress == false) {
                            fillInProgress = true;
                            
                            new Thread(new Runnable() {
                                
                                @Override
                                public void run() {
                                    fill(tile, tileX, tileY);
                                    fillInProgress = false;
                                }
                            }).start();
                        }
                    }
                }

                return true;
            }
        });
        

        
        new TilePalettePresenter(this, eventBus, tilePalette, tileDrawerSpinner, tileList, allTiles, eventTilePalette);
        

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {  
            new MapSelectionPresenter(this, eventBus, getActionBar());
        }      

        handleIntent(getIntent());
        
        Log.d(TAG, "ONCREATE FINISHED");
    }

    private boolean fillInProgress;

    private void fill(
        final TileData replacementTile, 
        final int x, 
        final int y
    ) {
        if (replacementTile.getLayer() == 0) {
            final TileData targetTile = 
                currentMap.getTile(x, y);
            
            if (targetTile != null && !replacementTile.equals(targetTile)) { 
                LinkedHashSet<Point> q = 
                    new LinkedHashSet<Point>();
                
                q.add(new Point(x, y));
                
                while (!q.isEmpty()) {
                    final Iterator<Point> i = q.iterator();
                    
                    final Point n = 
                        i.next();

                    i.remove();
                    
                    final TileData nTile = 
                            currentMap.getTile(n.x, n.y);
                    
                    if (nTile != null && nTile == targetTile) {
                        currentMap.setTile(n.x, n.y, replacementTile);
                        mapView.invalidateMapData(n.x, n.y);
                        addPoint(targetTile, q, new Point(n.x + 1, n.y));
                        addPoint(targetTile, q, new Point(n.x - 1, n.y));
                        addPoint(targetTile, q, new Point(n.x, n.y + 1));
                        addPoint(targetTile, q, new Point(n.x, n.y - 1));
                    }
                }
                
                q = null;
            }
        }
    }


    private void addPoint(
        final TileData targetTile, 
        final LinkedHashSet<Point> q, 
        final Point p
    ) {
        final TileData tile = 
            currentMap.getTile(p.x, p.y);
        
        if (tile != null && tile.equals(targetTile)) {
            q.add(p);
        }
    }
    
    public void onEvent(final PaletteItemSelectedEvent e) {
        currentTool = Tool.DRAW;
        currentPaletteItem = e.tile();
        eventBus.post(new ToolSelectedEvent(currentTool));
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onEvent(final ToolSelectedEvent e) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
        
        if (e.tool() == Tool.MOVE) {
            mapView.freezeMapLocation(false);
            
        } else {
            mapView.freezeMapLocation(true);
        }
    }
    
    private boolean tileDrawInProgress = false;
    
    public void onEvent(final DrawTileEvent e) {
        if (e.tile() instanceof TileData) {
            final TileData tile = 
                (TileData) e.tile();
            
            if (currentMap.setTile(e.x(), e.y(), tile)) {
                mapView.invalidateMapData(e.x(), e.y());
            }
            
        } else if (e.tile() instanceof EventData && !tileDrawInProgress) {
            final EventData event = 
                (EventData) e.tile();
            
            currentMap.setEvent(e.x(), e.y(), event);

            currentTool = Tool.MOVE;
            eventBus.post(new ToolSelectedEvent(currentTool));   
        }
        
        if (e.tile() instanceof DoorEventData) {
            editDoorEvent(e);
        
        } else if (e.tile() instanceof NpcEventData) {
            editNpcEvent(e);
        }
    }

    @SuppressLint("NewApi")
    private void editNpcEvent(final DrawTileEvent e) {
        if (tileDrawInProgress == false) {
            tileDrawInProgress = true;
            
            final NpcEventData npcEvent = 
                (NpcEventData) e.tile();
            
            final AlertDialog.Builder builder = 
                new AlertDialog.Builder(this);
            
            final LayoutInflater inflater = 
                this.getLayoutInflater();
            
            final View view = 
                inflater.inflate(R.layout.npc_event_edit_layout, null);
            
            final GridView npcSpriteList =
                (GridView) view.findViewById(R.id.npcSpriteList);
            
            final EditText npcDialog =
                (EditText) view.findViewById(R.id.npcDialog);
            
            final EditText npcCharacterName =
                (EditText) view.findViewById(R.id.npcCharacterName);
            
            final CheckBox npcStationary =
                (CheckBox) view.findViewById(R.id.npcStationary);
            
            final Spinner npcInitialDirection =
                (Spinner) view.findViewById(R.id.npcInitialDirection);
            
           
            npcDialog.addTextChangedListener(new TextWatcher() {    
                private boolean myChange = false;
                
                @Override
                public void onTextChanged(
                    final CharSequence s, 
                    final int start, 
                    final int before, 
                    final int count
                ) {
                    // do nothing
                }
                
                @Override
                public void beforeTextChanged(
                    final CharSequence s, 
                    final int start, 
                    final int before, 
                    final int count
                ) {
                    // do nothing
                }
                
                @Override
                public void afterTextChanged(
                    final Editable s
                ) {
                    if (myChange == false) {
                        myChange = true;
                        
                        wrapLine(0, 24, s);
                        wrapLine(1, 24, s);
                        wrapLine(2, 24, s);
                        wrapLine(3, 24, s);
                        
                        
                    } else {
                        myChange = false;
                    }
                }

                private void wrapLine(
                    final int lineNumber,
                    final int maxLength, 
                    final Editable s
                ) {
                    final int startOfLine = 
                        findStartOfLine(lineNumber, s);
                    
                    final int endOfLine =
                        findEndOfLine(startOfLine, maxLength, s);    
                    
                    final int lineLength =
                        endOfLine - startOfLine;
                    
                    if (
                        lineLength >= maxLength &&
                        s.length() > endOfLine && 
                        s.charAt(endOfLine) != ' ' && 
                        s.charAt(endOfLine) != '\n'
                    ) {
                        int startOfWord;
                        
                        for (
                            startOfWord = endOfLine; 
                            startOfWord > 0 && s.charAt(startOfWord) != ' ' && s.charAt(startOfWord) != '\n'; 
                            startOfWord--
                        );
                        
                        s.insert(startOfWord + 1, "\n");
                    }
                }

                private int findEndOfLine(
                    final int startOfLine, 
                    final int maxLength,
                    final Editable s
                ) {
                    int endOfLine;
                    
                    for (
                        endOfLine = startOfLine; 
                        (endOfLine - startOfLine) <= maxLength && endOfLine < s.length() && s.charAt(endOfLine) != '\n'; 
                        endOfLine++
                    );
                    
                    return endOfLine;
                }

                private int findStartOfLine(
                    final int lineNumber, 
                    final Editable s
                ) {
                    if (lineNumber == 0) {
                        return 0;
                        
                    } else {
                        int startOfLine = 0;
                        int numNewlinesSeen = 0;
                        
                        while (
                            numNewlinesSeen < lineNumber && 
                            startOfLine < s.length()
                        ) {
                            if (s.charAt(startOfLine) == '\n') {
                                numNewlinesSeen++;
                            }
                            
                            startOfLine++;
                        }
                        
                        return startOfLine;
                    }
                }
            });
            
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MapEditActivity.this, android.R.layout.simple_spinner_item); 
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            
            spinnerArrayAdapter.addAll("Faces Up", "Faces Down", "Faces Left", "Faces Right");  
            
            npcInitialDirection.setAdapter(spinnerArrayAdapter);
            

            npcEvent.setDirection(Direction.DOWN);
            
            npcInitialDirection.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    final AdapterView<?> parent, 
                    final View view,
                    final int position, 
                    final long id
                ) {
                    npcEvent.setDirection(Direction.values()[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // do nothing
                }
            });
            
            npcInitialDirection.setSelection(1);
            
            final ArrayList<CharacterData> charDataList =
                new ArrayList<CharacterData>();
            
            for (final CharacterSetData charSetData : RpgForgeApplication.getDb().getCharacterSets()) {
                for (final CharacterData charData : charSetData.getCharacters()) {
                    charDataList.add(charData);
                }
            }


            npcEvent.setCharacterData(charDataList.get(0));
            
            final NumberRef currentSelectedPositionInTilePalette = new NumberRef();
            currentSelectedPositionInTilePalette.value = 0;
            
            final BaseAdapter npcListAdapter = new BaseAdapter() {

                @Override
                public View getView(
                    final int position, 
                    final View convertView, 
                    final ViewGroup parent
                ) {
                    final CharacterData charData =
                        charDataList.get(position);

                    ImageView tileView;
                    
                    if (convertView == null) {
                        tileView = 
                            new ImageView(MapEditActivity.this);
                        
                    } else {
                        tileView = 
                            (ImageView) convertView;
                    }
                    
                    if (((int) currentSelectedPositionInTilePalette.value) == position) {
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
                    return (int) (MapEditActivity.this.getResources().getDisplayMetrics().density * dp);
                }
                
                @Override
                public long getItemId(int position) {
                    return System.identityHashCode(getItem(position));
                }
                
                @Override
                public Object getItem(int position) {
                    return charDataList.get(position);
                }
                
                @Override
                public int getCount() {
                    return charDataList.size();
                }
            };
            
            npcSpriteList.setAdapter(npcListAdapter);
            
            
            npcSpriteList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(
                    final AdapterView<?> parent, 
                    final View view,
                    final int position, 
                    final long id
                ) {
                    currentSelectedPositionInTilePalette.value = position;
                    npcListAdapter.notifyDataSetChanged();

                    npcEvent.setCharacterData(charDataList.get(position));
                }
            });

            builder.setView(view)
                .setPositiveButton("Create NPC", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        npcEvent.setCharacterName(npcCharacterName.getText().toString());
                        npcEvent.setCharacterDialog(npcDialog.getText().toString());
                        npcEvent.setInitialPosition(e.x() * 32, (e.y() - 1) * 32);        
                        npcEvent.setStationary(npcStationary.isChecked());
                        tileDrawInProgress = false;                   
                    }
                }).setCancelable(false);
            
            final AlertDialog dialog = builder.create();
            
            dialog.show();
            
            // FIXME: this does not work :(
            /*
            view.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    IBinder token = view.getWindowToken();
                    ( ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow( token, 0 );
                }
            });
            */
        }
    }

    private void editDoorEvent(final DrawTileEvent e) {
        if (tileDrawInProgress == false) {
            tileDrawInProgress = true;
            
            final DoorEventData doorEvent = 
                (DoorEventData) e.tile();
            
            final AlertDialog.Builder builder = 
                new AlertDialog.Builder(this);
            
            final LayoutInflater inflater = 
                this.getLayoutInflater();
            
            final View view = 
                inflater.inflate(R.layout.door_event_edit_layout, null);
            
            final MapView destMap = 
                (MapView) view.findViewById(R.id.doorDestMapView);
            
            final Spinner mapList =
                (Spinner) view.findViewById(R.id.npcInitialDirection);

            final CheckBox activeOnWalkOver =
                (CheckBox) view.findViewById(R.id.activeOnWalkOver);
            
            final BaseAdapter mapListAdapter = new BaseAdapter() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView label = new TextView(MapEditActivity.this);
                    label.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK | Gravity.LEFT);
                    label.setTextSize(18);
                    label.setPadding(8, 16, 8, 16);
                    label.setText(RpgForgeApplication.getDb().getMaps().get(position).getName());
                    label.setTextColor(Color.BLACK);
                    return label;
                }
                
                @Override
                public long getItemId(int position) {
                    return System.identityHashCode(getItem(position));
                }
                
                @Override
                public Object getItem(int position) {
                    return RpgForgeApplication.getDb().getMaps().get(position);
                }
                
                @Override
                public int getCount() {
                    return RpgForgeApplication.getDb().getMaps().size();
                }
            };
            
            mapList.setAdapter(mapListAdapter);
            
            final ObjectRef<MapData> destMapRef = new ObjectRef<MapData>();
            final NumberRef destX = new NumberRef();
            final NumberRef destY = new NumberRef();
            
            mapList.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    final AdapterView<?> parent, 
                    final View view,
                    final int position, 
                    final long id
                ) {
                    destMapRef.value = 
                        RpgForgeApplication.getDb().getMaps().get(position);
                    
                    destMap.setMap(destMapRef.value);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // do nothing
                }
            });
            
            destMap.setMap(currentMap);
            destMap.start();
            destMap.setZOrderOnTop(true);

            destMapRef.value = currentMap;
            destX.value = 32;
            destY.value = 32;
            
            mapList.setSelection(0);
            
            destMap.highlightTile(true, 0, 0);
            
            destMap.setOnTileClickListener(new OnTileClickListener() {
                @Override
                public boolean onTileClick(int tileX, int tileY) {
                    destX.value = tileX * 32;
                    destY.value = (tileY * 32) - 32;
                    destMap.highlightTile(true, tileX, tileY);
                    
                    return true;
                }
            });
            
            
            builder.setView(view)
                .setPositiveButton("Set Destination", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        // TODO: need to query user for activeOnWalkOver
                        doorEvent.setTarget((int) destX.value, (int) destY.value, destMapRef.value, activeOnWalkOver.isChecked());
                        destMap.stop();
                        tileDrawInProgress = false;
                    }
                }).setCancelable(false);
            
            builder.create().show();
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (currentTool == Tool.MOVE) {
                setActiveActionIcon(menu, R.id.menu_move, R.drawable.ic_action_move);
                
            } else if (currentTool == Tool.DRAW) {
                setActiveActionIcon(menu, R.id.menu_draw, R.drawable.ic_action_draw);
                
            } else if (currentTool == Tool.FILL) {
                setActiveActionIcon(menu, R.id.menu_fill, R.drawable.ic_action_fill);
                
            } else if (currentTool == Tool.EYEDROP) {
                setActiveActionIcon(menu, R.id.menu_eyedrop, R.drawable.ic_action_eyedrop);
            }
        }
        
        return true;
    }

    private void setActiveActionIcon(
        final Menu menu, 
        final int menuItemId,
        final int iconId
    ) {
        final MenuItem moveButton = 
            menu.findItem(menuItemId);

        final Bitmap activeMoveBitmap = 
            createActiveActionIcon(iconId);
        
        moveButton.setIcon(new BitmapDrawable(getResources(), activeMoveBitmap));
    }

    private Bitmap createActiveActionIcon(final int resourceId) {
        final Bitmap src = 
            BitmapFactory.decodeResource(getResources(), resourceId);
        
        final Bitmap dst = 
            Bitmap.createBitmap(
                src.getWidth(), 
                src.getHeight(), 
                Bitmap.Config.ARGB_8888);
        
        for (int x = 0; x < dst.getWidth(); x++) {
            for (int y = 0; y < dst.getHeight(); y++) {
                final int srcPixel = src.getPixel(x, y);
                
                if (srcPixel != 0) {
                    dst.setPixel(x, y, (srcPixel & 0xff000000) | 0xFFBB33);
                }
            }
        }
        
        return dst;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                return true;
                
            case R.id.menu_playtest:
                Intent intent = new Intent(this, GameActivity.class);
                //intent.putExtra("PROJECT_NAME", projectName);
                startActivity(intent);
                return true;
                
            case R.id.menu_new:
                handleCreateNewMap();
                return true;
                
            case R.id.menu_move:   
                currentTool = Tool.MOVE;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_eyedrop:
                currentTool = Tool.EYEDROP;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_draw:
                currentTool = Tool.DRAW;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_fill:   
                currentTool = Tool.FILL;
                eventBus.post(new ToolSelectedEvent(currentTool));
                return true;
                
            case R.id.menu_deletemap:
                handleDeleteCurrentMap();
                return true;
                
            case R.id.menu_renamemap:
                handleRenameCurrentMap();
                return true;
                
            case R.id.menu_resizemap:
                showResizeMapDialog();
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleRenameCurrentMap() {
        DialogUtil.showStringPrompt(this, "New Name", "Rename Map", "Cancel", new StringPromptListener() {          
            @Override
            public void onAccept(final String value) {
                currentMap.setName(value);
                eventBus.post(new SelectMapEvent(currentMapIndex, currentMap).setMapListChanged());
            }
        });
    }
    
    private void handleDeleteCurrentMap() {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(MapEditActivity.this);
         
        if (RpgForgeApplication.getDb().getMaps().size() == 1) {
            builder
                .setMessage("Cannot delete the only map in the project.")
                .setPositiveButton("Ok", 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            builder.create().show();
            
        } else {
            builder
            .setMessage("Are you sure you want to delete this map?  It cannot be undone.")
                .setPositiveButton("Delete Map", 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final MapData mapToDelete = 
                                currentMap;
                            
                            RpgForgeApplication.getDb().removeMap(mapToDelete);
                            
                            final int mapIndexToShow = 
                                Math.max(currentMapIndex - 1, 0);
                            
                            final MapData mapToShow = 
                                RpgForgeApplication.getDb().getMaps().get(mapIndexToShow);
                            
                            eventBus.post(new SelectMapEvent(mapIndexToShow, mapToShow).setMapDeleted()); 
                        }
                    })
                .setNegativeButton("Cancel", 
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
   
            builder.create().show();
        }
    }

    private void handleCreateNewMap() {
        showNewMapDialog(new NewMapDialogListener() {    
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onAccept(String mapName, int width, int height) {
                MapData newMap = new MapData(width, height); 
                newMap.fill(RpgForgeApplication.getDb().getDefaultTile());
                
                newMap.setName(mapName);
                RpgForgeApplication.getDb().addMap(newMap);
                
                eventBus.post(new SelectMapEvent(RpgForgeApplication.getDb().getMaps().size() - 1, newMap).setNewMapAdded());   
            }
        });
    }

    private MapData currentMap;
    private int currentMapIndex;
    
    public void onEvent(
        final SelectMapEvent e
    ) {
        mapView.setMap(e.map());
        currentMap = e.map();
        currentMapIndex = e.mapIndex();

        if (mapSelectionAdapter != null) {
            mapSelectionAdapter.notifyDataSetChanged(); 
        }
        
        getActionBar().setSelectedNavigationItem(e.mapIndex());
    }
    
    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        return;
    }
    
    @Override 
    public void onResume() {
        super.onResume();
        mapView.start();


        updateFromDatabase(); 
    }

    private void updateFromDatabase() {
        allTiles = 
            RpgForgeApplication.getDb().getAllTiles();       
        
        eventTilePalette = 
            RpgForgeApplication.getDb().getEvents();
        
        if (mapSelectionAdapter != null) {
            mapSelectionAdapter.notifyDataSetChanged();
        }
        
        if (tilePaletteAdapter != null) {
            tilePaletteAdapter.notifyDataSetChanged();
        }
    }
    
    @Override 
    public void onPause() {
        super.onPause();
        mapView.stop();
        Log.d(TAG, "SAVING FILE: " + RpgForgeApplication.getDbFile());
        RpgForgeApplication.save(this);
    }
    
    @Override 
    public void onDestroy() {
        super.onDestroy();
        mapView.stop();
    }
    
    @Override 
    public void onRestart() {
        super.onRestart();
    }
    
    @Override 
    public void onStop() {
        super.onStop();
        mapView.stop();
    }


    public void showResizeMapDialog() {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(this);
        
        final LayoutInflater inflater = 
            this.getLayoutInflater();
        
        final View view = 
            inflater.inflate(R.layout.map_resize_layout, null);
              
        final TextView mapWidth = 
            (TextView) view.findViewById(R.id.resizeWidthField);
        
        mapWidth.setText(Integer.toString(currentMap.getWidth()));
        
        final TextView mapHeight = 
            (TextView) view.findViewById(R.id.resizeHeightField);
        
        mapHeight.setText(Integer.toString(currentMap.getHeight()));
        
        builder.setView(view)
           .setPositiveButton("Resize Map", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   final String widthString = 
                       mapWidth.getText().toString();
                   
                   final int width = 
                       widthString.length() == 0 ? 20 : Integer.parseInt(widthString);
                   
                   final String heightString = 
                       mapHeight.getText().toString();
                   
                   final int height =
                       heightString.length() == 0 ? 20 : Integer.parseInt(heightString);
                   
                   eventBus.post(new ResizeMapEvent(width, height));
               }
           })
           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // do nothing
               }
           });
        
        builder.create().show();
    }
    
    public void showNewMapDialog(
        final NewMapDialogListener listener
    ) {
        final AlertDialog.Builder builder = 
            new AlertDialog.Builder(this);
        
        final LayoutInflater inflater = 
            this.getLayoutInflater();
        
        final View view = 
            inflater.inflate(R.layout.new_map_layout, null);
        
        final TextView mapNameField = 
            (TextView) view.findViewById(R.id.mapNameField);
        
        final TextView mapWidth = 
            (TextView) view.findViewById(R.id.widthField);
        
        final TextView mapHeight = 
            (TextView) view.findViewById(R.id.heightField);
        
        builder.setView(view)
           .setPositiveButton("Create Map", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   final String mapName = 
                       mapNameField.getText().toString().trim();
                   
                   final String widthString = 
                       mapWidth.getText().toString();
                   
                   final int width = 
                       widthString.length() == 0 ? 20 : Integer.parseInt(widthString);
                   
                   final String heightString = 
                       mapHeight.getText().toString();
                   
                   final int height =
                       heightString.length() == 0 ? 20 : Integer.parseInt(heightString);
                    
                   listener.onAccept(
                       mapName, 
                       width,
                       height);
               }
           })
           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // do nothing
               }
           });
        
        builder.create().show();
    }
    
    public static interface NewMapDialogListener {
        public void onAccept(final String mapName, final int width, final int height);
    }
}

package com.lukevalenty.rpgforge.editor.playercharacter;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.editor.playercharacter.CharacterPickerView.CharacterSelectedListener;

public class PlayerCharacterEditActivity extends BaseActivity {
    @InjectView(R.id.characterPickerView1) CharacterPickerView charPickerView;
    private PlayerCharacterData playerCharacterData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_edit_layout);   
        handleIntent(getIntent(), savedInstanceState);
        
        charPickerView.setCharacterSelectedListener(new CharacterSelectedListener() {
            @Override
            public void onCharacterSelected(final CharacterData characterData) {
                playerCharacterData.setCharacterData(characterData);
            }
        });
    }
    
    
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "MapEditActivity.onNewIntent");
        handleIntent(intent, null);
    }
    
    private void handleIntent(
        final Intent intent, 
        final Bundle savedInstanceState
    ) {
        Log.d(TAG, "MapEditActivity.handleIntent - " + intent);
        
        if (intent.hasExtra("ASSET_INDEX")) {
            final int assetIndex = intent.getIntExtra("ASSET_INDEX", 0);
            setPlayerCharacterData(assetIndex);
        
        } else if (
            savedInstanceState != null && 
            savedInstanceState.containsKey("ASSET_INDEX")
        ) {
            setPlayerCharacterData(savedInstanceState.getInt("ASSET_INDEX"));
            
        } else {
            setPlayerCharacterData(0);
        }

        Log.d(TAG, "HANDLE INTENT FINISHED");
    }


    private void setPlayerCharacterData(int assetIndex) {
        if (assetIndex >= RpgForgeApplication.getDb().getPlayerCharacters().size()) {
            final CharacterData defaultCharacterData = 
                RpgForgeApplication.getDb().getCharacterSets().getFirst().getCharacters().get(0);
            
            final PlayerCharacterData newPlayerCharacterData = 
                new PlayerCharacterData(defaultCharacterData);
            
            RpgForgeApplication.getDb().getPlayerCharacters().add(newPlayerCharacterData);
        }
        
        playerCharacterData = 
            RpgForgeApplication.getDb().getPlayerCharacters().get(assetIndex);
        
        charPickerView.setCurrentSelectedChar(playerCharacterData.getCharacterData());
    }

}

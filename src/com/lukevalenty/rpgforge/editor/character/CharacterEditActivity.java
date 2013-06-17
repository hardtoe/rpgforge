package com.lukevalenty.rpgforge.editor.character;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lukevalenty.rpgforge.BaseActivity;
import com.lukevalenty.rpgforge.R;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.BattleCharacterData;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.EnemyCharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.editor.character.CharacterPickerView.CharacterSelectedListener;

public class CharacterEditActivity extends BaseActivity {
    @InjectView(R.id.characterPickerView1) CharacterPickerView charPickerView;
    @InjectView(R.id.characterName) EditText characterName;
    private BattleCharacterData battleCharacterData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_edit_layout);   
        handleIntent(getIntent(), savedInstanceState);
        
        charPickerView.setCharacterSelectedListener(new CharacterSelectedListener() {
            @Override
            public void onCharacterSelected(final CharacterData characterData) {
                battleCharacterData.setCharacterData(characterData);
            }
        });
        
        characterName.setText(battleCharacterData.getName());
        characterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(
                final CharSequence s, 
                final int start, 
                final int count,
                final int after
            ) {
                // do nothing
            }
            
            @Override
            public void beforeTextChanged(
                final CharSequence s, 
                final int start, 
                final int count,
                final int after
            ) {
                // do nothing
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                battleCharacterData.setName(s.toString());
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
            setBattleCharacterData(assetIndex, intent.getBooleanExtra("ENEMY_CHARACTER", false));
        
        } else if (
            savedInstanceState != null && 
            savedInstanceState.containsKey("ASSET_INDEX")
        ) {
            setBattleCharacterData(savedInstanceState.getInt("ASSET_INDEX"), savedInstanceState.getBoolean("ENEMY_CHARACTER", false));
            
        } else {
            setBattleCharacterData(0, false);
        }

        Log.d(TAG, "HANDLE INTENT FINISHED");
    }


    private void setBattleCharacterData(
        final int assetIndex, 
        final boolean isEnemyCharacter
    ) {
        if (isEnemyCharacter) {
            if (assetIndex >= RpgForgeApplication.getDb().getEnemyCharacters().size()) {
                final CharacterData defaultCharacterData = 
                    RpgForgeApplication.getDb().getCharacterSets().getFirst().getCharacters().get(0);
                
                final EnemyCharacterData newEnemyCharacterData = 
                    new EnemyCharacterData(defaultCharacterData);
                
                RpgForgeApplication.getDb().getEnemyCharacters().add(newEnemyCharacterData);
            }
            
            battleCharacterData = 
                RpgForgeApplication.getDb().getEnemyCharacters().get(assetIndex);
            
        } else {
            if (assetIndex >= RpgForgeApplication.getDb().getPlayerCharacters().size()) {
                final CharacterData defaultCharacterData = 
                    RpgForgeApplication.getDb().getCharacterSets().getFirst().getCharacters().get(0);
                
                final PlayerCharacterData newPlayerCharacterData = 
                    new PlayerCharacterData(defaultCharacterData);
                
                RpgForgeApplication.getDb().getPlayerCharacters().add(newPlayerCharacterData);
            }
            
            battleCharacterData = 
                RpgForgeApplication.getDb().getPlayerCharacters().get(assetIndex);
        }
        
        charPickerView.setCurrentSelectedChar(battleCharacterData.getCharacterData());
    }

}

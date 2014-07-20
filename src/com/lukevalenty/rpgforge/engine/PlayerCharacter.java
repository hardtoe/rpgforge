package com.lukevalenty.rpgforge.engine;


import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.data.RpgDatabase;

public class PlayerCharacter extends GameObject {   
    private final PlayerCharacterData playerCharacterData;

    public PlayerCharacter() {
        final RpgDatabase db = 
            RpgForgeApplication.getDb();
        
        playerCharacterData = 
            db.getPlayerCharacters().get(0);
        
        final CharacterData charData =
            playerCharacterData.getCharacterData();

        addComponent(new PlayerControlComponent(this));
        addComponent(new CollisionComponent(this));
        addComponent(new MovementComponent(this)); 
        addComponent(new CameraFocusComponent(this));
        addComponent(new CharacterRenderComponent(this, charData));
    }
    
    public PlayerCharacterData getPlayerCharacterData() {
        return playerCharacterData;
    }
}

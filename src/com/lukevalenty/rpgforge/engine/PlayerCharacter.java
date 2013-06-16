package com.lukevalenty.rpgforge.engine;


import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.RpgDatabase;

public class PlayerCharacter extends GameObject {   
    public PlayerCharacter() {
        final RpgDatabase db = 
            RpgForgeApplication.getDb();
        
        final CharacterData charData =
            db.getPlayerCharacters().get(0).getCharacterData();

        addComponent(new PlayerControlComponent(this));
        addComponent(new CollisionComponent(this));
        addComponent(new MovementComponent(this)); 
        addComponent(new CameraFocusComponent(this));
        addComponent(new CharacterRenderComponent(this, charData));
    }
}

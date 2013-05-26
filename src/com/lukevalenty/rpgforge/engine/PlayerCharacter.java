package com.lukevalenty.rpgforge.engine;


import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;

public class PlayerCharacter extends GameObject {   
    public PlayerCharacter() {
        final CharacterData charData = 
            RpgForgeApplication.getDb().getCharacterSets().get(0).getCharacters().get(0);
        
        addComponent(new PlayerControlComponent(this));
        addComponent(new CollisionComponent(this));
        addComponent(new MovementComponent(this));
        addComponent(new CharacterRenderComponent(this, charData));
    }
}

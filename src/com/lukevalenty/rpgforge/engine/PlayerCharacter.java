package com.lukevalenty.rpgforge.engine;


import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.CharacterData;
import com.lukevalenty.rpgforge.data.PlayerCharacterData;
import com.lukevalenty.rpgforge.data.RpgDatabase;

public class PlayerCharacter extends GameObject {   
    private PlayerCharacterData playerCharacterData;

    private PlayerCharacter() {
        // used only for serialization
    }
    
    public PlayerCharacter(
        final PlayerCharacterData playerCharacterData
    ) {
        this.playerCharacterData = 
            playerCharacterData;
        
        final CharacterData charData =
            playerCharacterData.getCharacterData();

        addComponent(new PlayerControlComponent());
        addComponent(new PlayerCombatComponent(charData));
        addComponent(new CollisionComponent());
        addComponent(new MovementComponent()); 
        addComponent(new CameraFocusComponent());
        addComponent(new CharacterRenderComponent(charData));
    }
    
    public PlayerCharacterData getPlayerCharacterData() {
        return playerCharacterData;
    }
}

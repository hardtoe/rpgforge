package com.lukevalenty.rpgforge.engine;

import com.lukevalenty.rpgforge.data.CharacterData;

public class NonPlayerCharacter extends GameObject {   
    public NonPlayerCharacter(
        final CharacterData charData, 
        final int xStart, 
        final int yStart
    ) {        
        getNumberRef("x").value = xStart;
        getNumberRef("y").value = yStart;
        
        addComponent(new RandomWalkComponent());
        addComponent(new CollisionComponent());
        addComponent(new MovementComponent());
        addComponent(new CharacterRenderComponent(charData));
    }
}

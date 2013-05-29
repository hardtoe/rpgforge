package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;
import com.lukevalenty.rpgforge.graphics.DrawTileMapPool;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;

public class FrameState {
    public float timeDelta;
    public GamePhase phase;
    public ArrayList<DrawCommand> drawBuffer;
    public SetMatrixPool setMatrixPool;
    public DrawSpritePool spritePool;
    public DrawTileMapPool tilemapPool;
}

package com.lukevalenty.rpgforge.engine;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.EventData;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.engine.input.GameInput;
import com.lukevalenty.rpgforge.engine.input.OnScreenDPad;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;
import com.lukevalenty.rpgforge.graphics.DrawTileMapPool;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;

import de.greenrobot.event.EventBus;

public class GameEngine {
    private final MainLoop mainLoop;
    private Thread mainLoopThread;
    private GameInput gameInput;

    @Inject GameEngine(
        final MainLoop mainLoop
    ) {
        this.mainLoop = mainLoop;
    }

    public void start() {
        mainLoopThread = new Thread(mainLoop);
        mainLoop.setRunning(true);
        mainLoopThread.start();
    }
    
    public void stop() {
        mainLoop.setRunning(false);
        
        try {
            mainLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class MainLoop implements Runnable {
        private final DrawCommandBuffer drawCommandBuffer;
        private final SetMatrixPool setMatrixPool;
        private final DrawSpritePool spritePool;
        private final DrawTileMapPool tilemapPool;
        private final EventBus eventBus;

        private final GlobalGameState globalState;
        private final GameObjectContainer gameTree;
        
        private boolean mRunning;

        

        
        @Inject MainLoop(
            final DrawCommandBuffer drawCommandBuffer,
            final SetMatrixPool setMatrixPool,
            final DrawSpritePool spritePool,
            final DrawTileMapPool tilemapPool,
            final EventBus eventBus,
            final Context context
        ) {
            this.drawCommandBuffer = drawCommandBuffer;      
            this.setMatrixPool = setMatrixPool;
            this.spritePool = spritePool;
            this.tilemapPool = tilemapPool;
            this.eventBus = eventBus;

            globalState = 
                new GlobalGameState();
            
            final MapData map = 
                RpgForgeApplication.getDb().getMaps().getFirst();
            
            globalState.setMap(map);
            
            gameTree = 
                new GameObjectContainer(); 

            // 512 x 384 effective resolution
            final float scaleFactor =
                (float) (context.getResources().getDisplayMetrics().heightPixels / 384.0);
            
            gameTree.add(new CameraObject(scaleFactor));
            gameTree.add(new MapObject());
            gameTree.add(new PlayerCharacter());

            globalState.mapGameObjects =
                map.getGameObjects();
            
            for (int i = 0; i < globalState.mapGameObjects.size(); i++) {
                gameTree.add(globalState.mapGameObjects.get(i));
            }
            
        }

        
        @Override
        public void run() {
            long lastFrameTimestamp =
                System.nanoTime();
            
            final FrameState frameState = new FrameState();
            
            frameState.setMatrixPool = setMatrixPool;
            frameState.spritePool = spritePool;
            frameState.tilemapPool = tilemapPool;
            
            while(mRunning){
                frameState.drawBuffer = 
                    drawCommandBuffer.lockBackBuffer();

                frameState.timeDelta = 
                    (float) ((System.nanoTime() - lastFrameTimestamp) / 1000000.0);
                
                lastFrameTimestamp =
                    System.nanoTime();
                
                for (final GamePhase phase : GamePhase.values()) {
                    frameState.phase = phase;
                    gameTree.update(frameState, globalState);
                }
                
                drawCommandBuffer.unlockBackBuffer();
            }
        }

        public void setRunning(final boolean b) {
            mRunning = b;
        }


        public void setGameInput(final GameInput gameInput) {
            globalState.setGameInput(gameInput);
        }
    }

    public void addGameInput(final GameInput gameInput) {
        mainLoop.setGameInput(gameInput);
    }
}

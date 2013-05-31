package com.lukevalenty.rpgforge.engine;

import java.util.Collections;
import java.util.Comparator;

import android.content.Context;

import com.google.inject.Inject;
import com.lukevalenty.rpgforge.RpgForgeApplication;
import com.lukevalenty.rpgforge.data.MapData;
import com.lukevalenty.rpgforge.engine.input.GameInput;
import com.lukevalenty.rpgforge.graphics.DrawCommand;
import com.lukevalenty.rpgforge.graphics.DrawCommandBuffer;
import com.lukevalenty.rpgforge.graphics.DrawSpritePool;
import com.lukevalenty.rpgforge.graphics.DrawTileMapPool;
import com.lukevalenty.rpgforge.graphics.SetMatrixPool;

import de.greenrobot.event.EventBus;

public class GameEngine {
    private final MainLoop mainLoop;
    private Thread mainLoopThread;

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

            globalState.gameTree = 
                new GameObjectContainer(); 
            

            // 512 x 384 effective resolution
            final float scaleFactor =
                (float) (context.getResources().getDisplayMetrics().heightPixels / 384.0);
            
            globalState.gameTree.add(new CameraObject(scaleFactor));
            globalState.gameTree.add(new MapObject());
            globalState.gameTree.add(new PlayerCharacter());
            
            
            globalState.setMap(map);
            


            
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

                if (frameState.drawBuffer != null) {
                    frameState.timeDelta = 
                        (float) ((System.nanoTime() - lastFrameTimestamp) / 1000000.0);
                    
                    lastFrameTimestamp =
                        System.nanoTime();
                    
                    for (final GamePhase phase : GamePhase.values()) {
                        frameState.phase = phase;
                        globalState.gameTree.update(frameState, globalState);
                    }
                    
                    Collections.sort(frameState.drawBuffer, new Comparator<DrawCommand>() {
                        @Override
                        public int compare(DrawCommand lhs, DrawCommand rhs) {
                            if (lhs.z() < rhs.z()) {
                                return -1;
                                
                            } else if (lhs.z() > rhs.z()) {
                                return 1;
                                
                            } else {
                                return 0;
                            }
                        }
                    });
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

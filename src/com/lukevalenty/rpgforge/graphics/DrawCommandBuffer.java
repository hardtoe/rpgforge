package com.lukevalenty.rpgforge.graphics;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import roboguice.inject.ContextSingleton;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Double buffer for DrawCommands.
 * 
 * Front Buffer is consumed by the renderer.
 * Back Buffer is filled by the game engine.
 * 
 * @author luke
 *
 */
@ContextSingleton
public class DrawCommandBuffer {
    private final int SIZE = 2;
    
    private ArrayList<DrawCommand>[] buffers;
    private Lock[] bufferLocks;
    
    private int frontBufferPointer;
    private int backBufferPointer;
    
    @Inject DrawCommandBuffer() {
        buffers = new ArrayList[2];
        buffers[0] = new ArrayList<DrawCommand>(128);
        buffers[1] = new ArrayList<DrawCommand>(128);
        
        bufferLocks = new Lock[2];
        bufferLocks[0] = new ReentrantLock();
        bufferLocks[1] = new ReentrantLock();
        
        frontBufferPointer = 0;
        backBufferPointer = 1;
    }
    
    public ArrayList<DrawCommand> lockFrontBuffer() {
        bufferLocks[frontBufferPointer].lock();
        return buffers[frontBufferPointer];
    }
    
    public void unlockFrontBuffer() {
        bufferLocks[frontBufferPointer].unlock();
        frontBufferPointer = (frontBufferPointer + 1) % SIZE;
    }
    
    public ArrayList<DrawCommand> lockBackBuffer() {
        bufferLocks[backBufferPointer].lock();
        
        for (int i = 0; i < buffers[backBufferPointer].size(); i++) {
            buffers[backBufferPointer].get(i).recycle();
        }
        
        buffers[backBufferPointer].clear();
        
        return buffers[backBufferPointer];
    }
    
    public void unlockBackBuffer() {
        bufferLocks[backBufferPointer].unlock();
        backBufferPointer = (backBufferPointer + 1) % SIZE;
    }
}

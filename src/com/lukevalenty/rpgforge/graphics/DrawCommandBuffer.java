package com.lukevalenty.rpgforge.graphics;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import roboguice.inject.ContextSingleton;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lukevalenty.rpgforge.memory.AbstractPooledObject;
import com.lukevalenty.rpgforge.memory.ObjectPool;
import com.lukevalenty.rpgforge.memory.PooledObject;

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
    private final ArrayBlockingQueue<ArrayList<DrawCommand>> pool;
    private final ArrayBlockingQueue<ArrayList<DrawCommand>> queue;
    private ArrayList<DrawCommand> backBuffer;
    private ArrayList<DrawCommand> frontBuffer;
    
    @Inject DrawCommandBuffer() {
        pool = new ArrayBlockingQueue<ArrayList<DrawCommand>>(2);
        queue = new ArrayBlockingQueue<ArrayList<DrawCommand>>(1);
        
        try {
            pool.put(new ArrayList<DrawCommand>(128));
            pool.put(new ArrayList<DrawCommand>(128));
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<DrawCommand> lockFrontBuffer() {
        try {
            frontBuffer = queue.poll(100, TimeUnit.MILLISECONDS);
            
        } catch (InterruptedException e) {
            // do nothing
        }
        
        return frontBuffer;
    }
    
    public void unlockFrontBuffer() {
        if (frontBuffer != null) {
            for (int i = 0; i < frontBuffer.size(); i++) {
                frontBuffer.get(i).recycle();
            }
            
            frontBuffer.clear();
            
            try {
                pool.offer(frontBuffer, 100, TimeUnit.MILLISECONDS);
                
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }
    
    public ArrayList<DrawCommand> lockBackBuffer() {
        try {
            backBuffer = pool.take();
            
        } catch (InterruptedException e) {
            // do nothing
        } 
        
        return backBuffer;
    }
    
    public void unlockBackBuffer() {
        try {
            //while (!queue.offer(backBuffer)) {
            //    Thread.sleep(1);
            //}
            
            queue.put(backBuffer);
            
        } catch (InterruptedException e) {
            // do nothing
        }
    }
    
    /*
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
    */
}

package se.krka.kahlua.vm;

import com.lukevalenty.rpgforge.memory.ObjectPool;

public class LuaCallFramePool extends ObjectPool<LuaCallFrame> {
    @Override
    protected LuaCallFrame create() {
        return new LuaCallFrame(this);
    }
}

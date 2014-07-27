package com.lukevalenty.rpgforge.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;

public class LuaGameObjectComponent extends GameObjectComponent {
    private transient final KahluaConverterManager converterManager = new KahluaConverterManager();
    private transient final J2SEPlatform platform = new J2SEPlatform();
    private transient final KahluaTable env = platform.newEnvironment();
    private transient final KahluaThread thread = new KahluaThread(platform, env);
    private transient final LuaCaller caller = new LuaCaller(converterManager);
    private transient final LuaJavaClassExposer exposer = new LuaJavaClassExposer(converterManager, platform, env);
    
    private transient LuaClosure coroutine;
    
    private String scriptFileName;
    
    private LuaGameObjectComponent() {
        // used for serialization only
    }
    
    public LuaGameObjectComponent(final String scriptFileName) {
        this.scriptFileName = scriptFileName; 
    }


    @Override
    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        exposer.exposeGlobalFunctions(globalState);
        exposer.exposeGlobalFunctions(gameObject);
        exposer.exposeGlobalFunctions(exposer);
        
        try {
            final BufferedReader scriptReader = 
                new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(scriptFileName)));
            
            final StringBuilder script =
                new StringBuilder();
            
            String line = null;
            
            script.append("co = coroutine.create(function() \n");
            while ((line = scriptReader.readLine()) != null) {
                script.append(line).append("\n");
            }
            script.append(" end)");
            
            final LuaClosure coroutineBuilder = 
                LuaCompiler.loadstring(script.toString(), "", env);
            
            caller.protectedCall(thread, coroutineBuilder);
            
            coroutine = LuaCompiler.loadstring("coroutine.resume(co)", "", env);
            
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    @Override
    public void update(
        final FrameState frameState, 
        final GlobalGameState globalState
    ) {
        if (frameState.phase == GamePhase.UPDATE) {
            thread.pcall(coroutine);
        }
    }

}

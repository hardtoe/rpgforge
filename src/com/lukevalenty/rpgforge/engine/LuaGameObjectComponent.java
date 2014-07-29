package com.lukevalenty.rpgforge.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

import com.lukevalenty.rpgforge.engine.battle.ExecuteCombatTurn;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.LuaReturn;
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
    
    private transient LuaClosure main;
    private transient LuaClosure onMessage;
    
    private String scriptFileName;
    private Object[] args;
    
    private LuaGameObjectComponent() {
        // used for serialization only
        Log.e("LuaGameObjectComponent", "LuaGameObjectComponent()");
    }
    
    public LuaGameObjectComponent(final String scriptFileName, final Object... args) {
        this.scriptFileName = scriptFileName; 
        this.args = args;
        Log.e("LuaGameObjectComponent", "LuaGameObjectComponent("+scriptFileName+")");
    }


    @Override
    public void init(
        final GameObject gameObject, 
        final GlobalGameState globalState
    ) {
        exposer.exposeGlobalFunctions(globalState);
        exposer.exposeGlobalFunctions(gameObject);
        exposer.exposeGlobalFunctions(exposer);

        exposer.exposeClass(GameMessage.class);
        exposer.exposeClass(ExecuteCombatTurn.class);
        
        try {
            // load RPG Forge Library
            final LuaClosure rpgForgeLib = 
                LuaCompiler.loadis(getClass().getResourceAsStream("/com/lukevalenty/rpgforge/engine/RpgForgeLib.lua"), "", env);

            caller.protectedCall(thread, rpgForgeLib);
            
            // read in component script
            final BufferedReader scriptReader = 
                new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(scriptFileName)));
            
            final StringBuilder script =
                new StringBuilder();
            
            String line = null;
            
            while ((line = scriptReader.readLine()) != null) {
                script.append(line).append("\n");
            }
            script.append("co = coroutine.create(main)");
            
            final LuaClosure coroutineBuilder = 
                LuaCompiler.loadstring(script.toString(), "", env);
            
            caller.protectedCall(thread, coroutineBuilder, args);
            
            // save main coroutine and onMessage function
            main = LuaCompiler.loadstring("coroutine.resume(co)", "", env); 
            onMessage = (LuaClosure) env.rawget("onMessage"); //LuaCompiler.loadstring("onMessage(...)", "", env);
            
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
            thread.pcall(main);
        }
    }
    
    @Override
    public void onMessage(
        final GameMessage m
    ) {
        LuaReturn result = caller.protectedCall(thread, onMessage, m);
        if (!result.isSuccess()) {
            Log.e("LuaGameObject", result.getErrorString());
            Log.e("LuaGameObject", result.getLuaStackTrace());
        }
    }
}

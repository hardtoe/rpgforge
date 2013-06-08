package com.lukevalenty.rpgforge.editor.map;

public class ToolSelectedEvent {
    private final Tool tool;
    
    public ToolSelectedEvent(final Tool tool) {
        this.tool = tool;
    }
    
    public Tool tool() {
        return tool;
    }
}

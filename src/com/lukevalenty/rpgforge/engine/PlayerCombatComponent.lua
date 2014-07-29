local tag = "PlayerCombatComponent"
local initiateCombatTurn = false
local executeCombatTurnMsg = nil

function main()
  while true do
    while not initiateCombatTurn do
      coroutine.yield() 
    end
    
    initiateCombatTurn = false
    
    local selection =
      getSelectionGui(
        12, 9, 
        16, 12, 
        "Attack",
        "Move")
    
    if selection == 0 then
      -- attack
      log(tag, "ATTACK")
      
    elseif selection == 1 then
      -- move
      log(tag, "MOVE")
    
    end
      
    executeCombatTurnMsg:setFinished(true)
  end
end

function onMessage(msg)
  initiateCombatTurn = true
  executeCombatTurnMsg = msg
end

local enemyCharacterData = ...

local tag = "EnemyCombatComponent"
local initiateCombatTurn = false
local executeCombatTurnMsg = nil

function main()
  -- perform some basic exclamation and initialization before the main loop
  log(tag, "IM ALIVE!")
  setLocalFlag("isEnemy", true)
  setLocalObject("enemyCharacterData", enemyCharacterData)
    
  while true do
    -- wait for combat
    while not initiateCombatTurn do
      coroutine.yield() 
    end
    
    log(tag, "ENEMY IN COMBAT")
    
    initiateCombatTurn = false
    
    -- do something here
      
    -- pass control back to combat sequencer
    executeCombatTurnMsg:setFinished(true)
  end
end

function onMessage(msg)
  initiateCombatTurn = true
  executeCombatTurnMsg = msg
end

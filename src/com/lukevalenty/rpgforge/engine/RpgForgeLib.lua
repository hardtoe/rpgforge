function getSelectionGui(x1, y1, x2, y2, ...) 
  local selection = 0
  local numOptions = select('#', ...)
  
  local up = false
  local down = false
  
  while true do
    displaySelectionWindow(
      x1, 
      y1, 
      x2, 
      y2, 
      selection,
      ...)
      
    if isUpPressed() then
      if not up then
        selection = selection - 1
        
        if selection < 0 then
          selection = 0
        end
      end
      
      up = true
      down = false
      
      
    elseif isDownPressed() then
      if not down then
        selection = selection + 1
        
        if selection >= numOptions then
          selection = numOptions - 1
        end
      end
      
      up = false
      down = true
      
    elseif isActionPressed() then
      return selection
      
    else
      up = false
      down = false
    end
      
    coroutine.yield()
  end
    
  return selection
end
package com.gameoff.game.behaviors;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.umisc.StringUtils;

public class WaitRandom extends BehaviorNode{

  float timeMin = 1;
  float time = 1;
  float timeMax = 3;
  float elapsed = 0;
  
  public WaitRandom(float timeMin, float timeMax) {
    this.time = Math.max(this.time, time);
    this.timeMin = this.time;
    this.timeMax = Math.max(this.timeMax, timeMax);
  }
  
  @Override
  public void init() {
    super.init();
    elapsed = 0;
    this.time = this.timeMin + (float) Math.random() * (timeMax - timeMin);
  }
  
  @Override
  public NodeState update(float delta) {
    super.update(delta);
    
    elapsed+=delta;
    
    if(elapsed >= time) {
      return setState(NodeState.Success);
    }
    
    System.out.println(StringUtils.format("waiting elapsed=%s time=%s", elapsed,time));
    return setState(NodeState.Running);
  }

}

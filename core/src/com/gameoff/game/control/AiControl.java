package com.gameoff.game.control;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;
import com.kyperbox.umisc.UserData;

public class AiControl extends GameObjectController{
	
	
	
	BehaviorTree tree;
	BehaviorNode root;
	UserData context;
	
	public AiControl(UserData context,BehaviorNode root) {
		this.root = root;
		this.tree = new BehaviorTree();
		this.context = context;
	}
	
	public BehaviorTree getTree() {
		return tree;
	}
	
	public void setTree(BehaviorTree tree) {
		this.tree = tree;
	}
	
	public BehaviorNode getRoot() {
		return root;
	}
	
	public void setRoot(BehaviorNode root) {
		this.root = root;
	}
	
	public UserData getContext() {
		return this.context;
	}
	
	public void setContext(UserData context) {
		this.context = context;
	}

	@Override
	public void init(GameObject object) {
		
	}

	@Override
	public void update(GameObject object, float delta) {
		
	}

	@Override
	public void remove(GameObject object) {
		
	}

}

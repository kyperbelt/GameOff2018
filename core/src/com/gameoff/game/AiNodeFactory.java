package com.gameoff.game;

import com.badlogic.gdx.utils.JsonValue;
import com.gameoff.game.behaviors.TestNode;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.ai.InvertNode;
import com.kyperbox.ai.NodeGetter;
import com.kyperbox.ai.RandomSelectorNode;
import com.kyperbox.ai.RandomSequenceNode;
import com.kyperbox.ai.RepeatNode;
import com.kyperbox.ai.RepeatUntilFailNode;
import com.kyperbox.ai.SelectorNode;
import com.kyperbox.ai.SequenceNode;
import com.kyperbox.ai.SuccessNode;

public class AiNodeFactory {

	public static void createNodeDic() {
		
		//composite nodes
		BehaviorTree.registerNode("Sequence", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new SequenceNode();
			}
		});
		
		BehaviorTree.registerNode("Selector", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new SelectorNode();
			}
		});
		
		BehaviorTree.registerNode("RandomSequence", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new RandomSequenceNode();
			}
		});
		
		BehaviorTree.registerNode("RandomSelector", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new RandomSelectorNode();
			}
		});
		
		//supplement
		
		BehaviorTree.registerNode("Repeat", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {

				int count = properties.getChild("count").asInt();
				return new RepeatNode(count);
			}
		});
		
		BehaviorTree.registerNode("RepeatUntilFail", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {

				return new RepeatUntilFailNode();
			}
		});
		
		BehaviorTree.registerNode("Invert", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {

				return new InvertNode();
			}
		});
		
		BehaviorTree.registerNode("Success", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new SuccessNode();
			}
		});
		
		
		//leafs
		
		BehaviorTree.registerNode("Test", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				String t = properties.getChild("testText").asString();
				return new TestNode(t);
			}
		});
	}
}

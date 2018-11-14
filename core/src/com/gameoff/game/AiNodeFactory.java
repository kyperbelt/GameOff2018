package com.gameoff.game;

import com.badlogic.gdx.utils.JsonValue;
import com.gameoff.game.behaviors.*;
import com.kyperbox.ai.*;

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
		
		BehaviorTree.registerNode("Parallel", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new ParallelNode();
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
		
		BehaviorTree.registerNode("Wait", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				float t = properties.getChild("time").asFloat();
				return new Wait(t);
			}
		});
		
		BehaviorTree.registerNode("FindPlayerInRange", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				float range = properties.getChild("range").asFloat();
				return new FindPlayerInRange(range);
			}
		});
		
		BehaviorTree.registerNode("MoveToTarget", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				String target = properties.getChild("target").asString();
				return new MoveToTarget(target);
			}
		});
		
		BehaviorTree.registerNode("CheckDamagedState", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new CheckDamageState();
			}
		});
		
		BehaviorTree.registerNode("SetContextObject", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				String name = properties.getChild("objectName").asString();
				String layer = properties.getChild("layerName").asString();
				String context = properties.getChild("contextName").asString();
				return new SetContextObject(name,layer,context);
			}
		});
		
		
		BehaviorTree.registerNode("RemoveObject", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				String contextName = properties.getChild("contextName").asString();
				return new RemoveObject(contextName);
			}
		});
		
		BehaviorTree.registerNode("SpawnObject", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				String objectType = properties.getChild("objectType").asString();
				String layerName = properties.getChild("layerName").asString();
				float x = properties.getChild("x").asFloat();
				float y = properties.getChild("y").asFloat();
				return new SpawnObject(objectType, layerName, x, y);
			}
		});

		BehaviorTree.registerNode("UpdateDoor", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				String doorName = properties.getChild("doorName").asString();
				boolean closeDoor = properties.getChild("closeDoor").asBoolean();
				return new UpdateDoor(doorName, closeDoor);
			}
		});
		
		BehaviorTree.registerNode("StopMovement", new NodeGetter() {
			@Override
			public BehaviorNode getNode(JsonValue properties) {
				return new StopMovement();
			}
		});
		
	}
}

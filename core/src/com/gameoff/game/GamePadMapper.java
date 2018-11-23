package com.gameoff.game;

import com.badlogic.gdx.controllers.Controller;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.umisc.KyperControllerMapper;
import com.kyperbox.umisc.StringUtils;

public class GamePadMapper {

	public static KyperControllerMapper getMapsForController(Controller c) {
		if (XBoxOneWindows.isXboxOneWindowsController(c))
			return XBoxOneWindows.getMappings();
		if (XBoxBluetoothGamepad.isXboxBluetoothGamepad(c))
			return XBoxBluetoothGamepad.getMappings();
		if (BluetoothWirelessController.isBluetoothWirelessController(c))
			return BluetoothWirelessController.getMappings();
		if (XBoxController.isXBoxController(c))
			return XBoxController.getMappings();
		KyperBoxGame.error(KyperControllerMapper.class.getSimpleName(),
				StringUtils.format("mappings for [%s] not found.", c.getName()));
		return BluetoothWirelessController.getMappings();
	}

	// -------------------------------------------
	public static class XBoxController {
		public static final int BUTTON_A = 0;
		public static final int BUTTON_B = 1;
		public static final int BUTTON_X = 2;
		public static final int BUTTON_Y = 3;
		public static final int DPAD = 0;
		public static final int L1 = 4;
		public static final int R1 = 5;
		public static final int SELECT = 6;
		public static final int START = 7;
		public static final int L3 = 8;
		public static final int R3 = 9;
		public static final int L2_TRIGGER = 4; // -1 to 1
		public static final int R2_TRIGGER = 5; // -1 to -1;
		public static final int ANALOG_LEFT_X = 1; //
		public static final int ANALOG_LEFT_Y = 0;
		public static final int ANALOG_RIGHT_X = 3;
		public static final int ANALOG_RIGHT_Y = 2;
		public static final int EXTRA_1 = 10;

		public static boolean isXBoxController(Controller controller) {
			return controller.getName().toLowerCase().contains("xbox")
					&& controller.getName().toLowerCase().contains("controller");
		}

		public static KyperControllerMapper getMappings() {
			if (KyperBoxGame.DEBUG_LOGGING) {
				KyperBoxGame.log("ControllerMappings", "Xbox Controller Mappings");
			}
			KyperControllerMapper mappings = new KyperControllerMapper();
			mappings.button_a = BUTTON_A;
			mappings.button_b = BUTTON_B;
			mappings.button_x = BUTTON_X;
			mappings.button_y = BUTTON_Y;
			mappings.left_1 = L1;
			mappings.right_1 = R1;
			mappings.select = SELECT;
			mappings.start = START;
			mappings.left_3 = L3;
			mappings.right_3 = R3;
			mappings.left2_trigger = L2_TRIGGER;
			mappings.right2_trigger = R2_TRIGGER;
			mappings.analog_left_x = ANALOG_LEFT_X;
			mappings.analog_left_y = ANALOG_LEFT_Y;
			mappings.analog_right_x = ANALOG_RIGHT_X;
			mappings.analog_right_y = ANALOG_RIGHT_Y;
			mappings.dpad = DPAD;
			mappings.extra1 = EXTRA_1;
			mappings.axis_trigger = true;

			return mappings;

		}
	}

	
	//---------------------------------------
	public static class XBoxOneWindows {
		public static final int BUTTON_A = 0;
		public static final int BUTTON_B = 1;
		public static final int BUTTON_X = 2;
		public static final int BUTTON_Y = 3;
		public static final int DPAD = 0;
		public static final int L1 = 4;
		public static final int R1 = 5;
		public static final int SELECT = 6;
		public static final int START = 7;
		public static final int L3 = 8;
		public static final int R3 = 9;
		public static final int L2_TRIGGER = 4; // 0 to 1
		public static final int R2_TRIGGER = 4; // 0 to -1;
		public static final int ANALOG_LEFT_X = 1; //
		public static final int ANALOG_LEFT_Y = 0;
		public static final int ANALOG_RIGHT_X = 3;
		public static final int ANALOG_RIGHT_Y = 2;

		public static boolean isXboxOneWindowsController(Controller controller) {
			return controller.getName().toLowerCase().contains("xbox")
					&& controller.getName().toLowerCase().contains("one")
					&& controller.getName().toLowerCase().contains("windows");
		}

		public static KyperControllerMapper getMappings() {
			if (KyperBoxGame.DEBUG_LOGGING) {
				KyperBoxGame.log("ControllerMappings", "Xbox One Controller Mappings");
			}
			KyperControllerMapper mappings = new KyperControllerMapper();
			mappings.button_a = BUTTON_A;
			mappings.button_b = BUTTON_B;
			mappings.button_x = BUTTON_X;
			mappings.button_y = BUTTON_Y;
			mappings.left_1 = L1;
			mappings.right_1 = R1;
			mappings.select = SELECT;
			mappings.start = START;
			mappings.left_3 = L3;
			mappings.right_3 = R3;
			mappings.left2_trigger = L2_TRIGGER;
			mappings.right2_trigger = R2_TRIGGER;
			mappings.analog_left_x = ANALOG_LEFT_X;
			mappings.analog_left_y = ANALOG_LEFT_Y;
			mappings.analog_right_x = ANALOG_RIGHT_X;
			mappings.analog_right_y = ANALOG_RIGHT_Y;
			mappings.dpad = DPAD;
			mappings.axis_trigger = true;
			mappings.triggers_same_axis = true;
			return mappings;
		}

	}

	//---------------------------------------
	public static class XBoxBluetoothGamepad {
		public static final int BUTTON_A = 0;
		public static final int BUTTON_B = 1;
		public static final int BUTTON_X = 2;
		public static final int BUTTON_Y = 3;
		public static final int DPAD = 0;
		public static final int L1 = 4;
		public static final int R1 = 5;
		public static final int SELECT = 6;
		public static final int START = 7;
		public static final int L3 = 8;
		public static final int R3 = 9;
		public static final int L2_TRIGGER = 4; // -1 to 1
		public static final int R2_TRIGGER = 5; // -1 to -1;
		public static final int ANALOG_LEFT_X = 1; //
		public static final int ANALOG_LEFT_Y = 0;
		public static final int ANALOG_RIGHT_X = 3;
		public static final int ANALOG_RIGHT_Y = 2;
		public static final int EXTRA_1 = 10;

		public static boolean isXboxBluetoothGamepad(Controller controller) {
			return controller.getName().toLowerCase().contains("xbox")
					&& controller.getName().toLowerCase().contains("bluetooth")
					&& controller.getName().toLowerCase().contains("gamepad");
		}

		public static KyperControllerMapper getMappings() {
			if (KyperBoxGame.DEBUG_LOGGING) {
				KyperBoxGame.log("ControllerMappings", "Xbox Bluetooth Gamepad Mappings");
			}
			KyperControllerMapper mappings = new KyperControllerMapper();
			mappings.button_a = BUTTON_A;
			mappings.button_b = BUTTON_B;
			mappings.button_x = BUTTON_X;
			mappings.button_y = BUTTON_Y;
			mappings.left_1 = L1;
			mappings.right_1 = R1;
			mappings.select = SELECT;
			mappings.start = START;
			mappings.left_3 = L3;
			mappings.right_3 = R3;
			mappings.left2_trigger = L2_TRIGGER;
			mappings.right2_trigger = R2_TRIGGER;
			mappings.analog_left_x = ANALOG_LEFT_X;
			mappings.analog_left_y = ANALOG_LEFT_Y;
			mappings.analog_right_x = ANALOG_RIGHT_X;
			mappings.analog_right_y = ANALOG_RIGHT_Y;
			mappings.dpad = DPAD;
			mappings.extra1 = EXTRA_1;
			mappings.axis_trigger = true;

			return mappings;
		}

	}

	
	//---------------------------------------
	public static class BluetoothWirelessController {
		public static final int BUTTON_A = 1;
		public static final int BUTTON_B = 0;
		public static final int BUTTON_X = 4;
		public static final int BUTTON_Y = 3;
		public static final int DPAD = 0;
		public static final int L1 = 6;
		public static final int R1 = 7;
		public static final int SELECT = 12;
		public static final int START = 11;
		public static final int L3 = 13;
		public static final int R3 = 14;
		public static final int L2_TRIGGER = 8;
		public static final int R2_TRIGGER = 9;
		public static final int ANALOG_LEFT_X = 2; //
		public static final int ANALOG_LEFT_Y = 3;
		public static final int ANALOG_RIGHT_X = 0;
		public static final int ANALOG_RIGHT_Y = 1;
		public static final int EXTRA1 = 2;

		public static boolean isBluetoothWirelessController(Controller controller) {
			return controller.getName().toLowerCase().contains("bluetooth")
					&& controller.getName().toLowerCase().contains("wireless")
					&& controller.getName().toLowerCase().contains("controller");
		}

		public static KyperControllerMapper getMappings() {
			if (KyperBoxGame.DEBUG_LOGGING) {
				KyperBoxGame.log("ControllerMappings", "Bluetooth Wireless Controller Mappings");
			}
			KyperControllerMapper mappings = new KyperControllerMapper();
			mappings.button_a = BUTTON_A;
			mappings.button_b = BUTTON_B;
			mappings.button_x = BUTTON_X;
			mappings.button_y = BUTTON_Y;
			mappings.left_1 = L1;
			mappings.right_1 = R1;
			mappings.select = SELECT;
			mappings.start = START;
			mappings.left_3 = L3;
			mappings.right_3 = R3;
			mappings.left2_trigger = L2_TRIGGER;
			mappings.right2_trigger = R2_TRIGGER;
			mappings.analog_left_x = ANALOG_LEFT_X;
			mappings.analog_left_y = ANALOG_LEFT_Y;
			mappings.analog_right_x = ANALOG_RIGHT_X;
			mappings.analog_right_y = ANALOG_RIGHT_Y;
			mappings.dpad = DPAD;
			mappings.axis_trigger = false;
			mappings.extra1 = EXTRA1;
			return mappings;
		}
	}

}

package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import java.util.ArrayList;
import java.util.Arrays;

public interface Robot {
    //Game Variables
    double[] servoPositions = {1.0, 0.0};
    ArrayList<String> servoPositionNames = new ArrayList<>(Arrays.asList("Pick Up", "Release"));

    double[] armPositions = {200, 850, 950, 1300}; //HAS TO BE DOUBLE[]
    ArrayList<String> armPositionNames = new ArrayList<>(Arrays.asList("Reset_Arm", "Low_Goal", "Middle_Goal", "High_Goal"));

    double[][] shipping_area = {{0, 0}, {20, 0}, {20, 20}, {0, 20}};
    double[] shipping_hub = {30, 40};

    double robot_width = 15.0; //width = distance along strafing axis
    double robot_length = 15.0; //length = distance along driving axis

    //Robot Hardware

    ArrayList<String> dc_motor_names = new ArrayList<>(Arrays.asList());//"arm", "intake", "duckWheel"));
    double[] max_power = {};//0.5, 0.3, 0.7};
    double[] min_power = {};//-0.13, -0.3, -0.7};
    double[] motor_max_positions = {};//Double.POSITIVE_INFINITY, 1, 1}; //just keep as 1 if encoders disabled
    double[] motor_min_positions = {};//Double.NEGATIVE_INFINITY, -1, -1}; //just keep as -1 if encoders disabled
    int[] dc_motor_directions = {};//1, 1, 1}; //0 for forward, 1 for reverse

    ArrayList<String> servo_names = new ArrayList<>(Arrays.asList("Intake_Servo"));
    double[] servo_max_positions = {1.0};
    double[] servo_min_positions = {0.6};

    ArrayList<String> distance_sensor_names = new ArrayList<>(Arrays.asList());//"sensor_distance"));

    ArrayList<String> touch_sensor_names = new ArrayList<>(Arrays.asList());

    ArrayList<String> color_sensor_names = new ArrayList<>(Arrays.asList());

    ArrayList<String> led_names = new ArrayList<>(Arrays.asList());

    //Driving
    double strafe = 1.0;
    double turning_weight = 1.0;
    double distance_weight = 1.0;
    double distance_weight_two = 1.0; //per inch

    boolean locked_motion = false; //Motion is relative to player, not to robot
    boolean locked_rotation = false; //Rotation is relative to player, not to robot
                                //These must be false if not using PID or RoadRunner

    //PID
    boolean usePID = true;
    double p_weight = 0; //multiplied by difference; 0.025
    double d_weight = 0; //multiplied by derivative of difference (degrees per millisecond); 0.085

    AxesOrder axesOrder = AxesOrder.ZYX;
    boolean invertIMU = false; //you would want to invert if, idk, you put your control hub on upside down
        //by the way, if your control hub is on sideways, you actually have to edit RobotHardware.java

    //Road Runner
    boolean useRoadRunner = false; //can only use ONE of RoadRunner and PID
    double ticks_per_revolution = 0.0; //encoder ticks per revolution - dead wheels
    double wheel_radius = 0.0; //in inches
    double gear_ratio = 0.0; // output (wheel) speed / input (encoder) speed
    double lateral_distance = 0.0;  // inches; distance between the left and right dead wheels
    double forward_offset = 4.0; // inches; offset of the lateral wheel
    boolean integer_overflow = false;
    int[] encoder_directions = {1, 1, 1}; //0 for forward, 1 for reverse

    //Road Runner Tuning
    double forward_multiplier = 1.0;
    double strafing_multiplier = 1.0;
    double turning_multiplier = 1.0;
    //tune these in roadrunner calibration
    //strafing/forward: if we go x inches, multiplier = x / 90
    //turning: if we turn x degrees, multiplier = x / 1800 - roughly
    //kinda guess with these 2; but if we don't go/turn as far, decrease the multiplier, and vice versa
    //more tests and more distance travelled --> more accurate.

    //Tensorflow
    String VUFORIA_KEY =
            "Vuforia Key"; //actually have to get ur own key lol
    String webcam_name = "Webcam 1";
    double camera_zoom = 1.0; //use greater zoom for if the distance is > 50 cm; must be >= 1
    String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
    String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";
    // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
    Float min_confidence = 0.75f;
    int input_size = 300;
    boolean useAsset = true;
    String[] LABELS = {
            "1 Bolt",
            "2 Bulb", 
            "3 Panel"
    };

    //No need to ever change this

    ArrayList<String> encoderNames = new ArrayList<>(Arrays.asList("leftEncoder", "rightEncoder", "frontEncoder"));
    //for RoadRunner

    ArrayList<String> wheel_names = new ArrayList<>(Arrays.asList("rightFront", "rightBack", "leftBack", "leftFront"));
    //wheel order is front right, back right, back left, front left

    ArrayList<String> keys = new ArrayList<>(Arrays.asList(
            "operator a", "operator b", "operator x", "operator y", "operator dpad_up", "operator dpad_down",
            "operator dpad_left", "operator dpad_right", "operator left_bumper", "operator right_bumper",
            "driver a", "driver b", "driver x", "driver y", "driver dpad_up", "driver dpad_down",
            "driver dpad_left", "driver dpad_right", "driver left_bumper", "driver right_bumper",
            "operator left_stick_x", "operator right_stick_x", "operator left_stick_y", "operator right_stick_y",
            "operator left_trigger", "operator right_trigger", "driver left_trigger"
    ));

}
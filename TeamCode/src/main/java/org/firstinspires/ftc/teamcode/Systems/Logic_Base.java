package org.firstinspires.ftc.teamcode.Systems;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logic_Base implements Robot, Constants {

    public RobotHardware robot;

    public HashMap<String, ArrayList<Object>> keybinds = new HashMap<>();
    public String[] button_types = new String[27];

    public double[] times_started = new double[dc_motor_names.size() + servo_names.size() + cr_servo_names.size()]; //in seconds
    public double[] target_positions = new double[dc_motor_names.size() + servo_names.size()];
    public double[] starting_positions = new double[dc_motor_names.size() + servo_names.size()]; //never use for dc_motors

    public int[] key_values = new int[27]; //number of times button/axis is "activated"
    public boolean[] buttons = new boolean[20]; //value of button (True or False)
    public double[] axes = new double[7]; //value of axis (1 for buttons/cycles, -1.0 to 1.0 for everything else)

    public long current_time;
    public long previous_time;

    public Logic_Base(RobotHardware r) {
        robot = r;
        
        for (String servo : servo_names) {
            keybinds.put(servo, new ArrayList<>());
        }
        for (String motor : dc_motor_names) {
            keybinds.put(motor, new ArrayList<>());
        }
        for (String cr_servo : cr_servo_names) {
            keybinds.put(cr_servo, new ArrayList<>());
        }
    }

    public void execute_controllers(Gamepad gamepad1, Gamepad gamepad2) {
        drive(gamepad1);
        update_buttons(gamepad1, gamepad2);
        update_robot();
    }

    public void update_button(boolean button_pressed, String button_name) {
        boolean button_active;
        int temp = keys.indexOf(button_name);
        if (("toggle").equals(button_types[temp])) {
            key_values[temp] += (button_pressed == (key_values[temp] % 2 == 0)) ? 1 : 0;
            button_active = (key_values[temp] % 4 != 0);
        } else if (("default").equals(button_types[temp])) {
            button_active = button_pressed;
        } else {
            button_active = (key_values[temp] % 2 == 0) && (button_pressed);
            key_values[temp] += (button_pressed == (key_values[temp] % 2 == 0)) ? 1 : 0;
        }
        buttons[temp] = button_active;
    }

    public void update_axis(double axis, String axis_name) {
        double axis_value;
        int temp = keys.indexOf(axis_name);
        if (("toggle").equals(button_types[temp])) {
            key_values[temp] += ((Math.abs(axis) > 0.1) == (key_values[temp] % 2 == 0)) ? 1 : 0;
            axis_value = key_values[temp] % 4 != 0 ? 1 : 0;
        } else if (("default").equals(button_types[temp])) {
            axis_value = axis;
        } else {
            axis_value = (key_values[temp] % 2 == 0) && (Math.abs(axis) > 0.1) ? 1 : 0;
            key_values[temp] += ((Math.abs(axis) > 0.1) == (key_values[temp] % 2 == 0)) ? 1 : 0;
        }
        axes[temp - 20] = axis_value;
    }

    public void update_buttons(Gamepad gamepad1, Gamepad gamepad2) {
        update_button(gamepad1.a, "driver a");
        update_button(gamepad1.b, "driver b");
        update_button(gamepad1.x, "driver x");
        update_button(gamepad1.y, "driver y");
        update_button(gamepad1.dpad_up, "driver dpad_up");
        update_button(gamepad1.dpad_down, "driver dpad_down");
        update_button(gamepad1.dpad_left, "driver dpad_left");
        update_button(gamepad1.dpad_right, "driver dpad_right");
        update_button(gamepad1.left_bumper, "driver left_bumper");
        update_button(gamepad1.right_bumper, "driver right_bumper");
        update_axis(gamepad1.left_trigger, "driver left_trigger");

        update_button(gamepad2.a, "operator a");
        update_button(gamepad2.b, "operator b");
        update_button(gamepad2.x, "operator x");
        update_button(gamepad2.y, "operator y");
        update_button(gamepad2.dpad_up, "operator dpad_up");
        update_button(gamepad2.dpad_down, "operator dpad_down");
        update_button(gamepad2.dpad_left, "operator dpad_left");
        update_button(gamepad2.dpad_right, "operator dpad_right");
        update_button(gamepad2.left_bumper, "operator left_bumper");
        update_button(gamepad2.right_bumper, "operator right_bumper");
        update_axis(gamepad2.left_stick_x, "operator left_stick_x");
        update_axis(0 - gamepad2.left_stick_y, "operator left_stick_y"); //negative because down means positive for FTC controllers
        update_axis(gamepad2.right_stick_x, "operator right_stick_x");
        update_axis(0 - gamepad2.right_stick_y, "operator right_stick_y");
        update_axis(gamepad2.left_trigger, "operator left_trigger");
        update_axis(gamepad2.right_trigger, "operator right_trigger");
    }

    public void update_robot() {
        for (Map.Entry<String, ArrayList<Object>> element : keybinds.entrySet()) { //for every element in keybinds

            ArrayList<Object> object_keys = element.getValue(); //object_keys = what the motor maps to

            int number_of_keys = object_keys.size() / 4; //number of keys that map to the motor
            boolean object_is_active = false; //object is active iff at least one key that maps to it is activated

            boolean isDcMotor;
            int key_index;

            for (int i = 0; i < number_of_keys; i++) {
                key_index = keys.indexOf((String) object_keys.get(4 * i));
                object_is_active = ((object_is_active) || ((key_index < 20) && (buttons[key_index])) || ((key_index > 19) && (Math.abs(axes[key_index - 20]) > 0.1)));
            }

            if ((dc_motor_names.contains(element.getKey())) || (servo_names.contains(element.getKey()))) {

                isDcMotor = dc_motor_names.contains(element.getKey());

                int general_list_index;
                int specific_list_index;

                if (isDcMotor) {
                    general_list_index = dc_motor_names.indexOf(element.getKey());
                    specific_list_index = dc_motor_names.indexOf(element.getKey());
                } else {
                    general_list_index = servo_names.indexOf(element.getKey()) + dc_motor_names.size();
                    specific_list_index = servo_names.indexOf(element.getKey());
                }

                if (!object_is_active) { //if we aren't pressing any relevant buttons

                    times_started[general_list_index] = -10.0;

                    if (isDcMotor) {
                        robot.dc_motor_list[specific_list_index].setPower(Math.max(min_power[specific_list_index], Math.min(max_power[specific_list_index], (target_positions[general_list_index] - robot.dc_motor_list[specific_list_index].getCurrentPosition()) * p_weights[specific_list_index])));
                    } else {
                        target_positions[general_list_index] = Math.max(servo_min_positions[specific_list_index], Math.min(servo_max_positions[specific_list_index], target_positions[general_list_index]));
                        robot.servo_list[specific_list_index].setPosition(target_positions[general_list_index]);
                        starting_positions[general_list_index] = target_positions[general_list_index];
                    }
                    
                } else {

                    if (times_started[general_list_index] < 0) //if we're on and it's reset, un-reset it
                        times_started[general_list_index] = (double) System.nanoTime() / 1000000000.0;

                    for (int i = 0; i < number_of_keys; i++) {

                        key_index = keys.indexOf((String) object_keys.get(4 * i)); //where button is in list of keys; < 20 -> button, >= 20 -> axis
                        String type = (String) object_keys.get(4 * i + 1);

                        if ((key_index < 20 && buttons[key_index]) || (key_index > 19 && Math.abs(axes[key_index - 20]) > 0.1)) {
                            if ((type.equals("button")) || (type.equals("cycle"))) {

                                int delta = (int) object_keys.get(4 * i + 2);
                                double[] positions = (double[]) object_keys.get(4 * i + 3);

                                if (positions.length == 1) {
                                    target_positions[general_list_index] = positions[0];
                                } else {
                                    boolean increasing = (positions[1] > positions[0]);
                                    int current_index = 0;
                                    while ((current_index < positions.length) && ((positions[current_index] < target_positions[general_list_index]) || (!increasing)) && ((positions[current_index] > target_positions[general_list_index]) || (increasing))) {
                                        current_index += 1;
                                    }
                                    if ((delta > 0) && ((current_index + 1 > positions.length)  || (positions[current_index] != target_positions[key_index]))) {
                                        current_index -= 1;
                                    }
                                    if (type.equals("cycle")) {
                                        if ((current_index + 2 > positions.length) && (delta > 0)) {
                                            current_index = 0;
                                        } else if ((current_index < 1) && (delta < 0)) {
                                            current_index = positions.length - 1;
                                        } else {
                                            current_index = Math.max(0, Math.min(current_index + delta, positions.length - 1));
                                        }
                                    } else {
                                        current_index = Math.max(0, Math.min(current_index + delta, positions.length - 1));
                                    }
                                    target_positions[general_list_index] = positions[current_index]; //change the target position
                                }
                            } else {
                                if (isDcMotor) {
                                    target_positions[general_list_index] = Math.max(Math.min(robot.dc_motor_list[specific_list_index].getCurrentPosition(),
                                        motor_max_positions[specific_list_index]), motor_min_positions[specific_list_index]);

                                    double calculated_power;

                                    if ((type.equals("toggle")) || (key_index < 20)) {
                                        calculated_power = ((double) object_keys.get(4 * i + 3)) * (((String) object_keys.get(4 * i + 2)).equals("normal") ? 1.0 : Math.min(1, ((double) System.nanoTime() / 1000000000.0 - times_started[general_list_index]) / 0.75));
                                    } else {
                                        calculated_power = axes[key_index - 20] * ( //similar to button defaults, except no gradient option
                                                (key_index > 23) ? (double) object_keys.get(4 * i + 2) : //if it's a trigger, then set it to the first val
                                                (axes[key_index - 20] < 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3))
                                        );
                                    }

                                    calculated_power = Math.max(min_power[specific_list_index], Math.min(max_power[specific_list_index], calculated_power));
                                    if ((robot.dc_motor_list[specific_list_index].getCurrentPosition() > motor_max_positions[specific_list_index]) && (calculated_power > 0)) {
                                        calculated_power = Math.max(min_power[specific_list_index], (motor_max_positions[specific_list_index] - robot.dc_motor_list[specific_list_index].getCurrentPosition()) * p_weights[specific_list_index]);
                                    } else if (robot.dc_motor_list[specific_list_index].getCurrentPosition() < motor_min_positions[specific_list_index] && (calculated_power < 0)) {
                                        calculated_power = Math.min((motor_min_positions[specific_list_index] - robot.dc_motor_list[specific_list_index].getCurrentPosition()) * p_weights[specific_list_index], max_power[specific_list_index]);
                                    }
                                    robot.dc_motor_list[specific_list_index].setPower(calculated_power);
                                } else {
                                    if ((type.equals("toggle")) || (key_index < 20)) {
                                        target_positions[general_list_index] = starting_positions[general_list_index] + (double) object_keys.get(4 * i + 2) * ((double) System.nanoTime() / 1000000000.0 - times_started[general_list_index]);
                                    } else {
                                        target_positions[general_list_index] += //the expression below is seconds/tick, basically; current pos + seconds/tick * depth * angles/second
                                            ((double) System.nanoTime() / 1000000000.0 - times_started[general_list_index]) * axes[key_index - 20] * (
                                                (key_index > 23) ? (double) object_keys.get(4 * i + 2) : //if it's a trigger, then set it to the first val
                                                (axes[key_index - 20] < 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3))
                                            );
                                        times_started[general_list_index] = (double) System.nanoTime() / 1000000000.0;
                                    }
                                    target_positions[general_list_index] = Math.max(servo_min_positions[specific_list_index], Math.min(servo_max_positions[specific_list_index], target_positions[general_list_index]));
                                    robot.servo_list[specific_list_index].setPosition(target_positions[general_list_index]);
                                }
                            }
                        }
                    }
                }
            } else if (cr_servo_names.contains(element.getKey())) {
                int index = cr_servo_names.indexOf(element.getKey());
                if (!object_is_active) {
                    robot.cr_servo_list[index].setPower(0);
                    times_started[target_positions.length + index] = -10;
                } else {
                    if (times_started[target_positions.length + index] < 0) {
                        times_started[target_positions.length + index] = (double) System.nanoTime() / 1000000000.0;
                    }
                    for (int i = 0; i < number_of_keys; i++) {

                        key_index = keys.indexOf((String) object_keys.get(4 * i)); //where button is in list of keys; < 20 -> button, >= 20 -> axis
                        String type = (String) object_keys.get(4 * i + 1);

                        if ((key_index < 20 && buttons[key_index]) || (key_index > 19 && Math.abs(axes[key_index - 20]) > 0.1)) {

                            if ((type.equals("toggle")) || (key_index < 20)) {
                                robot.cr_servo_list[index].setPower(((double) object_keys.get(4 * i + 3)) * (((String) object_keys.get(4 * i + 2)).equals("normal") ? 1.0 : Math.min(1, ((double) System.nanoTime() / 1000000000.0 - times_started[target_positions.length + index]) / 0.75)));
                            } else {
                                robot.cr_servo_list[index].setPower(axes[key_index - 20] * ( //similar to button defaults, except no gradient option
                                    (key_index > 23) ? (double) object_keys.get(4 * i + 2) : //if it's a trigger, then set it to the first val
                                    (axes[key_index - 20] < 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3))
                                ));
                            }
                        }
                    }
                }
            }
        }
    }

    //Driving

    public void drive(Gamepad gamepad) {
        double LX = gamepad.left_stick_x;
        double LY = -gamepad.left_stick_y;
        double RX = gamepad.left_stick_x;
        double RY = -gamepad.left_stick_y;

        double turnAmount = -gamepad.left_trigger*TRIGGER_SENSITIVITY + gamepad.right_trigger*TRIGGER_SENSITIVITY - ((gamepad.left_bumper) ? BUTTON_SENSITIVITY : 0) + (gamepad.right_bumper ? BUTTON_SENSITIVITY : 0);

        double[] vec = {
                LX*LEFT_SENSITIVITY + RX*RIGHT_SENSITIVITY,
                LY*LEFT_SENSITIVITY + RY*RIGHT_SENSITIVITY
        };

        drive(vec, turnAmount);
    }

    /**
     *
     * @param vec array of len 2 of the vector that the drive is on. x and y must not be > 1. [x,y]
     * @param turnAmount the power given to turning changes. left is negative
     */
    /**
     *
     * @param vec array of len 2 of the vector that the drive is on. x and y must not be > 1. [x,y]
     * @param turnAmount the power given to turning changes. left is negative
     */
    public void drive( double[] vec, double turnAmount) {

        if (vec.length !=2 ) {
            throw new IllegalArgumentException("drive function vec len > 2");
        }

        double x = vec[0];
        double y = vec[1];

        double[] powers = new double[4];

        // rightFront
        powers[0] = y-x;
        // rightBack
        powers[1] = y+x;
        // leftBack
        powers[2] = y-x;
        // leftFront
        powers[3] = y+x;

        powers[0]-=turnAmount;
        powers[1]-= turnAmount;
        powers[2]+=turnAmount;
        powers[3]+=turnAmount;

        if (max(powers) > 1) {
            double max = max(powers);
            for (int i = 0; i < powers.length; i++) {
                powers[i]/=max;
            }
        }

        for (int i =0; i<4; i++) robot.wheel_list[i].setPower(powers[i]);
    }

    /**
     * finds the greatest absolute val in the arr
     */
    private double max(double[] arr) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            max = Math.max(max, Math.abs( arr[i]));
        }

        return max;
    }

    public double modifiedAngle(double radians) {
        while (radians > Math.PI) {
            radians -= 2 * Math.PI;
        }
        while (radians < 0 - Math.PI) {
            radians += 2 * Math.PI;
        }
        return radians;
    }

    public void new_keybind(String motor, String button, Object modifier1, Object modifier2, Object modifier3) {
        Object temp2;
        if (!keybinds.containsKey(motor)) throw new IllegalArgumentException("You misspelled " + motor + " - make sure its exactly as it's spelled in dc motor list or servo list, or it's \"goto\". Idiot");
        if (!(keys.contains(button))) throw new IllegalArgumentException("You misspelled " + button + "  - make sure its exactly as it's spelled in keys. ");
        if (keybinds.get(motor).contains((Object) button)) throw new IllegalArgumentException("You can't have \"" + button + "\" have 2 different functions for the same motor and button combination. The motor is " +  motor + ". ");

        if (dc_motor_names.contains(motor) || cr_servo_names.contains(motor) || servo_names.contains(motor)) {
            if (button_types[keys.indexOf(button)] == null) {
                button_types[keys.indexOf(button)] = (String) modifier1;
                if (modifier1.equals("cycle")) {
                    button_types[keys.indexOf(button)] = "button";
                }
            } else if (!button_types[keys.indexOf(button)].equals((String) modifier1)) {
                //if already set to something different
                if (!(modifier1.equals("cycle") && button_types[keys.indexOf(button)].equals("button"))) {
                    throw new IllegalArgumentException("A button cannot have 2 types; however, you are setting \"" + button +
                            "\" to be both a " + button_types[keys.indexOf(button)] + " and a button. (\"goto\" is, by default, a button) ");
                }
            }

            if (((String) modifier1).equals("button") || ((String) modifier1).equals("cycle")) {
                if (cr_servo_names.contains(motor)) throw new IllegalArgumentException("You can't set positions to Continuous Rotation Servos");
                try {
                    temp2 = (int) modifier2;
                } catch(ClassCastException e) {
                    throw new IllegalArgumentException("Increments have to be by an integer amount. Error was on key " + button + ". ");
                }
                try {
                    temp2 = (double[]) modifier3;
                } catch(ClassCastException e) {
                    throw new IllegalArgumentException("The positions list has to be one of doubles, even if it's a dc motor positions list. Error was on key " + button + ". ");
                }
            } else if (((String) modifier1).equals("toggle") || ((String) modifier1).equals("default")) {
                if (((String) modifier1).equals("default") && (keys.indexOf((String) button) > 19)) {
                    try {
                        temp2 = (double) modifier2;
                        temp2 = (double) modifier3;
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Power has to be a double. Error was on key " + button + ". ");
                    }
                    if ((Math.max(Math.abs((double) modifier2), Math.abs((double) modifier3)) > 1) && dc_motor_names.contains(motor)) {
                        throw new IllegalArgumentException("DC Motor Power has to be between -1.0 and 1.0. Error was on key " + button + ". ");
                    }
                } else if (servo_names.contains(motor)) { //servo, default or toggle
                    try {
                        temp2 = (double) modifier2;
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Power has to be a double. Error was on key " + button + ". ");
                    }
                } else {
                    try {
                        temp2 = (String) modifier2;
                        if (!((String) modifier2).equals("normal") && !((String) modifier2).equals("gradient")) {
                            throw new ClassCastException();
                        }
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Button type has to be \"normal\" or \"gradient\". Error was on key " + button + ". ");
                    }
                    try {
                        temp2 = (double) modifier3;
                        if (Math.abs((double) modifier3) > 1) {
                            throw new ClassCastException();
                        }
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Power has to be a double between -1.0 and 1.0. Error was on key " + button + ". ");
                    }
                }
            } else {
                throw new IllegalArgumentException("You misspelled " + modifier1 + " in key " + button + " - make sure its \"default\", \"button\", \"cycle\" or \"toggle\".");
            }
        }
        keybinds.get(motor).add((Object) button);
        keybinds.get(motor).add(modifier1);
        keybinds.get(motor).add(modifier2);
        keybinds.get(motor).add(modifier3);
    }

    public void set_button_types() {
        for (int i = 0; i < 27; i++) {
            if (button_types[i] == null) {
                button_types[i] = "default";
            }
        }
    }
}
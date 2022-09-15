package org.firstinspires.ftc.teamcode.Systems.Autonomous;

import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

public class Threaded_Servo extends Thread {

    RobotHardware robot;
    int servo_index;
    double target_position;

    public Threaded_Servo(RobotHardware r, String servo_name) {
        robot = r;
        servo_index = robot.servo_names.indexOf(servo_name);
    }

    public void set_position(double p) {
        target_position = p;
    }

    public boolean should_be_running = true;

    public void run() {
        while (should_be_running) {
            robot.servo_list[servo_index].setPosition(target_position);
        }
    }

}
package org.firstinspires.ftc.teamcode.Systems.Autonomous;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

public class Threaded_Motor extends Thread {

    RobotHardware robot;
    int motor_index;
    int target_position;
    public boolean isBusy = false;
    long startTime = 0;
    long delay = 0;
    long currentTime = System.nanoTime();

    public Threaded_Motor(RobotHardware r, String motor_name) {
        robot = r;
        motor_index = robot.dc_motor_names.indexOf(motor_name);
    }

    public boolean should_be_running = true;

    public void set_position(int p) {
        target_position = p;
    }

    public void reset() {
        robot.dc_motor_list[motor_index].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void delay(int milliseconds) {
        delay = milliseconds * 1000000L;
        startTime = System.nanoTime();
    }

    public void run() {
        reset();
        while (should_be_running) {
            currentTime = System.nanoTime();
            if (currentTime > startTime + delay) {
                isBusy = (Math.abs(target_position - robot.dc_motor_list[motor_index].getCurrentPosition()) < 5);
                robot.dc_motor_list[motor_index].setPower(Math.max(-0.5, Math.min(0.5, 0.05 *
                        (target_position - robot.dc_motor_list[motor_index].getCurrentPosition())
                )));
            } else {
                isBusy = true;
                robot.dc_motor_list[motor_index].setPower(0);
            }
        }
    }

}
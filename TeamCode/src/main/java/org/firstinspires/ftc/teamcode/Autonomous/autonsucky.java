package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Systems.*;

@Autonomous(name = "auto n nbad")
public class autonsucky extends LinearOpMode implements Robot {

    RobotHardware robot = new RobotHardware();

    double current_time = System.currentTimeMillis();
    double previous_time = System.currentTimeMillis();

    double current_error = 0.0;
    double previous_error = 0.0;

    double error = 0;
    double target = Math.PI / 2.0;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap, telemetry);

        waitForStart();

        target = Math.PI / 2.0; //Turn 90 degrees right

        double t = System.currentTimeMillis() / 1000.0;
        speedSet(0.5);
        while (System.currentTimeMillis() / 1000.0 - t < 5) { //ONE degree
            idle();
        }
        speedSet(0);

        stop();
    }

    public void speedSet(double speed) {
        for (DcMotor dc : robot.wheel_list) {
            dc.setPower(speed);
        }
    }

}
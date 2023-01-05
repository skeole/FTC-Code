package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

@Autonomous(name = "Autonomous 201")
public class Auton201 extends LinearOpMode {

    RobotHardware r = new RobotHardware();

    @Override
    public void runOpMode() throws InterruptedException {
        r.init(hardwareMap, telemetry);

        waitForStart();

        while (opModeIsActive()) {
            setSpeed(0.45);
            double time = System.currentTimeMillis() / 1000.0;
            while (System.currentTimeMillis() / 1000.0 - time < 3) {
                idle();
            }
            setSpeed(0);
            stop();
            idle();
        }

        stop();

        //throw new IllegalArgumentException("lol");
        //a.quit();
        //stop();
    }

    public void setSpeed(double power) {
        for (int i = 0; i < 3; i++) {
            r.wheel_list[i].setPower(power);
        }
    }

}
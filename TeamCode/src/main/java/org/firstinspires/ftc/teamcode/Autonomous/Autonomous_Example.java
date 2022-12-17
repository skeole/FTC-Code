package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Systems.Autonomous.*;
import org.firstinspires.ftc.teamcode.Systems.*;

class Auton extends Thread {

    RobotHardware rh;
    Tensorflow tf;

    ThreadedMotor motor1;
    ThreadedServo servo1;


    public boolean isRunning = true;

    public void run() { //MAIN FUNCTION

        motor1 = new ThreadedMotor(rh, "motor name");
        servo1 = new ThreadedServo(rh, "servo name");

        motor1.start();

        pause(3000); //suspend reading of code


        motor1.set_position(300);
        servo1.set_position(0.5);
        rh.setPower("motor 2", 0.3);

        waitFor(motor1); //waits for motor1 to finish

        isRunning = false;
    }

    public void quit() {
        motor1.should_be_running = false;
        servo1.should_be_running = false;
        rh.stop();
    }

    public Auton(RobotHardware r) {
        rh = r;
    }


    public Auton(RobotHardware r, Tensorflow t) {
        rh = r;
        tf = t;
    }


    public void waitFor(ThreadedMotor motor) {
        while (motor.isBusy) {
        }
    }

    public void pause(double milliseconds) {
        long t = System.nanoTime();
        while (System.nanoTime() - t <= milliseconds * 1000000) {
        }
    }
}

@Autonomous(name = "Autonomous Example")
public class Autonomous_Example extends LinearOpMode {

    RobotHardware r = new RobotHardware();
    Tensorflow t;
    Auton a;

    @Override
    public void runOpMode() throws InterruptedException {
        r.init(hardwareMap, telemetry);
        t = new Tensorflow(r);

        a = new Auton(r, t);

        waitForStart();

        a.start();

        while ((opModeIsActive()) && (a.isRunning)) {
            idle();
        }

        throw new IllegalArgumentException("lol");
        //a.quit();
        //stop();
    }

}

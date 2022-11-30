package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Systems.RoadRunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

import java.util.Random;

class TeleOp202Logic extends Logic_Base {

    double time_difference = System.currentTimeMillis();
    double tx = 2.0;
    double ty = 0.0;

    double z1 = Math.PI / 180.0 * 8.797411; //around 9 degrees idk
    double z2 = Math.PI / 180.0 * 352; //around 354 degrees idk

    double tpr = 2786.2109868741 / 2.0 / Math.PI;

    Random rand = new Random();

    public void execute_non_driver_controlled() {

        if (rand.nextDouble() > 0.99999) throw new IllegalArgumentException("Sorry, that action is not allowed");

        time_difference = System.currentTimeMillis() - time_difference;
        if (buttons[keys.indexOf("driver dpad_up")])
            ty += time_difference * 0.001;
        if (buttons[keys.indexOf("driver dpad_down")])
            ty -= time_difference * 0.001;
        if (buttons[keys.indexOf("driver dpad_right")])
            tx += time_difference * 0.001;
        if (buttons[keys.indexOf("driver dpad_left")])
            tx -= time_difference * 0.001;

        time_difference = System.currentTimeMillis();

        if (tx <= 0.001) tx = 0.001;

        double magnitude = Math.sqrt(tx * tx + ty * ty);

        if (magnitude > 2) {
            tx /= magnitude / 2.0;
            ty /= magnitude / 2.0;
            magnitude = 2.0;
        }

        double tangle2 = Math.acos(1 - magnitude * magnitude / 2.0); //180 means straight line
        double tangle1 = Math.PI + Math.atan(ty / tx) - tangle2 / 2.0; //0 means straight down
        double tangle3 = Math.PI / 2.0 - tangle2 - tangle1;

        tangle1 -= z1;
        tangle2 -= z2;

        tangle1 *= tpr;
        tangle2 *= tpr;

        target_positions[0] = tangle1; //ticks per radian
        target_positions[1] = target_positions[0];

        target_positions[2] = tangle2;

        robot.telemetry.addData("targetx", tx);
        robot.telemetry.addData("targety", ty);
        //target_positions[3] = tangle3;
        //set the servo to be the arm position

        robot.telemetry.addData("joint1left data", robot.dc_motor_list[0].getCurrentPosition());
        robot.telemetry.addData("joint1left data", target_positions[0]);

        robot.telemetry.addData("joint1right data", robot.dc_motor_list[1].getCurrentPosition());
        robot.telemetry.addData("joint1right data", target_positions[1]);

        //robot.telemetry.addData("joint2 data", robot.dc_motor_list[2].getCurrentPosition());
        //robot.telemetry.addData("joint2 data", target_positions[2]);

        robot.telemetry.update();
        if (useRoadRunner) {
            position_tracker.update();
        }
    }

    //Initialization

    public void init() {
        setZeroAngle(0);
        button_types[keys.indexOf("driver dpad_up")] = "default";
        button_types[keys.indexOf("driver dpad_down")] = "default";
        button_types[keys.indexOf("driver dpad_right")] = "default";
        button_types[keys.indexOf("driver dpad_left")] = "default";
    }

    public void init(StandardTrackingWheelLocalizer localizer) {
        init();
        initializeRoadRunner(45, 100, 90, localizer);
    }

    public void set_keybinds() {

        //new_keybind("claw", "driver a", "cycle", 1, new double[] {0.0, 1.0});
        new_keybind("claw", "driver a", "default", 0.2, "it's great to be a michigan wolverine");
        new_keybind("claw", "driver b", "default", -0.2, "it's great to be a michigan wolverine");

    }

    public TeleOp202Logic(RobotHardware r) {
        super(r);
        set_keybinds();
        set_button_types();
    }
}

@TeleOp(name="TeleOp Team 202", group="Iterative Opmode")
public class TeleOp202 extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    TeleOp202Logic logic = new TeleOp202Logic(robot);
    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap, telemetry);
        waitForStart();
        if (logic.useRoadRunner) {
            StandardTrackingWheelLocalizer localizer = new StandardTrackingWheelLocalizer(hardwareMap);
            logic.init(localizer);
        } else {
            logic.init();
        }
        while (opModeIsActive()) {
            logic.execute_controllers(gamepad1, gamepad2); //driver is gamepad1, operator is gamepad2
            logic.execute_non_driver_controlled();
        }
    }
}
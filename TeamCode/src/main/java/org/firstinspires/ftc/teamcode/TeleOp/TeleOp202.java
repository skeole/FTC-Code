package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Systems.RoadRunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

class TeleOp202Logic extends Logic_Base {

    public void execute_non_driver_controlled() {

        //set the servo to be the arm position
        //do this by resetting the servo target position each loop, NOT by setting the target position of the servo
            //this is because the servo is not in the keybinds list

        //robot.setPosition("arm servo", ahsdjklashd);

        robot.telemetry.update();
        if (useRoadRunner) {
            position_tracker.update();
        }
    }

    //Initialization

    public void init() {
        setZeroAngle(0);
    }

    public void init(StandardTrackingWheelLocalizer localizer) {
        init();
        initializeRoadRunner(45, 100, 90, localizer);
    }

    public void set_keybinds() {

        //arm

        //new_keybind("arm", "operator right_stick_y", "default", 0.26, 0.13);

        //don't include the servo for obvy reasons

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
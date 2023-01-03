package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Systems.RoadRunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

class TeleOp201Logic extends Logic_Base {

    DcMotor helloworld;
    Servo fuckthis;
    double lastpower = 0.6;

    public void execute_non_driver_controlled() {

        robot.telemetry.addData("Angle?", robot.getAngle());
        robot.telemetry.addData("Angle V2", current_angle = 0 - robot.getAngle() - zero_angle); //Only different value if not starting robot straight ahead
                                    //Positive = Rotated clockwise

        robot.telemetry.addData("Power", robot.dc_motor_list[0].getPower());
        robot.telemetry.addData("Servo Power?", robot.cr_servo_list[0].getPower());

        robot.telemetry.update();

        helloworld.setPower(robot.dc_motor_list[0].getPower());

        if (buttons[keys.indexOf("driver a")]) {
            lastpower = 0.6;
        } else if (buttons[keys.indexOf("driver b")]) {
            lastpower = 0.2;
        }

        fuckthis.setPosition(lastpower);

        if (useRoadRunner) {
            position_tracker.update();
        }
    }

    //Initialization

    public void init() {
        setZeroAngle(0);
        helloworld = robot.map.get(DcMotor.class, "Right");
        helloworld.setDirection(DcMotor.Direction.REVERSE);
        fuckthis = robot.map.get(Servo.class, "Scissor");
    }

    public void init(StandardTrackingWheelLocalizer localizer) {
        init();
        initializeRoadRunner(45, 100, 90, localizer);
    }

    public void set_keybinds() {

        // Arm
        new_keybind("Left", "driver dpad_up", "default", "normal", 1.0);

        new_keybind("Left", "driver dpad_down", "default", "normal", 0.1);

        // V4B
        new_keybind("Virtual", "driver y", "default", "normal", 1.0);
        new_keybind("Virtual", "driver x", "default", "normal", -1.0);

        //new_keybind("Scissor", "driver a", "default", 0.3, "what");
        //new_keybind("Scissor", "driver b", "default", -0.3, "the hell");

    }

    public TeleOp201Logic(RobotHardware r) {
        super(r);
        set_keybinds();
        set_button_types();
    }
}

@TeleOp(name="TeleOp", group="Iterative Opmode")
public class TeleOp201 extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    TeleOp201Logic logic = new TeleOp201Logic(robot);
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
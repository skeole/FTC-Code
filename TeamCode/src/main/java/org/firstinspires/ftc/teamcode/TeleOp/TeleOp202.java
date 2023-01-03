package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

class TeleOp202Logic extends Logic_Base {

    double time_difference = System.currentTimeMillis();
    double tx = 2.0;
    double ty = 0.0;

    DcMotor dc = null;

    Servo claw = null;
    Servo clawAligner= null;

    public void execute_non_driver_controlled() {
        time_difference = System.currentTimeMillis() - time_difference;
        if (buttons[keys.indexOf("operator dpad_up")])
            ty += time_difference * 0.002;
        if (buttons[keys.indexOf("operator dpad_down")])
            ty -= time_difference * 0.002;
        if (buttons[keys.indexOf("operator dpad_right")])
            tx += time_difference * 0.002;
        if (buttons[keys.indexOf("operator dpad_left")])
            tx -= time_difference * 0.002;
        if (buttons[keys.indexOf("operator right_bumper")]) {
            claw.setPosition(CLAW_OPEN);
        }
        if (buttons[keys.indexOf("operator left_bumper")]) {
            claw.setPosition(CLAW_CLOSE);
        }

        ty += axes[(keys.indexOf("operator left_stick_y")-20)] * CLAW_ALIGNER_INCREMENTER * time_difference;
        tx += axes[(keys.indexOf("operator left_stick_x")-20)] * CLAW_ALIGNER_INCREMENTER * time_difference;

        if (buttons[keys.indexOf("operator y")]) {
            tx = 1;
            ty = 1.7;
        }

        if (buttons[keys.indexOf("operator x")]) {
            ty = -1.2;
            tx = 1.2;
        }
        if (buttons[keys.indexOf("operator a")]) {
            ty = 1.8;
            tx = 0.8;
        }


        time_difference = System.currentTimeMillis();

        if (ty >= 2) ty = 2;
        if (tx >= 2) tx = 2;
        if (ty < -1.2) ty = -1.2;
        if (tx < 0.5) tx = 0.5;
        double magnitude = Math.sqrt(tx * tx + ty * ty);

        if (magnitude > 2) {
            double ratio =  1.995 / magnitude;
            tx *= ratio;
            ty *= ratio;
            magnitude = Math.sqrt(tx * tx + ty * ty);
        }

        double TARGET_JOINT_2 = Math.acos(1 - magnitude * magnitude / 2.0); //180 means straight line
        double TARGET_JOINT_1 = Math.PI + Math.atan(ty / tx) - TARGET_JOINT_2 / 2.0; //0 means straight down
//        double TARGET_CLAW_ALIGNER = Math.PI / 2.0 - TARGET_JOINT_2 - TARGET_JOINT_1;

        // removing the initial angle
        TARGET_JOINT_1 -= JOINT_1_INITIAL;
        TARGET_JOINT_2 -= JOINT_2_INITIAL;

        // converting to encoder ticks
        TARGET_JOINT_1 *= TICKS_PER_RADIAN;
        TARGET_JOINT_2 *= TICKS_PER_RADIAN;

        target_positions[0] = 0 - TARGET_JOINT_1; //ticks per radian
        dc.setPower(robot.dc_motor_list[0].getPower());


        target_positions[1] = TARGET_JOINT_2;

        if (axes[keys.indexOf("operator left_trigger")-20] > 0.1) {
            clawAligner.setPosition(clawAligner.getPosition() +.005 );
        }
        if (axes[keys.indexOf("operator right_trigger")-20] >0.1) {
            clawAligner.setPosition(clawAligner.getPosition() -.005 );

        }


        robot.telemetry.update();
    }

    public void init() {
        dc = robot.map.get(DcMotor.class, "joint1left");

        button_types[keys.indexOf("driver dpad_up")] = "default";
        button_types[keys.indexOf("driver dpad_down")] = "default";
        button_types[keys.indexOf("driver dpad_right")] = "default";
        button_types[keys.indexOf("driver dpad_left")] = "default";


        claw = robot.map.get(Servo.class, "claw");
        clawAligner = robot.map.get(Servo.class, "clawAligner");

        claw.setPosition(CLAW_OPEN);
        clawAligner.setPosition(0.5);
    }

    public void set_keybinds() {

        new_keybind("claw", "driver x", "default", 0.2, "it's great");
        new_keybind("claw", "driver y", "default", -0.2, "to be");
        new_keybind("clawAligner", "driver a", "default", 0.2, "it's great to be a michigan wolverine");
        new_keybind("clawAligner", "driver b", "default", -0.2, "it's great to be a michigan wolverine");

    }

    public TeleOp202Logic(RobotHardware r) {
        super(r);
        set_keybinds();
        set_button_types();
    }
}

@TeleOp(name="TeleOp Team 202", group="Iterative Opmode")
public class
TeleOp202 extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    TeleOp202Logic logic = new TeleOp202Logic(robot);
    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap, telemetry);
        waitForStart();
            logic.init();
        while (opModeIsActive()) {
            logic.execute_controllers(gamepad1, gamepad2); //driver is gamepad1, operator is gamepad2
            logic.execute_non_driver_controlled();
        }
    }
}
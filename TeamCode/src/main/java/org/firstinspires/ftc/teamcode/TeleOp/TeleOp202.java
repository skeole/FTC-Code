package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Systems.RoadRunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

class TeleOp202Logic extends Logic_Base {

    double time_difference = System.currentTimeMillis();
    double tx = 2.0;
    double ty = 0.0;

    boolean clawOpen = false;

    static double CLAW_OPEN = 1;
    static double CLAW_CLOSE = 0;

    double CLAW_ALIGNER_INCREMENTER=0.01;

    double z1 = Math.PI / 180.0 * 5; 
    double z2 = Math.PI / 180.0 * 354; 

    double tpr = 2786.2109868741 / 2.0 / Math.PI;

    DcMotor dc = null;

    public void execute_non_driver_controlled() {

        double xbefore = tx;
        double ybefore = ty;

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
            clawOpen = true;
            target_positions[dc_motor_names.size() + servo_names.indexOf("claw")] = CLAW_OPEN;
        }
        if (buttons[keys.indexOf("operator left_bumper")]) {
            clawOpen = false;
            target_positions[dc_motor_names.size() + servo_names.indexOf("claw")] = CLAW_CLOSE;
        }

        ty += axes[(keys.indexOf("operator left_stick_y")-20)] * CLAW_ALIGNER_INCREMENTER * time_difference;
        tx += axes[(keys.indexOf("operator left_stick_x")-20)] * CLAW_ALIGNER_INCREMENTER * time_difference;

        time_difference = System.currentTimeMillis();

        if (ty < Math.sqrt(3)) {
            tx = 1;
            if (ty < -1.2) ty = -1.2;
        } else {
            tx = Math.sqrt(4 - ty * ty);
        }

        if (tx <= 0.001) tx = 0.001;

        if (buttons[keys.indexOf("operator y")]) {
            tx = 1;
            ty = 1.7;
        }

        double magnitude = Math.sqrt(tx * tx + ty * ty);

        if (magnitude > 2) {
            tx = xbefore;
            ty = ybefore;
            magnitude = Math.sqrt(tx * tx + ty * ty);
        }

        double tangle2 = Math.acos(1 - magnitude * magnitude / 2.0); //180 means straight line
        double tangle1 = Math.PI + Math.atan(ty / tx) - tangle2 / 2.0; //0 means straight down
        double tangle3 = Math.PI / 2.0 - tangle2 - tangle1;

        // removing the initial angle
        tangle1 -= z1;
        tangle2 -= z2;

        // converting to encoder ticks
        tangle1 *= tpr;
        tangle2 *= tpr;

        target_positions[0] = 0 - tangle1; //ticks per radian
        dc.setPower(robot.dc_motor_list[0].getPower());


        target_positions[1] = tangle2;

        if (axes[keys.indexOf("operator left_trigger")-20] > 0.1) {
            target_positions[2]+= axes[keys.indexOf("operator left_trigger")-20] * CLAW_ALIGNER_INCREMENTER;
        }
        if (axes[keys.indexOf("operator right_trigger")-20] >0.1) {
            target_positions[2]-= axes[keys.indexOf("operator right_trigger")-20] * CLAW_ALIGNER_INCREMENTER;
        }

        robot.telemetry.addData("targetx", tx);
        robot.telemetry.addData("targety", ty);
        robot.telemetry.addData("clawAligner", target_positions[2]);

        robot.telemetry.update();
        if (useRoadRunner) {
            position_tracker.update();
        }

    }

    //Initialization

    public void init() {
        setZeroAngle(0);
        dc = robot.map.get(DcMotor.class, "joint1left");
        try {
            button_types[keys.indexOf("driver dpad_up")] = "default";
            button_types[keys.indexOf("driver dpad_down")] = "default";
            button_types[keys.indexOf("driver dpad_right")] = "default";
            button_types[keys.indexOf("driver dpad_left")] = "default";
        }
        catch (Exception e) {
            throw new IllegalArgumentException("it was in the init of logic");
        }
    }

    public void init(StandardTrackingWheelLocalizer localizer) {
        init();
        initializeRoadRunner(45, 100, 90, localizer);
    }

    public void setKeybinds() {

    }

    public TeleOp202Logic(RobotHardware r) {
        super(r);
        setKeybinds();
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
package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Systems.RoadRunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

@TeleOp(name="TeleOp Testing", group="Iterative Opmode")
public class TeleOp201Testing extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap, telemetry);
        waitForStart();
        while (opModeIsActive()) {
            robot.telemetry.addData("Left DCM", robot.dc_motor_list[0].getCurrentPosition());
            robot.telemetry.update();
        }
    }
}
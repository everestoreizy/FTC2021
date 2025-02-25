package org.eps.Deprecated;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class HardwareWithEncoders {

    //Globals
    static final double     COUNTS_PER_MOTOR_REV    = 520 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 6.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    //Drive train
    public DcMotor mLF = null;
    public DcMotor mLB = null;
    public DcMotor mRF = null;
    public DcMotor mRB = null;

    //Claw
    public Servo sCL = null;
    public Servo sCR = null;

    //Carousel
    public DcMotor mC = null;

    //Arm
    public DcMotor mARM = null;

    //All motors
    public DcMotor [] mALL;

    double [] rotationArray;

    HardwareMap hMAP = null;
    private ElapsedTime period = new ElapsedTime();

    //Global speed values
    double mCpower = 0.1; //Carousel power

    //Constructor
    public HardwareWithEncoders() {
        //Nothing
    }

    public void init(HardwareMap ahwmap) {
        //Save references to hardware map
        hMAP = ahwmap;

        //Define and init motors
        mLF = hMAP.dcMotor.get("LF");
        mRF = hMAP.dcMotor.get("RF");
        mLB = hMAP.dcMotor.get("LB");
        mRB = hMAP.dcMotor.get("RB");

        mC = hMAP.dcMotor.get("CAROUSEL");

        mARM = hMAP.dcMotor.get("ARM");

        sCL = hMAP.servo.get("GLEFT");
        sCR = hMAP.servo.get("GRIGHT");

        mALL = new DcMotor[]{ mLF, mRF, mLB, mRB, mC, mARM };

        mLB.setDirection(DcMotor.Direction.REVERSE);
        mRB.setDirection(DcMotor.Direction.FORWARD);
        mLF.setDirection(DcMotor.Direction.FORWARD);
        mRF.setDirection(DcMotor.Direction.FORWARD);

        for (DcMotor m : mALL) {
            m.setPower(0.0);
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

    }

    public double[] getDrivePowersFromAngle(double angle) {
        double[] unscaledPowers = new double[4];
        unscaledPowers[0] = Math.sin(angle + Math.PI / 4);
        unscaledPowers[1] = Math.cos(angle + Math.PI / 4);
        unscaledPowers[2] = unscaledPowers[1];
        unscaledPowers[3] = unscaledPowers[0];
        return unscaledPowers;
    }

    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            //do nothing
        }
    }

    //Get motor encoder positions
    public int gLF(){
        return mLF.getCurrentPosition();
    }

    public int gRF(){
        return mRF.getCurrentPosition();
    }

    public int gLB(){
        return mLB.getCurrentPosition();
    }

    public int gRB(){
        return mRB.getCurrentPosition();
    }

    public int gC() { return mC.getCurrentPosition(); }

    public int gARM() { return mARM.getCurrentPosition(); }

    //Set all drive train motors to run to a certain position.
    public void edrive(int posLF, int posRF, int posLB, int posRB) {
        mLF.setTargetPosition(gLF() + posLF);
        mRF.setTargetPosition(gRF() + posRF);
        mLB.setTargetPosition(gLB() + posLB);
        mRB.setTargetPosition(gRB() + posRB);
    }

    public void edriveOne(int ticks){
        edrive(ticks, ticks, ticks, ticks);
    }

    public void eturn(int ticks) {
        mLF.setTargetPosition(gLF() - ticks);
        mRF.setTargetPosition(gRF() + ticks);
        mLB.setTargetPosition(gLB() - ticks);
        mRB.setTargetPosition(gRB() + ticks);
    }

    public void estrafe(int ticks) {
        edrive(gLF() + ticks,
                gRF() -ticks,
                gLB() -ticks,
                gRB() + ticks);
    }

    public void ecarousel(int ticks) {
        mC.setTargetPosition(gC() + ticks);
    }

    public void eclaw(double leftPosition, double rightPosition){
        sCL.setPosition(leftPosition);
        sCR.setPosition(rightPosition);
    }

    public void earm(int ticks){
        mARM.setTargetPosition(gARM() + ticks);
    }
}

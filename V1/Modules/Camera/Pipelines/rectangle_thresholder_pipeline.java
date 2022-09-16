package org.firstinspires.ftc.teamcode.V1.Modules.Camera.Pipelines;

import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class rectangle_thresholder_pipeline extends OpenCvPipeline {

    private String out;
    public Scalar lowerRed = new Scalar(0, 0, 0);
    public Scalar upperRed = new Scalar(255, 255, 255);

    public Scalar lowerBlue = new Scalar(0, 0, 0);
    public Scalar upperBlue = new Scalar(255, 255, 255);

    public Scalar lowerGreen = new Scalar(0, 0, 0);
    public Scalar upperGreen = new Scalar(255, 255, 255);

    private boolean saveImg = false;
    private Mat hsvMat = new Mat();
    private Mat maskedInputMat = new Mat();
    private Mat binaryMat = new Mat();
    private Point topLeft1 = new Point(10, 0), bottomRight1 = new Point(40, 20); // Analyzed area is constant

  /*public rectangle_thresholder_pipeline(Telemetry telemetry) {
    this.telemetry = telemetry;
    saveImg = false;
  }*/

    public rectangle_thresholder_pipeline() {
        saveImg = false;
    }

    public rectangle_thresholder_pipeline(boolean s) {
        saveImg = s;
    }

    // GAME SPECIFIC CODE
    public double thresholdColor(Mat input, Scalar lower, Scalar upper){
        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvMat, lower, upper, binaryMat);
        Core.bitwise_and(input, input, maskedInputMat, binaryMat);

        double w1 = 0, w2 = 0;
        // process the pixel value for each rectangle  (255 = W, 0 = B)
        for (int i = (int) topLeft1.x; i <= bottomRight1.x; i++) {
            for (int j = (int) topLeft1.y; j <= bottomRight1.y; j++) {
                if (binaryMat.get(i, j)[0] == 255) {
                    w1++;
                }
            }
        }

        return w1;
    }

    @Override
    public Mat processFrame(Mat input) {
        double winR, winB, winG;
        winR = thresholdColor(input, lowerRed, upperRed);
        winB = thresholdColor(input, lowerBlue, upperBlue);
        winG = thresholdColor(input, lowerGreen, upperGreen);

        double best = Math.max(Math.max(winB, winG), winR);

        if(best == winR)
            out = "red";
        else if(best ==  winB)
            out = "blue";
        else
            out = "green";

        if (saveImg) {
            // saveMatToDisk(input, "rect_manual_img");
            saveImg = false;
            //saveMatToDisk(input, "rect_manual_img");
        }

        return binaryMat;
    }

    public String getOut() {
        return out;
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.image.NIVision.MeasurementType;
import edu.wpi.first.wpilibj.image.NIVision.Rect;
import edu.wpi.first.wpilibj.templates.subsystems.CameraGimble;
import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 * @author Taylor McCorkill :)
 */
public class TargetingCode {
    
//    private final int redMIN = 230; 
//    private final int redMAX = 255;
//    private final int greenMIN = 40;
//    private final int greenMAX = 140;
//    private final int blueMIN = 40;
//    private final int blueMAX = 140;
    
    private final int redMIN = 200; 
    private final int redMAX = 255;
    private final int greenMIN = 200; //(int) (100*DriverStation.getInstance().getAnalogIn(1));
    private final int greenMAX = 255; //(int) (100*DriverStation.getInstance().getAnalogIn(2));
    private final int blueMIN = 125;  //(int) (100*DriverStation.getInstance().getAnalogIn(3));
    private final int blueMAX = 255; //(int) (100*DriverStation.getInstance().getAnalogIn(4));

    final int XMAXSIZE = 24;
    final int XMINSIZE = 24;
    final int YMAXSIZE = 24;
    final int YMINSIZE = 48;
    final double xMax[] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
    final double xMin[] = {.4, .6, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, 0.6, 0};
    final double yMax[] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
    final double yMin[] = {.4, .6, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
        .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
        .05, .05, .6, 0};
    final int RECTANGULARITY_LIMIT = 60;
    final int ASPECT_RATIO_HIGH_LIMIT = 75;
    
    final int X_EDGE_LIMIT = 40;
    final int Y_EDGE_LIMIT = 60;
    final int X_IMAGE_RES = 320;          //X Image resolution in pixels, should be 160, 320 or 640
//    final double VIEW_ANGLE = 43.5;       //Axis 206 camera
    final double VIEW_ANGLE = 48;       //Axis M1011 camera
    AxisCamera camera;          // the axis camera object (connected to the switch)
    CriteriaCollection cc;      // the criteria for doing the particle filter operation
    
    int correctImage;
    double currentXImage;
    double currentYImage;
    int goalScore;
    final double buffer = 0.1;
    final double rangeBuffer =  6.0;
    final double optimalHighRange = 146.0;
    final double optimalMidRange = 135.0;
    final double MOTOR_SPEED = 0.5;
   
    public class Scores {

        double rectangularity;
        double aspectRatioInner;
        double aspectRatioOuter;
        double xEdge;
        double yEdge;
        
    }

    public TargetingCode() {
        camera = AxisCamera.getInstance();  // get an instance of the camera
        cc = new CriteriaCollection();      // create the criteria for the particle filter
        cc.addCriteria(MeasurementType.IMAQ_MT_AREA, 500, 65535, false);
    }

    public double target() {
        double horizontalDistance = 0.0;
        try {
            /**
             * Do the image capture with the camera and apply the algorithm
             * described above. This sample will either get images from the
             * camera or from an image file stored in the top level directory in
             * the flash memory on the cRIO. The file name in this case is
             * "testImage.jpg"
             *
             */
            ColorImage image = camera.getImage();     // comment if using stored images
            // ColorImage image;                           // next 2 lines read image from flash on cRIO
            //image = new RGBImage("/testImage.jpg");		// get the sample image from the cRIO flash
           // BinaryImage thresholdImage = image.thresholdHSV(60, 100, 90, 255, 20, 255);   // keep only red objects
            //BinaryImage thresholdImage = image.thresholdHSV (100, 140, 220, 255, 104, 120); // DOES NOT WORK, DO NOT USE
//            BinaryImage thresholdImage = image.thresholdRGB (0, 30, 180, 255, 180, 255); //reads green light
            BinaryImage thresholdImage = image.thresholdRGB ( redMIN, redMAX, greenMIN, greenMAX, blueMIN, blueMAX); //reads red light
            thresholdImage.write("/threshold.bmp");
            BinaryImage convexHullImage = thresholdImage.convexHull(false);          // fill in occluded rectangles
            convexHullImage.write("/convexHull.bmp");
            BinaryImage filteredImage = convexHullImage.particleFilter(cc);           // filter out small particles
            filteredImage.write("/filteredImage.bmp");
            

            //iterate through each particle and score to see if it is a target
            Scores scores[] = new Scores[filteredImage.getNumberParticles()];
            System.out.println("scores.length = " + scores.length);
            for (int i = 0; i < scores.length; i++) {
                ParticleAnalysisReport report = filteredImage.getParticleAnalysisReport(i);
                scores[i] = new Scores();

                scores[i].rectangularity = scoreRectangularity(report);
                scores[i].aspectRatioOuter = scoreAspectRatio(filteredImage, report, i, true);
                scores[i].aspectRatioInner = scoreAspectRatio(filteredImage, report, i, false);
                scores[i].xEdge = scoreXEdge(thresholdImage, report);
                scores[i].yEdge = scoreYEdge(thresholdImage, report);

                if (scoreCompare(scores[i], false)) {
                    System.out.println("Particle: " + i + " is a High Goal  centerX: " + report.center_mass_x_normalized
                            + "centerY: " + report.center_mass_y_normalized);
                    horizontalDistance = computeDistance(thresholdImage, report, i, false);
                    System.out.println("Distance: " + computeDistance(thresholdImage, report, i, false));
                    
                    currentXImage = report.center_mass_x_normalized;
                    currentYImage = report.center_mass_y_normalized;
                    goalScore = 1;
//                    correctImage = i;
                } 
                else 
                    if (scoreCompare(scores[i], true)) {
                        System.out.println("Particle: " + i + " is a Middle Goal  centerX: " + report.center_mass_x_normalized
                                + "centerY: " + report.center_mass_y_normalized);
                        System.out.println("  Distance: " + computeDistance(thresholdImage, report, i, true));
                        horizontalDistance = computeDistance(thresholdImage, report, i, true);
                        currentXImage = report.center_mass_x_normalized;
                        currentYImage = report.center_mass_y_normalized;
                        goalScore = 2;
//                        correctImage = i;
                    }   
                    else {
                        System.out.println("Particle: " + i + " is not a goal centerX: " + report.center_mass_x_normalized
                                + "centerY: " + report.center_mass_y_normalized);
                    }
                
                System.out.println("  Rect: " + scores[i].rectangularity + " ARinner: " + scores[i].aspectRatioInner);
                System.out.println("  AspectRatioOuter: " + scores[i].aspectRatioOuter + " xEdge: " + scores[i].xEdge
                        + "yEdge: " + scores[i].yEdge);
                
//                correctImage = i;
//                currentImage = report.center_mass_x_normalized;
                
            }

            /**
             * all images in Java must be freed after they are used since they
             * are allocated out of C data structures. Not calling free() will
             * cause the memory to accumulate over each pass of this loop.
             */
            filteredImage.free();
            convexHullImage.free();
            thresholdImage.free();
            image.free();

        } catch (AxisCameraException ex) {        // this is needed if the camera.getImage() is called
            ex.printStackTrace();
            System.out.println("Something broke. Axis camera exception");
        } catch (NIVisionException ex) {
            ex.printStackTrace();
        }
        return horizontalDistance;
    }
    
    public void CenterBot() {
        
//        System.out.println("Method 'CenterBot' is being called");
        
//        final double MOTOR_SPEED = 0.3;
                
        if (RobotTemplate.isXCentered == false) {
            target();

            if (currentXImage > buffer) {
                RobotTemplate.leftMotor.set(-MOTOR_SPEED);
                RobotTemplate.rightMotor.set(MOTOR_SPEED);
                //RobotTemplate.cameraHorizontalAngle += CameraGimble.servoRatio;
            }


            else if (currentXImage < -buffer) {
                RobotTemplate.leftMotor.set(MOTOR_SPEED);
                RobotTemplate.rightMotor.set(-MOTOR_SPEED);
                //RobotTemplate.cameraHorizontalAngle -= CameraGimble.servoRatio;
            }

            else if ((currentXImage > -buffer) && (currentXImage < buffer)) {
                System.out.println("Robot is centered");
                RobotTemplate.leftMotor.set(0.0);
                RobotTemplate.rightMotor.set(0.0);
                RobotTemplate.isXCentered = true;
            }
        }
    }

//    public void Angle() {
//        
////        final double MOTOR_SPEED = 0.3;
//                
//        if (RobotTemplate.isYCentered == false) {
//            target();
//
//            if (currentYImage > buffer) {
//                //RobotTemplate.leftMotor.set(-MOTOR_SPEED);
//                //RobotTemplate.rightMotor.set(MOTOR_SPEED);
//                RobotTemplate.cameraVerticalAngle += CameraGimble.servoRatio;
//            }
//
//
//            else if (currentYImage < -buffer) {
//                //RobotTemplate.leftMotor.set(MOTOR_SPEED);
//                //RobotTemplate.rightMotor.set(-MOTOR_SPEED);
//                RobotTemplate.cameraVerticalAngle -= CameraGimble.servoRatio;
//            }
//
//            else if ((currentYImage > -buffer) && (currentYImage < buffer)) {
//                System.out.println("Camera in Buffer Zone");
//                //RobotTemplate.leftMotor.set(0.0);
//                //RobotTemplate.rightMotor.set(0.0);
//                RobotTemplate.isYCentered = true;
//            }
//        }  
//    }
    public void HorizontalDistance()
    {
        if(RobotTemplate.isInRange == false)
        {
            target();
            if(goalScore == 1)
            {
                if(target() > optimalHighRange + rangeBuffer)
                {
                    RobotTemplate.rightMotor.set(MOTOR_SPEED);
                    RobotTemplate.leftMotor.set(MOTOR_SPEED);
                }
            
                if(target() < optimalHighRange - rangeBuffer)
                {
                    RobotTemplate.rightMotor.set(-MOTOR_SPEED);
                    RobotTemplate.leftMotor.set(-MOTOR_SPEED); 
                }
            
                if((target() < optimalHighRange + rangeBuffer) && (target() > optimalHighRange - rangeBuffer))
                {
                    RobotTemplate.rightMotor.set(0.0);
                    RobotTemplate.leftMotor.set(0.0);
                    goalScore = 0;
                    RobotTemplate.isInRange = true;
                }
            }
            if(goalScore == 2)
            {
                if(target() > optimalMidRange + rangeBuffer)
                {
                    RobotTemplate.rightMotor.set(MOTOR_SPEED);
                    RobotTemplate.leftMotor.set(MOTOR_SPEED);
                }
                if(target() < optimalMidRange - rangeBuffer)
                {
                    RobotTemplate.rightMotor.set(-MOTOR_SPEED);
                    RobotTemplate.leftMotor.set(-MOTOR_SPEED);
                }
                if((target() < optimalMidRange + rangeBuffer) && (target() > optimalMidRange - rangeBuffer))
                {
                    RobotTemplate.rightMotor.set(0.0);
                    RobotTemplate.leftMotor.set(0.0);
                    goalScore = 0;
                    RobotTemplate.isInRange = true;
                }
            }
            else
            {
                RobotTemplate.isInRange = true;
            }
        }
    }
    
    /**
     * Computes the estimated distance to a target using the height of the
     * particle in the image. For more information and graphics showing the math
     * behind this approach see the Vision Processing section of the
     * ScreenStepsLive documentation.
     *
     * @param image The image to use for measuring the particle estimated
     * rectangle
     * @param report The Particle Analysis Report for the particle
     * @param outer True if the particle should be treated as an outer target,
     * false to treat it as a center target
     * @return The estimated distance to the target in Inches.
     */
    public double computeDistance(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean outer) throws NIVisionException {
        double rectShort, height;
        int targetHeight;

        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
        //using the smaller of the estimated rectangle short side and the bounding rectangle height results in better performance
        //on skewed rectangles
        height = Math.min(report.boundingRectHeight, rectShort);


        targetHeight = outer ? 29 : 20;

        //final double ratio = 1.0/2.815;
        final double ratio = 1.0/2.55;
                
        
        
//return X_IMAGE_RES * targetHeight / (height * 12 * 2 * Math.sin(VIEW_ANGLE * Math.PI / (180 * 2)));
         System.out.println("This is  targetHeight value: " + targetHeight);
        System.out.println("This is X_IMAGE_RES * targetHeight value: " + X_IMAGE_RES * targetHeight);
        System.out.println("This is  value: (height * 12 * ratio *  Math.tan(VIEW_ANGLE * Math.PI / (180 * 2)))" + (height * 12 *ratio *  Math.tan(VIEW_ANGLE * Math.PI / (180 * 2))));
        return X_IMAGE_RES * targetHeight / (height * 12 * ratio *  Math.tan(VIEW_ANGLE * Math.PI / (180 * 2)));
        
    }

    /**
     * Computes a score (0-100) comparing the aspect ratio to the ideal aspect
     * ratio for the target. This method uses the equivalent rectangle sides to
     * determine aspect ratio as it performs better as the target gets skewed by
     * moving to the left or right. The equivalent rectangle is the rectangle
     * with sides x and y where particle area= x*y and particle perimeter= 2x+2y
     *
     * @param image The image containing the particle to score, needed to
     * perform additional measurements
     * @param report The Particle Analysis Report for the particle, used for the
     * width, height, and particle number
     * @param outer	Indicates whether the particle aspect ratio should be
     * compared to the ratio for the inner target or the outer
     * @return The aspect ratio score (0-100)
     */
    public double scoreAspectRatio(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean outer) throws NIVisionException {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);

        //original
        //idealAspectRatio = outer ? (62/29) : (62/20);	//Dimensions of goal opening + 4 inches on all 4 sides for reflective tape
        if (outer) {
            idealAspectRatio = 62 / 29;
        } else {
            idealAspectRatio = 62 / 20;
        }
        //changed
        //idealAspectRatio = outer ? (31/26) : (62/20);	



        //Divide width by height to measure aspect ratio
        if (report.boundingRectWidth > report.boundingRectHeight) {
            //particle is wider than it is tall, divide long by short
            aspectRatio = 100 * (1 - Math.abs((1 - ((rectLong / rectShort) / idealAspectRatio))));
        } else {
            //particle is taller than it is wide, divide short by long
            aspectRatio = 100 * (1 - Math.abs((1 - ((rectShort / rectLong) / idealAspectRatio))));
        }
        System.out.println("This is  aspectRatio value: " + aspectRatio);
        return (Math.max(0, Math.min(aspectRatio, 100.0)));		//force to be in range 0-100
    }

    /**
     * Compares scores to defined limits and returns true if the particle
     * appears to be a target
     *
     * @param scores The structure containing the scores to compare
     * @param outer True if the particle should be treated as an outer target,
     * false to treat it as a center target
     *
     * @return True if the particle meets all limits, false otherwise
     */
    boolean scoreCompare(Scores scores, boolean outer) {
        boolean isTarget = true;

        isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
        if (outer) {
            isTarget &= scores.aspectRatioOuter > ASPECT_RATIO_HIGH_LIMIT;
        } else {
            isTarget &= scores.aspectRatioInner > ASPECT_RATIO_HIGH_LIMIT;
        }
        isTarget &= scores.xEdge > X_EDGE_LIMIT;
        isTarget &= scores.yEdge > Y_EDGE_LIMIT;

        return isTarget;
    }

    /**
     * Computes a score (0-100) estimating how rectangular the particle is by
     * comparing the area of the particle to the area of the bounding box
     * surrounding it. A perfect rectangle would cover the entire bounding box.
     *
     * @param report The Particle Analysis Report for the particle to score
     * @return The rectangularity score (0-100)
     */
    double scoreRectangularity(ParticleAnalysisReport report) {
        if (report.boundingRectWidth * report.boundingRectHeight != 0) {
            return 100 * report.particleArea / (report.boundingRectWidth * report.boundingRectHeight);
        } else {
            return 0;
        }
    }

    /**
     * Computes a score based on the match between a template profile and the
     * particle profile in the X direction. This method uses the the column
     * averages and the profile defined at the top of the sample to look for the
     * solid vertical edges with a hollow center.
     *
     * @param image The image to use, should be the image before the convex hull
     * is performed
     * @param report The Particle Analysis Report for the particle
     *
     * @return The X Edge Score (0-100)
     */
    public double scoreXEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException {
        double total = 0;
        LinearAverages averages;

        Rect rect = new Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_COLUMN_AVERAGES, rect);
        float columnAverages[] = averages.getColumnAverages();
        for (int i = 0; i < (columnAverages.length); i++) {
            if (xMin[(i * (XMINSIZE - 1) / columnAverages.length)] < columnAverages[i]
                    && columnAverages[i] < xMax[i * (XMAXSIZE - 1) / columnAverages.length]) {
                total++;
            }
        }
        total = 100 * total / (columnAverages.length);
        return total;
    }

    /**
     * Computes a score based on the match between a template profile and the
     * particle profile in the Y direction. This method uses the the row
     * averages and the profile defined at the top of the sample to look for the
     * solid horizontal edges with a hollow center
     *
     * @param image The image to use, should be the image before the convex hull
     * is performed
     * @param report The Particle Analysis Report for the particle
     *
     * @return The Y Edge score (0-100)
     *
     */
    public double scoreYEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException {
        double total = 0;
        LinearAverages averages;

        Rect rect = new Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_ROW_AVERAGES, rect);
        float rowAverages[] = averages.getRowAverages();
        for (int i = 0; i < (rowAverages.length); i++) {
            if (yMin[(i * (YMINSIZE - 1) / rowAverages.length)] < rowAverages[i]
                    && rowAverages[i] < yMax[i * (YMAXSIZE - 1) / rowAverages.length]) {
                total++;
            }
        }
        total = 100 * total / (rowAverages.length);
        return total;
    }
}

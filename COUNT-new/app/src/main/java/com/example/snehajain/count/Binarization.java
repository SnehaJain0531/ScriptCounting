package com.example.snehajain.count;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Binarization {




    public int[] cropScript(Mat image) {
        int[] limits = new int[2];
        int width = 720, height = 960;
        Size size = new Size(width, height);
        Mat gray = new Mat();
        Mat edges = new Mat();
        Mat reimage = new Mat();
        Mat lines = new Mat();
        Imgproc.resize(image, reimage, size, 0, 0, Imgproc.INTER_AREA);
        Imgproc.cvtColor(reimage, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(gray, edges, 60, 60 * 3);
        ArrayList<Double> y_Coords = new ArrayList<>();


        Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 250);

        if (lines == null) {
            limits[0] = 0;
            limits[1] = reimage.height()-1;
        }
        else
            {

            for (int x = 0; x < lines.rows(); x++) {
                double rho = lines.get(x, 0)[0],
                        theta = lines.get(x, 0)[1];
                double a = Math.cos(theta), b = Math.sin(theta);
                double  y = b*rho;
                double y1 = y+1000*a;
                double y2 = y-1000*a;

                if(y1>=0 && y2>=0)
                {
                    y_Coords.add(y1);
                    y_Coords.add(y2);
                }

            }

                Collections.sort(y_Coords);
                System.out.println(y_Coords.size());


                if(y_Coords.size()>75)
                {


                    limits[0] = (int)Math.round(y_Coords.get(0))-50;
                    limits[1] = (int)(Math.round(y_Coords.get(y_Coords.size()-1))) + 50;
                    System.out.println(limits[0]+","+limits[1]);


                }

                /*else if(y_Coords.size()>25)
                {
                    Long l1 = new Long(Math.round(y_Coords.get(0)) - 100);
                    limits[0] = l1.intValue();
                    Long l2 = new Long(Math.round(y_Coords.get(y_Coords.size()-1)) + 100);
                    limits[1] = l2.intValue() ;
                    System.out.println(limits);
                }*/

                else
                {
                    limits[0] = 0;
                    limits[1] = 960;
                }

                System.out.println(limits[0] + "," + limits[1]);

                System.out.println("Height:"+image.height());
                float miny = (limits[0]*image.height())/960;
                float maxy = (limits[1]*image.height())/960;
                limits[0] = Math.round(miny);
                limits[1] = Math.round(maxy);
                System.out.println(limits[0] + "," + limits[1]);
            }

            if(limits[1]==0)
            {
                limits[1] = image.height();
            }
            System.out.println(limits[0] + "," + limits[1]);
            return limits;
    }

    public ArrayList<Integer> getScriptCount(Mat image,int miny,int maxy)
    {


        float fminy = (miny*2250)/image.height();
        float fmaxy = (maxy*2250)/image.height();
        maxy = Math.round(fmaxy);
        miny = Math.round(fminy);
        Imgproc.resize(image,image,new Size(3000,2250),0,0,Imgproc.INTER_AREA);
        Mat gray = new Mat();
        Mat athresh = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(gray,athresh,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,151,11);
        if(miny<0) miny = 0;
        if(maxy>2250) maxy = 2250;


        ArrayList<Integer> results = new ArrayList<Integer>();

        int noi = 100;
        int stepcount = image.width()/noi;

        for(int x=0;x<image.width();x+=stepcount)
        {
            int dip_count=0;
            ArrayList<Integer> intensities = new ArrayList<>();



            System.out.println(miny+","+maxy);

            for(int y=miny;y<maxy;y++)
            {
                intensities.add((int)athresh.get(y,x)[0]);
            }

            for(int i=4; i<intensities.size()-4;i++)
            {
                if(intensities.get(i-4)==255 &&  intensities.get(i-3)==255 && intensities.get(i-2)==255  && intensities.get(i-1)==255 && intensities.get(i)==0 && intensities.get(i+1)==0 && intensities.get(i+2)==0 && intensities.get(i+3)==0)
                {
                    dip_count+=1;
                }
            }

            results.add(dip_count+1);


        }

        return results;
    }
}

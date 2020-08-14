package com.example.snehajain.count;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Binarization {



 // Hough trans cropping removed due to unrealibility
  /*  public int[] cropScript(Mat image) {
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

                else if(y_Coords.size()>25)
                {
                    Long l1 = new Long(Math.round(y_Coords.get(0)) - 100);
                    limits[0] = l1.intValue();
                    Long l2 = new Long(Math.round(y_Coords.get(y_Coords.size()-1)) + 100);
                    limits[1] = l2.intValue() ;
                    System.out.println(limits);
                }

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
    }*/

    public ArrayList<Integer> getScriptCount(Mat image,int miny,int maxy)
    {


        /*float fminy = (miny*2250)/image.height();
        float fmaxy = (maxy*2250)/image.height();
        maxy = Math.round(fmaxy);
        miny = Math.round(fminy);*
        Imgproc.resize(image,image,new Size(3000,2250),0,0,Imgproc.INTER_AREA);*/
        Mat gray = new Mat();
        Mat athresh = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(gray,athresh,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,151,15);
        if(miny<0) miny = 0;
        if(maxy>image.height()) maxy = image.height();


        ArrayList<Integer> results = new ArrayList<Integer>();

        int noi = 100;
        int stepcount = image.width()/noi;

        if (image.width()<=100)
        {
            stepcount = 1;
        }

        for(int x=0;x<image.width();x+=stepcount)
        {
            int dip_count=0,total_dip_count=0;
            ArrayList<Integer> intensities = new ArrayList<>();
            int sum_of_thicknesses=0;


            System.out.println(miny+","+maxy);

            for(int y=miny;y<maxy;y++)
            {
                intensities.add((int)athresh.get(y,x)[0]);
            }

            int white_start=-1,in_white=0;

            if(intensities.get(0)==255)
            {
                in_white=1;
                white_start=0;
            }

            ArrayList<Integer> white_region_thicknesses = new ArrayList<>();


            for(int i=1; i<intensities.size()-1;i++)
            {
                if(in_white==1 && intensities.get(i)==0)
                {
                    sum_of_thicknesses+=(i-white_start);
                    total_dip_count+=1;
                    white_region_thicknesses.add((i-white_start));
                    in_white=0;
                    white_start=-1;

                }

                if(in_white==0 && intensities.get(i)==255)
                {
                    white_start=i;
                    in_white=1;
                }
            }

            double mean = sum_of_thicknesses/total_dip_count;
            double lthresh = 0.25 * mean;
            double uthresh = 5 * mean;

            if(total_dip_count>100)
            {
                lthresh = 0;
            }

            for(int i=0; i<white_region_thicknesses.size()-1;i++)
            {
                if(white_region_thicknesses.get(i)>=lthresh && white_region_thicknesses.get(i)<=uthresh)
                {
                    dip_count+=1;
                }
            }

            results.add(dip_count+1);

        }

        return results;
    }
}

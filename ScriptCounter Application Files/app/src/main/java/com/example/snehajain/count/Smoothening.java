package com.example.snehajain.count;



import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


class Smoothening {

   /* public float[] houghCropping(Mat img, Mat edges) {
        Mat lines = new Mat();
        float[] y_vals = new float[2];

        Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, 200, 200, 100);

        int miny = 99999, maxy = 0;
        double y1, y2;

        for (int x = 0; x < lines.rows(); x++) {
            double[] l = lines.get(x, 0);
            y1 = l[1];
            y2 = l[3];
            maxy = Math.max(maxy, (int)(y1+y2)/2);
            miny = Math.min(miny, (int)(y1+y2)/2);
            Imgproc.line(img, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }

        y_vals[0] = miny-5;
        y_vals[1] = maxy+5;

        return y_vals;
    }*/

    public int mode(ArrayList<Integer> arr) {
        HashMap<Integer, Integer> vals = new HashMap<Integer, Integer>();

        for (int i=0; i<arr.size(); i++) {
            if (!vals.containsKey(arr.get(i)))  {
                vals.put(arr.get(i), 1);
            }
            else {
                vals.put(arr.get(i), vals.get(arr.get(i))+1);
            }
        }

        int max_freq = 0;
        int md = 0;

        for (HashMap.Entry<Integer, Integer> set : vals.entrySet()) {
            if (set.getValue() > max_freq) {
                max_freq = set.getValue();
                md = set.getKey();
            }
        }
        return md;
    }

    private double[] smooth(double[] y, int count) {
        int WSZ = 3;
        int s;

        for (int i=WSZ-1; i<count; i++) {
            s = 0;
            for (int j=i; j>=i-WSZ+1; j--) {
                s+=y[j];
            }
            y[i] = s/WSZ;
        }
        return y;
    }


    public ArrayList<Integer> getScriptCount(Mat img,int miny,int maxy) {

        Mat gray = new Mat();
        Mat edges = new Mat();

        int width = 720, height = 960;
        Size size = new Size(width, height);

        Imgproc.resize(img, img, size, 0, 0, Imgproc.INTER_AREA);
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(gray, edges, 60, 60*3);

        /*vals=houghCropping(img, edges);
        miny = (int)vals[0];
        maxy = (int)vals[1];*/

        float fminy = (miny*960)/img.height();
        float fmaxy = (maxy*960)/img.height();
        maxy = Math.round(fmaxy);
        miny = Math.round(fminy);

        if(miny<0) miny = 0;
        if(maxy>960) maxy = 960;

        ArrayList<Integer> results = new ArrayList<Integer>();
        for (int j=10; j<width; j+=10) {

            double[] y = new double[maxy-miny+1];
            int count = 0;

            for (int i=miny; i<maxy; i++)
                y[count++] = (int)gray.get(i, j)[0];
            for (int i=0; i<2; i++)
                y = smooth(y, count);

            int dips = 0;

            for (int i=1; i<count-1; i++)
                if (y[i-1] >= y[i] && y[i+1] > y[i])
                    dips += 1;

            results.add(dips);
        }
        //System.out.println(mode(results));
        return results;
    }
}




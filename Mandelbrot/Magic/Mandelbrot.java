import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class Mandelbrot extends PApplet {
    private static double wScale = 3, hScale = 2;
    private static double viewWidth = 3840, viewHeight = 2160,scl=1, res = 3, dscl = 2;
    //    private BigDecimal scl = new BigDecimal(1);
    private static double maxIter = 200;
    private static double rg1 = 255, rg2 = 255, rg3 = 255;
    private static double hue = 1, sat = 1, bri = 1, renderTime = 0, cxVal, cyVal;
    private static double rendPX = viewWidth/2, rendPY = viewHeight/2;
    private static double juX = 0, juY = 0;
    private static boolean drawMandel = true,moveImage=false,wheelie=false;
    private static PImage output;
    private static float imgScale =1,imgRX=0,imgRY=0,oldMX=0,oldMY=0;
    public static void main(String[] args) {
            if(args.length>0) {

            fromFile(args[0]);
        }
        PApplet.main("Mandelbrot", args);
    }
    public void settings() {
//        size((int) viewWidth, (int) viewHeight);
        fullScreen();
    }

    public void setup() {
        background(0);
//        colorMode(HSB, (int) maxIter);
        colorMode(HSB, 255);
        noStroke();
        render(imagX(rendPX), imagY(rendPY), scl,!drawMandel);

    }

    public void draw() {
        if(moveImage && (mousePressed || wheelie)){
            if(oldMX==0){
                oldMX=mouseX;
                oldMY=mouseY;
            }
            imgRX+=(mouseX-oldMX);
            imgRY+=(mouseY-oldMY);
            oldMX=mouseX;
            oldMY=mouseY;
            scale(imgScale);
//            output.updatePixels();
            image(output,imgRX,imgRY,width,height);
            wheelie=false;
        }
        if(keyPressed) {
            colorMode(RGB, 255);
            fill(115, 150);
            rect(0, 0, 400, 600);
            fill(255);
            text(updateStats(), 10, 20);
            colorMode(HSB, 255);
        }
    }
    public void mouseReleased(){
        oldMX=0;
        oldMY=0;
    }
    public void mouseWheel(MouseEvent e){
        if(imgScale>0) {
            imgScale += e.getCount();
            wheelie=true;
        }
    }
    public void mouseClicked() {
        if(!moveImage) {
            if (drawMandel) {
                rendPX = transX(mouseX);
                rendPY = transY(mouseY);
                juX = imagX(rendPX);
                juY = imagY(rendPY);
                render(juX, juY, scl, false);

            } else {
                rendPX = transX(mouseX);
                rendPY = transY(mouseY);
                render(imagX(rendPX), imagY(rendPY), scl);
            }
        }

    }
    private void render(double cx, double cy, double scale){
        render(cx,cy,scale,true);
    }
    private void render(double cx, double cy, double scale, boolean julia) {

        colorMode(HSB, (255);
        println("---rendering---");
        int startTimer = millis();
        int iter;
        double nXPix = width / res, nYPix = height / res;
        output = createImage((int)nXPix,1+(int)nYPix,HSB);
        int[][] result = new int[(int)nXPix][(int)nYPix];
        for (double i = -nXPix / 2; i < nXPix / 2; i++) {
            for (double j = -height / res / 2; j < height / res / 2; j++) {
                if(julia){
                    iter = iterate(cx + (i / (nXPix / 2)) * (wScale / scale / 2), cy + (j / (nYPix / 2)) * (hScale / scale / 2));
                }else {
                    iter = iterateM(cx + (i / (nXPix / 2)) * (wScale / scale / 2), cy + (j / (nYPix / 2)) * (hScale / scale / 2));
                }
//                result[(int)(i+nXPix/2)][(int)(j+nYPix/2)]=iter;
                if(iter >0) {
                    output.pixels[(int)(i+nXPix/2)+(int)((j+nYPix/2)*nXPix)] =
                    color((float) (hue*100*(sin(radians(iter*2))+1)), (float)(sat*150 * (iter / Math.abs(iter))), (float)(bri*150 * (iter / Math.abs(iter))));
                }else{
                    output.pixels[(int)(i+nXPix/2)+(int)((j+nYPix/2)*nXPix)] =
                            color(0);
                }

//                output.pixels[(int)(i+nXPix/2)+(int)((j+nYPix/2)*nXPix)] = iter;
//                rect((float) ((i + nXPix / 2) * res), (float) ((j + nYPix / 2) * res), (float) res, (float) res);
            }
        }
//        output.updatePixels();
        image(output,0,0,(int)viewWidth,(int)viewHeight);
        scl=scale;
        textSize(20);
        fill(255);

        renderTime = millis() - startTimer;
        noStroke();
        println(updateStats());

    }
    private int iterateM(double cx, double cy) {
        double zx = 0, zy = 0;
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 2) {
                return i;
            }
            double zx1 = (zx * zx - zy * zy) + cx;
            double zy1 = (2 * zx * zy) + cy;
            zx = zx1;
            zy = zy1;
        }
        return -1;
    }
    private int iterate(double zx, double zy) {
        double cx = juX, cy = juY;
        for (int i = 1; i < maxIter; i++) {
            if (dist(cx, cy, zx, zy) > 2) {
                return i;
            }
            double zx1 = (zx * zx - zy * zy) + cx;
            double zy1 = (2 * zx * zy) + cy;
            zx = zx1;
            zy = zy1;
        }
        return -1;

    }

    private double dist(double cx, double cy, double zx, double zy) {
        return Math.sqrt(
                Math.abs(zx - cx) * Math.abs(zx - cx) +
                        Math.abs(zy - cy) * Math.abs(zy - cy));

    }
    private int saveNum=0;
    private String updateStats(){
        return updateStats(false);
    }
    private String updateStats(boolean accurate) {
        DecimalFormat df;
        if(accurate){
            df = new DecimalFormat("##################################.##########" +
                    "##################################################################");
        }else {
            df = new DecimalFormat("#####.#####");
        }
        String juul;
        int iter;
        if(drawMandel){
            juul = "Mandelbrot";
            iter = iterateM(imagX(rendPX),imagY(rendPY));
        }else{
            juul="Julia from: (" + df.format(juX) + " + " + df.format(juY) +"i)";
            iter = iterate(imagX(rendPX),imagY(rendPY));
        }
        return ("rendered in: " + df.format((renderTime) )+ "ms" + "\n" +
                "\n" +
                "(q,a)zoom: " + scl + "\n" +
                "(4,v)zoom speed: " + df.format(dscl) + "\n" +
                "(r,f)pixel size: " + df.format(res) + "\n" +
                "(w,s)max iter: " + df.format(maxIter) + "\n" +
                "\n" +
                "rendering: " + juul + "\n" +
                "\n" +
                "(1,z)H: "+ df.format(hue)+"\n"+
                "(2,x)S: "+ df.format(sat)+"\n"+
                "(3,c)B: "+ df.format(bri)+"\n"+
/*                "(5,b)rg1: "+ df.format(rg1)+"\n"+
                "(6,n)rg2: "+ df.format(rg2)+"\n"+
                "(7,m)rg3: "+ df.format(rg3)+"\n"+*/
                "renderPoint: (" + df.format(rendPX) + " + " + df.format(rendPY) + "i)" + "\n" +
                "chaos at RP : " + iter +"\n"+
                "-----------");
    }
    private String toFile(){
        String juul;
        if(drawMandel){
            juul = "true\n0\n0";
        }else{
            juul="false\n"+juX + "\n" + juY;
        }
        return (scl + "\n" +
                res + "\n" +
                maxIter + "\n" +
                juul + "\n" +
                hue+"\n"+
                sat+"\n"+
                bri+"\n"+
/*                "(5,b)rg1: "+ (rg1)+"\n"+
                "(6,n)rg2: "+ (rg2)+"\n"+
                "(7,m)rg3: "+ (rg3)+"\n"+*/
                rendPX + "\n" +
                rendPY + "\n");
    }
    private static void fromFile(String file){
        try{
           Scanner input = new Scanner(new File(System.getProperty("user.dir")+"\\MandelBrot\\Magic\\"+file));
           int cnt=0;
           while(input.hasNextLine()) {
               switch(cnt) {
                   case(0):
                       scl = Double.parseDouble(input.nextLine());
                       break;
                   case(1):
                       res = Double.parseDouble(input.nextLine());
                       break;
                   case(2):
                       maxIter = Double.parseDouble(input.nextLine());
                       break;
                   case(3):
                       drawMandel = Boolean.parseBoolean(input.nextLine());
                       break;
                   case(4):
                       juX = Double.parseDouble(input.nextLine());
                       break;
                   case(5):
                       juY = Double.parseDouble(input.nextLine());
                       break;
                   case(6):
                       hue = Double.parseDouble(input.nextLine());
                       break;
                   case(7):
                       sat = Double.parseDouble(input.nextLine());
                       break;
                   case(8):
                       bri = Double.parseDouble(input.nextLine());
                       break;
                   case(9):
                       rendPX = Double.parseDouble(input.nextLine());
                       break;
                   case(10):
                       rendPY = Double.parseDouble(input.nextLine());
                       break;
                   default:
                       input.nextLine();
               }
               cnt++;
           }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        }

    private double newX(double x){
        return ((rendPX+((x-viewWidth/2)/scl))-viewWidth/2)*(wScale/viewWidth);
    }
    private double newY(double y){
        return (((rendPY+(y-viewHeight/2)/scl))-viewHeight/2)*(hScale/viewHeight);
    }
    private double imagX(double x){
        return (x-viewWidth/2)*(wScale/viewWidth);
    }
    private double imagY(double y){
        return (y-viewHeight/2)*(hScale/viewHeight);
    }
    private double transX(double x) {
        return (rendPX+((x-viewWidth/2)/scl));
    }

    private double transY(double y) {
        return ((rendPY+(y-viewHeight/2)/scl));
    }

    public void keyPressed() {
        if(keyCode == CONTROL){
            drawMandel = !drawMandel;
            if(drawMandel){
                render(imagX(rendPX),imagY(rendPY),scl,false);
            }else{
                render(imagX(rendPX),imagY(rendPY),scl);
            }
        }
        switch (key) {
            case('='):
                println("saving....");
                output.save(System.getProperty("user.dir")+"\\MandelBrot\\Magic\\"+"output"+saveNum+".tiff");
                List<String> lines = Arrays.asList(toFile());
                Path file = Paths.get(System.getProperty("user.dir")+"\\MandelBrot\\Magic\\"+"output"+saveNum+".txt");
                try {
                    Files.write(file, lines, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveNum++;
                println("saved!");
                break;
            case('p'):
                moveImage=!moveImage;
                break;
            case ('q'):
                if(drawMandel){
                    render(imagX(rendPX), imagY(rendPY), scl * dscl,false);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl * dscl);
                }
                break;

            case ('a'):
                if(drawMandel){
                    render(imagX(rendPX), imagY(rendPY), scl / dscl,false);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl / dscl);
                }
                break;


            case ('4'):
                dscl = dscl * dscl;
                break;
            case ('v'):
                dscl = Math.sqrt(dscl);
                break;


            case ('`'):
                hue = 1;
                sat = 1;
                bri = 1;
                rendPX = viewWidth / 2;
                rendPY = viewHeight / 2;
                imgRX = 0;
                imgRY = 0;
                scl = 1;
                imgScale=1;
                moveImage = false;
                dscl = 2;
                maxIter = 200;
                drawMandel =true;
                render(imagX(rendPX), imagX(rendPY), scl,false);
                break;


            case ('s'):
                maxIter /= dscl;
                break;
            case ('w'):
                maxIter *= dscl;
                break;


            case ('2'):
                sat *= dscl;
                break;
            case ('x'):
                sat /= dscl;
                break;

            case ('3'):
                bri *= dscl;
                break;
            case ('c'):
                bri /= dscl;
                break;

            case ('1'):
                hue = hue * dscl;
                break;
            case ('z'):
                hue /= dscl;
                break;

            case ('5'):
                rg1 += +0.1;
                break;
            case ('b'):
                rg1 -= 0.1;
                break;
            case ('6'):
                rg2 += +0.1;
                break;
            case ('n'):
                rg2 -= 0.1;
                break;
            case ('7'):
                rg3 += +0.1;
                break;
            case ('m'):
                rg3 -= 0.1;
                break;


            case ('r'):
                if(res>1) {
                    res -= 1;
                }else{
                    res/=2;
                }
                break;
            case ('f'):
                if(res>=1) {
                    res += 1;
                }else{
                    res*=2;
                }
                break;

            case (' '):
                if(drawMandel){
                    render(imagX(rendPX), imagY(rendPY), scl,false);
                }else {
                    render(imagX(rendPX), imagY(rendPY), scl);
                }
                break;
            default:
                break;
        }
    }


}
//    public void mouseReleased(){
//        render(0,0,scl,res);
//    }
//


//            ellipse(oldX(zx),oldY(zy),30,30);
//            ellipse(oldX(zx1),oldY(zy1),30,30);
//            line(oldX(zx),oldY(zy),oldX(zx1),oldY(zy1));
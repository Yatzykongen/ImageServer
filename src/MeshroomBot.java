import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;

public class MeshroomBot implements Runnable {

    private Robot robot;

    private ObjectFile objectFile;

    private Server server;

    public MeshroomBot(Server server) {
        this.server = server;
    }

    @Override
    public void run()
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        //Wait
        robot.delay(3000);

        openMeshroom();
        openPhotoFolder();
        robot.delay(1000);
        dragAndDrop(1627, 1156, 168, 395);
        makeSaveFile();
        startMeshroomRender();
        server.setProgress(0);
        waitForPixelColor(190, 821, new Color(76, 175, 80));//CameraInit Done
        server.setProgress(1);
        System.out.println("CameraInit Done");
        waitForPixelColor(390, 821, new Color(76, 175, 80));//FeatureExtraction Done
        server.setProgress(2);
        System.out.println("FeatureExtraction Done");
        waitForPixelColor(590, 821, new Color(76, 175, 80));//ImageMatching Done
        server.setProgress(3);
        System.out.println("ImageMatching Done");
        waitForPixelColor(790, 821, new Color(76, 175, 80));//FeatureMatching Done
        server.setProgress(4);
        System.out.println("FeatureMatching Done");
        waitForPixelColor(990, 821, new Color(76, 175, 80));//StructureFromMotion Done
        server.setProgress(5);
        System.out.println("StructureFromMotion Done");
        waitForPixelColor(1190, 821, new Color(76, 175, 80));//PrepareDenseScene Done
        server.setProgress(6);
        System.out.println("PrepareDenseScene Done");
        waitForPixelColor(1400, 821, new Color(76, 175, 80));//CameraConnection Done
        server.setProgress(7);
        System.out.println("CameraConnection Done");
        waitForPixelColor(1600, 821, new Color(76, 175, 80));//DepthMap Done
        server.setProgress(8);
        System.out.println("DepthMap Done");
        waitForPixelColor(1800, 821, new Color(76, 175, 80));//DepthMapFilter Done
        server.setProgress(9);
        System.out.println("DepthMapFilter Done");
        waitForPixelColor(2000, 821, new Color(76, 175, 80));//Meshing Done
        server.setProgress(10);
        System.out.println("Meshing Done");
        waitForPixelColor(2200, 821, new Color(76, 175, 80));//MeshFiltering Done
        server.setProgress(11);
        System.out.println("MeshFiltering Done");
        waitForPixelColor(2400, 821, new Color(76, 175, 80));//Texturing Done
        server.setProgress(12);
        System.out.println("Texturing Done");


        loadModel();
        closeWindows();
        File objFile = getObjectFile("C:\\Users\\sondr\\Desktop\\MeshroomCache\\Texturing", "texturedMesh.obj");
        File mtlFile = getObjectFile("C:\\Users\\sondr\\Desktop\\MeshroomCache\\Texturing", "texturedMesh.mtl");
        File pngFile = getObjectFile("C:\\Users\\sondr\\Desktop\\MeshroomCache\\Texturing", "texture_0.png");
        byte[] objContent = null;
        byte[] mtlContent = null;
        byte[] pngContent = null;
        try {
            if (objFile != null) {
                objContent = Files.readAllBytes(objFile.toPath());
                System.out.println("Object File was made!");
            }
            if (mtlFile != null) {
                mtlContent = Files.readAllBytes(mtlFile.toPath());
                System.out.println("Object File was made!");
            }
            if (pngFile != null) {
                pngContent = Files.readAllBytes(pngFile.toPath());
                System.out.println("Object File was made!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        objectFile = new ObjectFile(objContent, objFile.getName(), "obj", mtlContent, mtlFile.getName(), "mtl", pngContent, pngFile.getName(), "png");
        server.setObjectFile(objectFile);
        server.setBotRunningFalse();

    }
    private void type(String s)
    {
        byte[] bytes = s.getBytes();
        for (byte b : bytes)
        {
            int code = b;
            // keycode only handles [A-Z] (which is ASCII decimal [65-90])
            if (code > 96 && code < 123) code = code - 32;
            robot.delay(100);
            robot.keyPress(code);
            robot.keyRelease(code);
        }
    }

    private void getMouseCoordinates()
    {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point point = pointerInfo.getLocation();
        int x = (int) point.getX();
        int y = (int) point.getY();
        Color color = robot.getPixelColor(x, y);
        System.out.print("Mouse X:"+x+"  ");
        System.out.println("Mouse Y:"+y+"  ");
        System.out.println("Pixel color: "+color);
    }

    private void openMeshroom()
    {
        moveMouseAndClick(1055, 1420);
    }

    private void loopMouseCoordinates()
    {
        for(int i = 0; i<10; i++)
        {
            getMouseCoordinates();
            robot.delay(1000);
        }
    }

    private void moveMouseAndClick(int x, int y)
    {
        robot.mouseMove(x, y);
        robot.delay(1000);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void openPhotoFolder()
    {
        moveMouseAndClick(122, 1420);
        moveMouseAndClick(1230, 1217);
    }

    private void dragAndDrop(int xInitial, int yInitial, int xEnd, int yEnd)
    {
        robot.mouseMove(xInitial, yInitial);
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(500);
        robot.mouseMove(xEnd, yEnd);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void startMeshroomRender()
    {
        moveMouseAndClick(1677, 71);
        robot.delay(500);
    }

    private void waitForPixelColor(int x, int y, Color color)
    {
        while (robot.getPixelColor(x, y).getRGB()!=color.getRGB()) robot.delay(1000);
    }

    private void loadModel()
    {
        moveMouseAndClick(2878, 665);
        robot.delay(1000);
    }

    private void closeWindows()
    {
        moveMouseAndClick(3426, 16);
        robot.delay(500);
        moveMouseAndClick(2259, 1011);
    }

    private void makeSaveFile()
    {
        moveMouseAndClick(1677, 71);
        robot.delay(500);
        moveMouseAndClick(1700, 570);
        robot.delay(500);
        moveMouseAndClick(90, 186);
        robot.delay(500);
        moveMouseAndClick(190, 646);
        robot.delay(500);
        type("UGV Model");
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private File getObjectFile(String pathName, String fileName){
        File root = new File(pathName);
        try {
            boolean recursive = true;

            Collection files = FileUtils.listFiles(root, null, recursive);

            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.getName().equals(fileName)) {
                    System.out.println(file.getAbsolutePath());
                    return file;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
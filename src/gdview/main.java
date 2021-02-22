/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdview;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.MouseEvent;
import static java.awt.event.MouseEvent.BUTTON1;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class main {

    public static File imageFile;
    public static JLabel label;
    public static double schaal;
    public static JFrame frame;
    static JPanel pane;
    public static int imageHoogte, imageBreedte, indexfilesInDirectory;
    public static int frameHoogte, schermwijdte;
    public static Vector filesInDirectory;
    public static int gekozenFileIndex;

    private static void setButton(int button) {

    }

    public main() throws IOException {
        gekozenFileIndex = 0;
        indexfilesInDirectory = 1;
        filesInDirectory = getFilesInDirectory(getStartdirectory());
        File f = (File) filesInDirectory.elementAt(gekozenFileIndex);
        indexfilesInDirectory = gekozenFileIndex;
        String absoluutPath = f.getAbsolutePath();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] schermen = ge.getScreenDevices();
        GraphicsDevice mijnScherm = schermen[0];
        frameHoogte = mijnScherm.getDisplayMode().getHeight();
        schermwijdte = mijnScherm.getDisplayMode().getWidth();
        frameHoogte = frameHoogte - 50;
        schermwijdte = schermwijdte - 50;

        frame = new JFrame(absoluutPath);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pane = new JPanel();
        pane.setLayout(null);
        label = new JLabel(createImageIcon(absoluutPath, frameHoogte, schermwijdte));
        frame.add(pane);
        frame.getContentPane().add(label);
        frame.getContentPane().setBounds(0, 0, imageBreedte, imageHoogte);
        frame.setBounds(0, 0, imageBreedte + 40, imageHoogte + 50);
        muisLuisteraar mijnMuisLuisteraar = new muisLuisteraar();
        frame.addMouseListener(mijnMuisLuisteraar);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {

            main images = new main();

            // TODO code application logic here
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getStartdirectory() throws IOException {
        String eruit = "";
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF Images", "jpg", "gif", "png", "jpeg", "tiff");
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(pane);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            eruit = file.getCanonicalPath();
        }
        return eruit;
    }

    protected static ImageIcon createImageIcon(String deImageFile, int schermHoogte, int schermBreedte) throws IOException, NullPointerException {
        double quotientSchermWH, quotientImageWH;
        Image imageVooricon;
        imageFile = new File(deImageFile);

        Image imageToBeDisplayed = ImageIO.read(imageFile);

        int heightImageToBeDisplayed = imageToBeDisplayed.getHeight(null);
        int widthImageToBeDisplayed = imageToBeDisplayed.getWidth(null);

        quotientSchermWH = schermBreedte / (double) schermHoogte;
        quotientImageWH = widthImageToBeDisplayed / (double) heightImageToBeDisplayed;
        if (quotientSchermWH > quotientImageWH) {
            // schalen op hoogte
//            System.out.println("schalen op hoogte");
            imageVooricon = imageToBeDisplayed.getScaledInstance(-1, schermHoogte, Image.SCALE_FAST);
            imageHoogte = imageVooricon.getHeight(frame);
            imageBreedte = imageVooricon.getWidth(frame);
            schaal = schermHoogte / (double) heightImageToBeDisplayed;

        } else {
            // schalen op breedte
//            System.out.println("schalen op breedte");
            imageVooricon = imageToBeDisplayed.getScaledInstance(schermBreedte, -1, Image.SCALE_FAST);
            imageHoogte = imageVooricon.getHeight(frame);
            imageBreedte = imageVooricon.getWidth(frame);
            schaal = schermBreedte / (double) widthImageToBeDisplayed;
        }
        return new ImageIcon(imageVooricon);
    }

    Vector getFilesInDirectory(String erin) {
//        Vector eruit = new Vector();
        File startFile = new File(erin);
        File startDirFile = startFile.getParentFile();
        Vector fileVector = new Vector();
//        System.out.println(startDirFile);
        File folder = new File(startDirFile.getAbsolutePath());
        File[] listOfFiles = folder.listFiles();

        Arrays.sort(listOfFiles);

//        for (File file : listOfFiles) {
//            if (file.isFile()) {
//                System.out.println(file.getName());
//            }
//        }
        try {
            // for each name in the path array
            gekozenFileIndex = 0;
            for (File fileInDir : listOfFiles) {
                if (fileInDir.isFile()) {
                    String extension = fileInDir.getName().substring(fileInDir.getName().lastIndexOf("."));
                    if (extension.toLowerCase().contains(".png")) {
//                        System.out.println(fileInDir);
                        fileVector.addElement(fileInDir);
                        if (fileInDir.equals(startFile)) {
                            gekozenFileIndex = fileVector.size() - 1;

                        }
                    }
                    if (extension.toLowerCase().contains(".jpg")) {
//                        System.out.println(fileInDir);
                        fileVector.addElement(fileInDir);
                        if (fileInDir.equals(startFile)) {
                            gekozenFileIndex = fileVector.size() - 1;

                        }
                    }
                    if (extension.toLowerCase().contains(".jpeg")) {
//                        System.out.println(fileInDir);
                        fileVector.addElement(fileInDir);
                        if (fileInDir.equals(startFile)) {
                            gekozenFileIndex = fileVector.size() - 1;

                        }
                    }
                    if (extension.toLowerCase().contains(".tiff")) {
//                        System.out.println(fileInDir);
                        fileVector.addElement(fileInDir);
                        if (fileInDir.equals(startFile)) {
                            gekozenFileIndex = fileVector.size() - 1;

                        }
                    }

                }
            }

        } catch (Exception e) {
            System.out.println("Error");            // if any error occurs
            e.printStackTrace();
        }
        return fileVector;
    }

    class muisLuisteraar extends MouseInputAdapter {

        public void mouseReleased(MouseEvent event) {
            int x = event.getX();
            int y = event.getY();
            int richting = 0;
//        System.out.println("javaapplication2.muisLuisteraar.mouseReleased() " + main.schaal );
//            System.out.println("javaapplication2.muisLuisteraar.mouseReleased() " + event.getButton());

            if (event.getButton() == BUTTON1) {
                richting = -1;
                if (main.indexfilesInDirectory == 0) {
                    richting = 0;
                };
            } else {
                richting = 1;
                if (main.indexfilesInDirectory == filesInDirectory.size() - 1) {
                    richting = 0;
                };

            }

            main.indexfilesInDirectory += richting;
            if (richting != 0) {
                File f = (File) filesInDirectory.elementAt(main.indexfilesInDirectory);
                String absoluutPath = f.getAbsolutePath();
//                System.out.print(main.indexfilesInDirectory);
//                System.out.print(" " + main.filesInDirectory.size());
//                System.out.println(" " + f.getName());

                try {
                    ImageIcon nextOrPrevImageIcon = createImageIcon(absoluutPath, main.frameHoogte, schermwijdte);
                    main.frame.setTitle(main.indexfilesInDirectory + " " + main.filesInDirectory.size() + " " + absoluutPath);
                    main.frame.getContentPane().removeAll();
                    main.label = new JLabel(nextOrPrevImageIcon);
                    main.frame.getContentPane().add(main.label);
                    main.frame.pack();

                } catch (IOException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("gdview.main.muisLuisteraar.mouseReleased() io exception" + absoluutPath);

                } catch (NullPointerException e) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, e);
                    System.out.println("gdview.main.muisLuisteraar.mouseReleased() NullPointerException" + absoluutPath);
                }

            }
        }
    }

}

package gdview;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

public class viewClass {

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

    public viewClass(String parameterFile) throws IOException {
        if (parameterFile.compareTo("leeg") == 0) {

            gekozenFileIndex = 0;
            indexfilesInDirectory = 1;
            filesInDirectory = getFilesInDirectory(getStartdirectory());
        } else {
            gekozenFileIndex = 0;
            indexfilesInDirectory = 1;
            filesInDirectory = getFilesInDirectory(parameterFile);

        }
        File f = (File) filesInDirectory.elementAt(gekozenFileIndex);
        indexfilesInDirectory = gekozenFileIndex;
        String absoluutPath = f.getAbsolutePath();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] schermen = ge.getScreenDevices();
        GraphicsDevice mijnScherm = schermen[0];
        frameHoogte = mijnScherm.getDisplayMode().getHeight();
        schermwijdte = mijnScherm.getDisplayMode().getWidth();
        frameHoogte = frameHoogte - 40;
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
        toetsLuiteraar toetsen = new toetsLuiteraar();
        frame.addKeyListener(toetsen);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            viewClass images = new viewClass("leeg");
        } else {
            if (args[0].equalsIgnoreCase("version")) {
                System.out.println("2021-02-22 18 23");
            } else {
                viewClass images = new viewClass(args[0]);
            }

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
//        System.out.println("javaapplication2.muisLuisteraar.mouseReleased() " + viewClass.schaal );
//            System.out.println("javaapplication2.muisLuisteraar.mouseReleased() " + event.getButton());
            if (event.getButton() == BUTTON1) {
                richting = -1;
                if (viewClass.indexfilesInDirectory == 0) {
                    richting = 0;
                };
            } else {
                richting = 1;
                if (viewClass.indexfilesInDirectory == filesInDirectory.size() - 1) {
                    richting = 0;
                };
            }
            viewClass.indexfilesInDirectory += richting;
            if (richting != 0) {
                File f = (File) filesInDirectory.elementAt(viewClass.indexfilesInDirectory);
                String absoluutPath = f.getAbsolutePath();
//                System.out.print(viewClass.indexfilesInDirectory);
//                System.out.print(" " + viewClass.filesInDirectory.size());
//                System.out.println(" " + f.getName());

                try {
                    ImageIcon nextOrPrevImageIcon = createImageIcon(absoluutPath, viewClass.frameHoogte, schermwijdte);
                    viewClass.frame.setTitle(viewClass.indexfilesInDirectory + " " + viewClass.filesInDirectory.size() + " " + absoluutPath);
                    viewClass.frame.getContentPane().removeAll();
                    viewClass.label = new JLabel(nextOrPrevImageIcon);
                    viewClass.frame.getContentPane().add(viewClass.label);
                    viewClass.frame.pack();

                } catch (IOException ex) {
                    Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("gdview.main.muisLuisteraar.mouseReleased() io exception" + absoluutPath);

                } catch (NullPointerException e) {
                    Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, e);
                    System.out.println("gdview.main.muisLuisteraar.mouseReleased() NullPointerException" + absoluutPath);
                }

            }//

        }
    }

    class toetsLuiteraar extends KeyAdapter {

        String strGetal;

        public toetsLuiteraar() {
            strGetal = "0";
        }

        @Override
        public void keyPressed​(KeyEvent e) {
            super.keyTyped(e);
//            System.out.println("toets " + e.getKeyCode());
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            } else {
                if (e.getKeyCode() == KeyEvent.VK_0) {
                    strGetal = strGetal + "0";
                }
                if (e.getKeyCode() == KeyEvent.VK_1) {
                    strGetal = strGetal + "1";
                }
                if (e.getKeyCode() == KeyEvent.VK_2) {
                    strGetal = strGetal + "2";
                }
                if (e.getKeyCode() == KeyEvent.VK_3) {
                    strGetal = strGetal + "3";
                }
                if (e.getKeyCode() == KeyEvent.VK_4) {
                    strGetal = strGetal + "4";
                }
                if (e.getKeyCode() == KeyEvent.VK_5) {
                    strGetal = strGetal + "5";
                }
                if (e.getKeyCode() == KeyEvent.VK_6) {
                    strGetal = strGetal + "6";
                }
                if (e.getKeyCode() == KeyEvent.VK_7) {
                    strGetal = strGetal + "7";
                }
                if (e.getKeyCode() == KeyEvent.VK_8) {
                    strGetal = strGetal + "8";
                }
                if (e.getKeyCode() == KeyEvent.VK_9) {
                    strGetal = strGetal + "9";
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    int lengte = strGetal.length();
                    lengte--;
                    if (lengte > 0) {
                        strGetal = (String) strGetal.subSequence(0, lengte);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int getal = 0;
                    getal = Integer.parseInt(strGetal);
                    strGetal = "0";
                    if (getal < viewClass.filesInDirectory.size()) {
                        viewClass.indexfilesInDirectory = getal;
                    }
                    File f = (File) filesInDirectory.elementAt(viewClass.indexfilesInDirectory);
                    String absoluutPath = f.getAbsolutePath();
//                System.out.print(viewClass.indexfilesInDirectory);
//                System.out.print(" " + viewClass.filesInDirectory.size());
                System.out.println(" " + f.getName());

                    ImageIcon nextOrPrevImageIcon;
                    try {
                        nextOrPrevImageIcon = createImageIcon(absoluutPath, viewClass.frameHoogte, schermwijdte);
                        viewClass.frame.setTitle(viewClass.indexfilesInDirectory + " " + viewClass.filesInDirectory.size() + " " + absoluutPath);
                        viewClass.frame.getContentPane().removeAll();
                        viewClass.label = new JLabel(nextOrPrevImageIcon);
                        viewClass.frame.getContentPane().add(viewClass.label);
                        viewClass.frame.pack();
                    } catch (IOException ex) {
                        Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NullPointerException ex) {
                        Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
                    }

//                        strGetal = (String) strGetal.subSequence(0, lengte);
                }
            }

        }

    }
}

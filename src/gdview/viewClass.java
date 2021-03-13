package gdview;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import static java.awt.event.MouseEvent.BUTTON1;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import java.awt.Image;
import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;

public class viewClass {

    public static File imageFile;
    public static JLabel label;
//    public static double schaal;
    public static JFrame frame;
    static JPanel pane;
    public static int imageHoogte, imageBreedte, indexfilesInDirectory, maxAantalImages;
    public static int frameHoogte, schermwijdte;
    public static Vector filesInDirectory;
    public static int gekozenFileIndex;
    private static boolean recursief;

    public viewClass(String parameterFile) throws IOException {
        filesInDirectory = new Vector();
        if (parameterFile.compareTo("leeg") == 0) {
            gekozenFileIndex = 0;
            indexfilesInDirectory = 1;
            recursief = Boolean.TRUE;
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
        frame.setTitle(viewClass.indexfilesInDirectory + " " + viewClass.maxAantalImages + " " + absoluutPath + " 2021-mrt-13 v14");
//        frame.setBackground(Color.BLACK);
        pane = new JPanel();

//        pane.setCursor();
        pane.setLayout(null);
        label = new JLabel(createImageIcon(absoluutPath, frameHoogte, schermwijdte));
        frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));

        frame.add(pane);
        frame.getContentPane().add(label);
//        frame.getContentPane().setBounds(0, 0, imageBreedte, imageHoogte);

//        System.out.println("hoogte frame =" + frame.getHeight());
        frame.setBounds(0, 0, imageBreedte + 20, imageHoogte + 20);
//        frame.setBounds(0, 0, imageBreedte , imageHoogte );
        muisLuisteraar mijnMuisLuisteraar = new muisLuisteraar();
        frame.addMouseListener(mijnMuisLuisteraar);
        toetsLuiteraar toetsen = new toetsLuiteraar();
        frame.addKeyListener(toetsen);
        frame.setVisible(true);
    }

    public void verwerkNewImage() {
        /* gebruikte variabel : indexFilesInDirectory    
     deze methove verwerkt de geselceteerde file in de files in directory array
     van deze file word een icon gemaakt 
    dit icon wordt toegevoegd aan het  frame
         */

        File f = (File) filesInDirectory.elementAt(viewClass.indexfilesInDirectory);
        String absoluutPath = f.getAbsolutePath();
        try {
//            long starttijd = System.currentTimeMillis();
            ImageIcon nextOrPrevImageIcon = createImageIcon(absoluutPath, frameHoogte, schermwijdte);
            viewClass.frame.setTitle(viewClass.indexfilesInDirectory + " " + maxAantalImages + " " + absoluutPath);
//            viewClass.frame.getContentPane()1.removeAll();
//            viewClass.label = new JLabel(nextOrPrevImageIcon);
            viewClass.label.setIcon(nextOrPrevImageIcon);
            viewClass.frame.getContentPane().add(viewClass.label);
//            viewClass.frame.getContentPane().setBounds(0, 0, imageBreedte, imageHoogte);
//            System.out.print(" hoogte frame voor bounds  =" + frame.getHeight());

            frame.setBounds(0, 0, imageBreedte + 20, imageHoogte + 20);
//            System.out.print(" hoogte frame na bounds  =" + frame.getHeight());
//            frame.setBounds(0, 0, imageBreedte, imageHoogte); // new 27 feb 2021
            viewClass.frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));

//            viewClass.frame.pack();
            frame.setVisible(true);
//            long eindtijd = System.currentTimeMillis();
//            System.out.print(" bestede tijd:");
//            System.out.print(eindtijd - starttijd);
//            System.out.println(" " + absoluutPath);
        } catch (IOException ex) {
            Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("gdview.main.muisLuisteraar.mouseReleased() io exception" + absoluutPath);
        } catch (NullPointerException e) {
            Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("gdview.main.muisLuisteraar.mouseReleased() NullPointerException" + absoluutPath);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            viewClass images = new viewClass("leeg");
        } else {
            if (args[0].equalsIgnoreCase("dir")) {
                viewClass images = new viewClass("dir");
            }
//            int[] pixels = new int[16 * 16];
//            Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
//            Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
            viewClass images = new viewClass(args[0]);
        }
    }

    public String getStartdirectory() throws IOException {
        String eruit = "";
        JFileChooser fc = new JFileChooser();
        if (recursief) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        } else {
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                "JPG & GIF Images", "jpg", "gif", "png", "jpeg", "tiff");
//        fc.setFileFilter(filter);
        }
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
            imageVooricon = imageToBeDisplayed.getScaledInstance(-1, schermHoogte, Image.SCALE_FAST);
            imageHoogte = imageVooricon.getHeight(frame);
            imageBreedte = imageVooricon.getWidth(frame);
//            System.out.print("schalen op hoogte , H = " + imageHoogte + " B=" + imageBreedte + " ");
//            schaal = schermHoogte / (double) heightImageToBeDisplayed;

        } else {
            // schalen op breedte
            imageVooricon = imageToBeDisplayed.getScaledInstance(schermBreedte, -1, Image.SCALE_FAST);
            imageHoogte = imageVooricon.getHeight(frame);
            imageBreedte = imageVooricon.getWidth(frame);
//            System.out.print("schalen op breedte , H = " + imageHoogte + " B=" + imageBreedte + " ");
//            schaal = schermBreedte / (double) widthImageToBeDisplayed;
        }
        return new ImageIcon(imageVooricon);
    }

    Vector getFilesInDirectory(String erin) throws IOException {
//        Vector eruit = new Vector();
        File startFile = new File(erin);
        File startDirFile;
        File[] listOfFiles = null;
        File folder;
        Vector fileVector;
        fileVector = new Vector();
        recursief = Boolean.FALSE;
        gekozenFileIndex = 0;

        if (startFile.isDirectory()) {
            recursief = Boolean.TRUE;
            getFilesRecursive(startFile); // vult public var filesInDirectory
            Collections.sort(filesInDirectory, Comparator.comparing(File::getAbsolutePath, new FilenameComparator()));

//            for (int i = 0; i < filesInDirectory.size(); i++) {
//                File f = (File) filesInDirectory.elementAt(i);
//                String absoluutPath = f.getAbsolutePath();
//
////                System.out.println(absoluutPath);
//            }
            fileVector = filesInDirectory;

        } else {

            startDirFile = startFile.getParentFile();
            folder = new File(startDirFile.getAbsolutePath());
            listOfFiles = folder.listFiles();
//
//            Arrays.sort(listOfFiles);
//            for (File file : listOfFiles) {
//                if (file.isFile()) {
//                    System.out.println(file.getName());
//                }
//            }
//            System.out.println("gdview.viewClass.getFilesInDirectory()");
            Arrays.sort(listOfFiles, Comparator.comparing(File::getName, new FilenameComparator()));

//            for (File file : listOfFiles) {
//                if (file.isFile()) {
//                    System.out.println(file.getName());
//                }
//            }
            // for each name in the path array
            for (File fileInDir : listOfFiles) {
                if (fileInDir.isFile()) {
                    try {
                        String extension = fileInDir.getName().substring(fileInDir.getName().lastIndexOf("."));
                        if (extension.toLowerCase().contains(".png")) {
//                        System.out.println(fileInDir);
                            fileVector.addElement(fileInDir);
                            if (fileInDir.equals(startFile)) {
                                gekozenFileIndex = fileVector.size() - 1;

                            }
                        } else {
                            if (extension.toLowerCase().contains(".jpg")) {
//                        System.out.println(fileInDir);
                                fileVector.addElement(fileInDir);
                                if (fileInDir.equals(startFile)) {
                                    gekozenFileIndex = fileVector.size() - 1;

                                }
                            } else {
                                if (extension.toLowerCase().contains(".jpeg")) {
//                        System.out.println(fileInDir);
                                    fileVector.addElement(fileInDir);
                                    if (fileInDir.equals(startFile)) {
                                        gekozenFileIndex = fileVector.size() - 1;

                                    }
                                } else {
                                    if (extension.toLowerCase().contains(".tiff")) {
//                        System.out.println(fileInDir);
                                        fileVector.addElement(fileInDir);
                                        if (fileInDir.equals(startFile)) {
                                            gekozenFileIndex = fileVector.size() - 1;

                                        }
                                    }
                                }
                            }
                        }
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                        System.out.println("gdview.viewClass.getFilesInDirectory() out of bounds exception " + fileInDir.getCanonicalPath());
                    }
                }

            }

        }
        maxAantalImages = fileVector.size() - 1;
        return fileVector;
    }

    class muisLuisteraar extends MouseInputAdapter {

        public void mousePressed(MouseEvent event) {
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
                verwerkNewImage();
            }
        }
    }

    class toetsLuiteraar extends KeyAdapter {

        String strGetal;

        public toetsLuiteraar() {
            strGetal = "";
        }

        @Override
        public void keyPressed​(KeyEvent e) {

            super.keyTyped(e);

//            System.out.println("toets " + e.getKeyCode());
            int richting = 0;
            if ((e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_RIGHT)) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    richting = -1;
                    if (viewClass.indexfilesInDirectory == 0) {
                        richting = 0;
                    };
                } else {
                    richting = 1;
                    if (viewClass.indexfilesInDirectory == filesInDirectory.size() - 1) {
                        richting = 0;
                    }
                }
                viewClass.indexfilesInDirectory += richting;
                if (richting != 0) {
                    verwerkNewImage();
                }
            } else {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                } else {
                    if ((e.getKeyCode() >= KeyEvent.VK_0) && (e.getKeyCode() <= KeyEvent.VK_9)) {
                        String strKeyWaarde = "";
                        int numKeyWaarde = e.getKeyCode() - 48;
                        strKeyWaarde = String.valueOf(numKeyWaarde);

                        strGetal = strGetal + strKeyWaarde;
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            int lengte = strGetal.length();
                            lengte--;
                            if (lengte > 0) {
                                strGetal = (String) strGetal.subSequence(0, lengte);
                            }
                        }
                    }

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        int getal = 0;
                        if (!strGetal.isBlank()) {
                            if (Integer.parseInt(strGetal) != 0) {
                                getal = Integer.parseInt(strGetal);
                                if (getal < viewClass.filesInDirectory.size()) {
                                    viewClass.indexfilesInDirectory = getal;
                                }
                                verwerkNewImage();
                            }
                            strGetal = "";
                        }

                    }
                }
            }
        }
    }

    private static void getFilesRecursive(File pFile) {
        for (File files : pFile.listFiles()) {
            if (files.isDirectory()) {
                if (recursief) {
                    getFilesRecursive(files);
                }
            } else {
                if (isImage(files)) {
                    filesInDirectory.addElement(files);
                }
            }
        }

    }

    static boolean isImage(File erin) {

//        System.out.println("gdview.viewClass.isImage()" + erin.getAbsolutePath());
        Boolean eruit = Boolean.FALSE;
        try {
            String extension = erin.getName().substring(erin.getName().lastIndexOf("."));
            if (extension.toLowerCase().contains(".png")) {
                eruit = Boolean.TRUE;
            } else {
                if (extension.toLowerCase().contains(".jpg")) {
                    eruit = Boolean.TRUE;
                } else {
                    if (extension.toLowerCase().contains(".jpeg")) {
                        eruit = Boolean.TRUE;
                    } else {
                        if (extension.toLowerCase().contains(".tiff")) {
                            eruit = Boolean.TRUE;
                        }
                    }
                }
            }
        } catch (java.lang.StringIndexOutOfBoundsException e) {
            try {
                System.out.println("gdview.viewClass.getFilesInDirectory() out of bounds exception " + erin.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return eruit;
    }
}

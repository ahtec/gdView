package gdview;

import java.awt.Color;
//import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
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
import javax.swing.JOptionPane;

public class viewClass extends JFrame {

//    public static File imageFile;
    public static JLabel label, label2;
    public static JFrame frame, frame2;
    static JPanel pane, pane2;
    public String titeltxt;

    public static int imageHoogte, imageBreedte, indexfilesInDirectory, maxAantalImages;
    public static int frameHoogte, schermwijdte, vertRuimteBoven = 10, vertRuimteOnder = 10, horzRuimte = 0;
    public static Vector filesInDirectory;
    public static int gekozenFileIndex, richting;
    private static boolean recursief;
//    String versie = "13 aug  2021 V38";
    public String versie = "28 nov  2021 V41";
    private int totaleBreedte;

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
            recursief = Boolean.FALSE;
            filesInDirectory = getFilesInDirectory(parameterFile);
        }

        if (filesInDirectory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Geen imgages gevonden", versie, 1);
        } else {
            File f = (File) filesInDirectory.elementAt(gekozenFileIndex);
            indexfilesInDirectory = gekozenFileIndex;
            String absoluutPath = f.getAbsolutePath();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] schermen = ge.getScreenDevices();
            GraphicsDevice mijnScherm = schermen[0];
            frameHoogte = mijnScherm.getDisplayMode().getHeight();
            schermwijdte = mijnScherm.getDisplayMode().getWidth();
            System.out.println("scherminstellingen hoogte =" + frameHoogte + "wijdte =" + schermwijdte);
            frame = new JFrame(absoluutPath);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
//            frame.setTitle(viewClass.indexfilesInDirectory + " " + viewClass.maxAantalImages + " " + absoluutPath + versie);

            frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

            // eventueel 2e frame displayen
            FileDrop fileDrop = new FileDrop(frame, new FileDrop.Listener() {
                private Object startFile;

                public void filesDropped(java.io.File[] files) {
                    for (int i = 0; i < files.length; i++) {
                        if (isImage(files[i])) {
                            filesInDirectory.addElement(files[i]);
                            maxAantalImages++;
                            if (i == 0) {
                                indexfilesInDirectory = maxAantalImages;
                            }
                        } else {
                            if (files[i].isDirectory()) {
                                File[] listOfFiles = files[i].listFiles();
                                Arrays.sort(listOfFiles, Comparator.comparing(File::getName, new FilenameComparator()));
                                for (File fileInDir : listOfFiles) {
                                    if (isImage(fileInDir)) {
                                        filesInDirectory.addElement(fileInDir);
                                        maxAantalImages++;
                                    }
                                }

                            }
                        }
                    }
                    verwerkNewImage();
                }

            }
            ); // end FileDrop.Listener

            Image tempImage = createImageIcon();
            ImageIcon im = new ImageIcon(tempImage);
            label = new JLabel(im);

            // ****************************hier voeg ik de pane aan het frame toe
//            frame.setLayout(new FlowLayout());
//            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(label);
            frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
            frame.setSize(schermwijdte, frameHoogte);
            frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
            muisLuisteraar mijnMuisLuisteraar = new muisLuisteraar();
            frame.addMouseListener(mijnMuisLuisteraar);
            toetsLuiteraar toetsen = new toetsLuiteraar();
            frame.addKeyListener(toetsen);
            frame.setVisible(true);
        }
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
            Image tempImage = createImageIcon();
            ImageIcon im = new ImageIcon(tempImage);
            label = new JLabel(im);
//            viewClass.frame.setTitle(titeltxt);
            viewClass.frame.getContentPane().removeAll();
            viewClass.frame.getContentPane().add(viewClass.label);
            viewClass.frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
            frame.setVisible(true);
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
            viewClass images = new viewClass(args[0]);
        }
    }

    public String getStartdirectory() throws IOException {
        String eruit = "";
        JFileChooser fc = new JFileChooser();
//        if (recursief)
//        {
//            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

//        } else
//        {
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        FileNameExtensionFilter filter = new FileNameExtensionFilter(
//                "JPG & GIF Images", "jpg", "gif", "png", "jpeg", "tiff");
//        fc.setFileFilter(filter);
//        }
        int returnVal = fc.showOpenDialog(pane);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            eruit = file.getCanonicalPath();
        } else {
            System.exit(0);
        }

        return eruit;
    }

    private Image createImageIcon() throws IOException {
        File imageFile = (File) filesInDirectory.elementAt(viewClass.indexfilesInDirectory);
//        titeltxt = "[ " + viewClass.indexfilesInDirectory + " /  " + maxAantalImages + "] " + imageFile.getName();
        Image imageRead = ImageIO.read(imageFile);
//        Image imageRead2;

        Image bi = new BufferedImage(schermwijdte, frameHoogte, BufferedImage.TYPE_INT_RGB);
        Font myFont = new Font("Courier", Font.BOLD, 14);

        Graphics g = bi.getGraphics();
        g.setColor(Color.red);
        g.setFont(myFont);

//        g.clearRect(0, 0, schermwijdte, imageHoogte);
        imageRead = schalen(imageRead);
        totaleBreedte = imageBreedte;
        g.drawImage(imageRead, horzRuimte, vertRuimteBoven, null);
//        g.drawString(imageFile.getName() + " " + imageRead.getHeight(pane) +" " + indexfilesInDirectory, 20, frameHoogte );
//        g.drawString(imageFile.getAbsolutePath() + " " + indexfilesInDirectory + "van " + maxAantalImages, 0, frameHoogte);
        titeltxt = imageFile.getAbsolutePath() + "  " + indexfilesInDirectory + " van " + maxAantalImages;

        // is er nog een volgende imago
        if (!(richting == 0)) {

            if ((viewClass.indexfilesInDirectory + richting) <= maxAantalImages) {
                if ((viewClass.indexfilesInDirectory + richting) >= 0) {
                    if (imageBreedte < imageHoogte) {
                        // dan is i portrait
                        // hoeveel ruimte is er over?
                        int ruimteRechts = schermwijdte - imageBreedte;
                        // lukt dat met het volden image?

                        int vorigeImageBreedte = imageBreedte + horzRuimte;
//                viewClass.indexfilesInDirectory++;
                        viewClass.indexfilesInDirectory += richting;
                        imageFile = (File) filesInDirectory.elementAt(viewClass.indexfilesInDirectory);

                        imageRead = schalen(ImageIO.read(imageFile));
                        totaleBreedte += imageBreedte;
                        // past i erop?
                        if (ruimteRechts > imageBreedte) {

                            if (imageBreedte + vorigeImageBreedte < schermwijdte) {
                                titeltxt += " & " + imageFile.getName();

//                                g.drawLine(vorigeImageBreedte, 0, vorigeImageBreedte, imageHoogte);
                                g.drawImage(imageRead, vorigeImageBreedte, vertRuimteBoven, null);

//                                g.drawString(imageFile.getName() +" " + totaleBreedte + " " + indexfilesInDirectory, vorigeImageBreedte + 20, frameHoogte - 40);
//                                System.out.println(imageFile.getName());
                            }
                        } else {
                            viewClass.indexfilesInDirectory -= richting;
                        }
                    }
                }
            }
        }

        return (bi);
    }

    Image schalen(Image imageRead) {
        double quotientSchermWH, quotientImageWH;
        int heightImageToBeDisplayed = imageRead.getHeight(null);
        int widthImageToBeDisplayed = imageRead.getWidth(null);

        quotientSchermWH = schermwijdte / (double) frameHoogte;
        quotientImageWH = widthImageToBeDisplayed / (double) heightImageToBeDisplayed;

        if (quotientSchermWH > quotientImageWH) {
            // schalen op hoogte
            imageRead = imageRead.getScaledInstance(-1, frameHoogte - vertRuimteBoven - vertRuimteOnder, Image.SCALE_SMOOTH);
            imageHoogte = imageRead.getHeight(frame);
            imageBreedte = imageRead.getWidth(frame);
            System.out.println("schalen op hoogte , H = " + imageHoogte + " B=" + imageBreedte + " ");
            System.out.println("geschaald met , H = " + (frameHoogte - vertRuimteBoven - vertRuimteOnder));

        } else {
            // schalen op breedte
            imageRead = imageRead.getScaledInstance(schermwijdte - horzRuimte, -1, Image.SCALE_FAST);
            imageHoogte = imageRead.getHeight(frame);
            imageBreedte = imageRead.getWidth(frame);
            System.out.println("schalen op breedte , H = " + imageHoogte + " B=" + imageBreedte + " schermwijdte=" + schermwijdte + " horz ruimte=" + horzRuimte);
        }

        return (imageRead);
    }

    Vector getFilesInDirectory(String erin) throws IOException {
        File startFile = new File(erin);
        File startDirFile;
        File[] listOfFiles = null;
        File folder;
        Vector fileVector;
        fileVector = new Vector();
        gekozenFileIndex = 0;

        if (startFile.isDirectory()) {
            getFilesRecursive(startFile); // vult public var filesInDirectory
            Collections.sort(filesInDirectory, Comparator.comparing(File::getAbsolutePath, new FilenameComparator()));

            fileVector = filesInDirectory;

        } else {

            startDirFile = startFile.getParentFile();
            folder = new File(startDirFile.getAbsolutePath());
            listOfFiles = folder.listFiles();
            Arrays.sort(listOfFiles, Comparator.comparing(File::getName, new FilenameComparator()));

            // for each name in the path array
            for (File fileInDir : listOfFiles) {
                if (fileInDir.isFile()) {
                    try {
                        String extension = fileInDir.getName().substring(fileInDir.getName().lastIndexOf("."));
                        if (extension.toLowerCase().contains(".png")) {
                            fileVector.addElement(fileInDir);
                            if (fileInDir.equals(startFile)) {
                                gekozenFileIndex = fileVector.size() - 1;

                            }
                        } else {
                            if (extension.toLowerCase().contains(".jpg")) {
                                fileVector.addElement(fileInDir);
                                if (fileInDir.equals(startFile)) {
                                    gekozenFileIndex = fileVector.size() - 1;

                                }
                            } else {
                                if (extension.toLowerCase().contains(".jpeg")) {
                                    fileVector.addElement(fileInDir);
                                    if (fileInDir.equals(startFile)) {
                                        gekozenFileIndex = fileVector.size() - 1;

                                    }
                                } else {
                                    if (extension.toLowerCase().contains(".tiff")) {
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

//        public void mousePressed(MouseEvent event) {
        public void mouseReleased(MouseEvent event) {
            int x = event.getX();
            int y = event.getY();
//            richting = 0;

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
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
                }
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
//             richting = 0;
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
                    } else // dus geen getal
                    {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            int lengte = strGetal.length();
                            lengte--;
                            if (lengte > 0) {
                                strGetal = (String) strGetal.subSequence(0, lengte);
                            }
                        } else {
                            // dus geen bacspace 
                            if (e.getKeyCode() == KeyEvent.VK_D) {
                                // d is gedrukt
//                              System.out.println("gdview.viewClass.toetsLuiteraar.keyPressed(D)");
                                JFileChooser fc = new JFileChooser();
                                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                int returnVal = fc.showOpenDialog(pane);
                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    File geselecteerdeDirectory = fc.getSelectedFile();
                                    File[] listOfFiles = geselecteerdeDirectory.listFiles();
                                    Arrays.sort(listOfFiles, Comparator.comparing(File::getName, new FilenameComparator()));
                                    for (File fileInDir : listOfFiles) {
                                        if (isImage(fileInDir)) {
                                            filesInDirectory.addElement(fileInDir);
                                            maxAantalImages++;
//                                            System.out.println(maxAantalImages);
                                        }
                                    }
                                    verwerkNewImage();

                                }
                            }
                        }
                    if (e.getKeyCode() == KeyEvent.VK_F1) {
                        // f1 gedrukt
                                    JOptionPane.showMessageDialog(null, titeltxt,versie  , 1);

                    }
                        
                    }

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        int getal = 0;
                        if (!strGetal.isBlank()) {
//                            if (Integer.parseInt(strGetal) != 0)
                            {
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
        String naamFile = erin.getName();
        if (naamFile.lastIndexOf(".") > 0) {
            try {
//                String extension = erin.getName().substring(erin.getName().lastIndexOf("."));
                String extension = naamFile.substring(naamFile.lastIndexOf("."));
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
//                    Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, e);
                    System.out.println("gdview.viewClass.getFilesInDirectory() out of bounds exception " + erin.getCanonicalPath());
                } catch (IOException ex) {
                    Logger.getLogger(viewClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return eruit;
    }
}

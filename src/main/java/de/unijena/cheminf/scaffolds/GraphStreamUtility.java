/*
 * Copyright (c) 2023 Julian Zander <zanderjulian@gmx.de>
 *                    Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package de.unijena.cheminf.scaffolds;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.images.Resolutions;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.scaffold.ScaffoldNodeBase;
import org.openscience.cdk.tools.scaffold.ScaffoldNodeCollectionBase;
import org.openscience.cdk.tools.scaffold.ScaffoldTree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Contains functionalities to visualise CDK-Scaffold-generated scaffold networks and trees in a
 * very basic way, employing the open GraphStream graph library.
 * Please note that this is a very basic functionality primarily meant for visual inspection and debugging.
 * GraphStream might also throw errors in some cases where the problem lies with the library, not
 * this functionality here.
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 * @version 1.1.0.0
 */
public class GraphStreamUtility {
    /**
     * Default style sheet that is used for the created graphs if no custom style is defined.
     */
    public static final String DEFAULT_GRAPH_STYLE_SHEET = "node { shape: rounded-box; size-mode: fit; padding: 60px; } graph { shape: box; size-mode: fit; padding: 100px; }";
    //
    /**
     *Default ID given to the created graphs if no ID is given.
     */
    public static final String DEFAULT_GRAPH_ID = "scaffold-graph";
    //
    /**
     * Default graph viewer used for visualisation.
     */
    public static final String DEFAULT_GRAPHSTREAM_UI = "swing";
    //
    /**
     * Default setting value for whether to label nodes with their index and level in the created graphs.
     */
    public static final boolean DEFAULT_ARE_NODES_LABELLED = true;
    //
    /**
     * Default CDK depiction generator used to create structure images for the graph nodes
     */
    public static final DepictionGenerator DEFAULT_CDK_DEPICTION_GENERATOR = new DepictionGenerator().withSize(2048,2048).withFillToFit();
    //
    /**
     * the cdk-scaffold ScaffoldNodeBase instances represented by a node in the created graph is stored under this property key.
     */
    public static final String GRAPH_NODE_PROPERTY_KEY_SCAFFOLD_NODE_BASE_INSTANCE = "ScaffoldNodeBase";
    //
    /**
     * Folder to store screenshots, temporary image files, etc.
     */
    private static File graphStreamDisplayFolder = new File((new File("")).getAbsolutePath()
            + File.separatorChar + "GraphStreamDisplay"
            + File.separatorChar);
    //
    /**
     * Folder for temporary files like the node structure depictions.
     */
    private static File tempFolder = new File(graphStreamDisplayFolder.getAbsolutePath()
            + File.separatorChar + "temp"
            + File.separatorChar);
    //
    /**
     * Static class initializer that sets the "org.graphstream.ui" system property to the default
     * value if it is unset.
     */
    static {
        if (Objects.isNull(System.getProperty("org.graphStream.ui"))) {
            System.setProperty("org.graphstream.ui", GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
        }
    }
    //
    /**
     * Private constructor to hide the implicit public one.
     */
    private GraphStreamUtility() {
        throw new IllegalStateException("Utility class");
    }
    //
    /**
     * Folder to store screenshots, temporary image files, etc.
     *
     * @return output folder
     */
    public static File getGraphStreamDisplayFolder() {
        return graphStreamDisplayFolder;
    }
    //
    /**
     * Sets the folder to store screenshots, temporary image files, etc.
     *
     * @param aDirectory folder directory where the JRE can write and read
     * @throws NullPointerException if the given folder is null
     * @throws IllegalArgumentException if the JRE is unable to read and write in the given folder
     */
    public static void setGraphStreamDisplayFolder(File aDirectory) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aDirectory, "Given folder is null.");
        try {
            boolean tmpWasCreationSuccessful = false;
            if (!aDirectory.exists()) {
                tmpWasCreationSuccessful = aDirectory.mkdirs();
            }
            if (!aDirectory.canRead() || !aDirectory.canWrite() || !aDirectory.isDirectory() || !tmpWasCreationSuccessful) {
                throw new IllegalArgumentException("Given directory " + aDirectory.getAbsolutePath() +" is not a directory or cannot be read from or written to.");
            }
            GraphStreamUtility.graphStreamDisplayFolder = aDirectory;
        } catch (SecurityException aSecurityException) {
            throw new IllegalArgumentException("Given directory " + aDirectory.getAbsolutePath() +" is protected by a security manager.");
        }
    }
    //
    /**
     * Folder for temporary files like the node structure depictions.
     *
     * @return folder for temporary files
     */
    public static File getTempFolder() {
        return GraphStreamUtility.tempFolder;
    }
    //
    /**
     * Sets the folder for temporary files like the node structure depictions.
     *
     * @param aDirectory folder directory where the JRE can write and read
     * @throws NullPointerException if the given folder is null
     * @throws IllegalArgumentException if the JRE is unable to read and write in the given folder
     */
    public static void setTempFolder(File aDirectory)  throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aDirectory, "Given folder is null.");
        try {
            boolean tmpWasCreationSuccessful = false;
            if (!aDirectory.exists()) {
                tmpWasCreationSuccessful = aDirectory.mkdirs();
            }
            if (!aDirectory.canRead() || !aDirectory.canWrite() || !aDirectory.isDirectory() || !tmpWasCreationSuccessful) {
                throw new IllegalArgumentException("Given directory " + aDirectory.getAbsolutePath() +" is not a directory or cannot be read from or written to.");
            }
            GraphStreamUtility.tempFolder = aDirectory;
        } catch (SecurityException aSecurityException) {
            throw new IllegalArgumentException("Given directory " + aDirectory.getAbsolutePath() +" is protected by a security manager.");
        }
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is displayed in an application window using GraphStream.
     * <br>NOTE: It is not recommended displaying multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aScaffoldNodeCollection displayed scaffold graph
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static void displayWithGraphStream(ScaffoldNodeCollectionBase aScaffoldNodeCollection)
            throws NullPointerException, IllegalArgumentException, IOException {
        GraphStreamUtility.displayWithGraphStream(aScaffoldNodeCollection,
                GraphStreamUtility.DEFAULT_ARE_NODES_LABELLED,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI
                );
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is displayed in an application window using GraphStream.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     * <br>NOTE: It is not recommended displaying multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aScaffoldNodeCollection displayed scaffold graph
     * @param areNodesLabelled adds a label with node level and node index if true
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static void displayWithGraphStream(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                              boolean areNodesLabelled)
            throws NullPointerException, IllegalArgumentException, IOException
    {
        GraphStreamUtility.displayWithGraphStream(aScaffoldNodeCollection,
                areNodesLabelled,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI
                );
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is displayed in an application window using GraphStream.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     * <br>NOTE: It is not recommended displaying multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aScaffoldNodeCollection displayed scaffold graph
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static void displayWithGraphStream(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                              boolean areNodesLabelled,
                                              DepictionGenerator aDepictionGenerator)
            throws NullPointerException, IllegalArgumentException, IOException
    {
        GraphStreamUtility.displayWithGraphStream(aScaffoldNodeCollection,
                areNodesLabelled,
                aDepictionGenerator,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is displayed in an application window using GraphStream.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     * <br>NOTE: It is not recommended displaying multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aScaffoldNodeCollection displayed scaffold graph
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @param aStyleSheet style sheet property for the graph
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static void displayWithGraphStream(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                              boolean areNodesLabelled,
                                              DepictionGenerator aDepictionGenerator,
                                              String aStyleSheet)
            throws NullPointerException, IllegalArgumentException, IOException
    {
        GraphStreamUtility.displayWithGraphStream(aScaffoldNodeCollection,
                areNodesLabelled,
                aDepictionGenerator,
                aStyleSheet,
                GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is displayed in an application window using GraphStream.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     * The GraphStream UI property defines which graphics library is employed, Java Swing (more reliable) or JavaFX (more experimental).
     * <br>NOTE: It is not recommended displaying multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aScaffoldNodeCollection displayed scaffold graph
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @param aStyleSheet style sheet property for the graph
     * @param aGraphStreamUIProperty "swing" or "javafx" (make sure to have installed the respective dependencies)
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static void displayWithGraphStream(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                              boolean areNodesLabelled,
                                              DepictionGenerator aDepictionGenerator,
                                              String aStyleSheet,
                                              String aGraphStreamUIProperty)
            throws NullPointerException, IllegalArgumentException, IOException
    {
        Objects.requireNonNull(aScaffoldNodeCollection, "Given scaffold graph is null.");
        Objects.requireNonNull(aDepictionGenerator, "Given depiction generator is null.");
        Objects.requireNonNull(aStyleSheet, "Given style sheet is null");
        Objects.requireNonNull(aGraphStreamUIProperty, "Given GraphStream UI property is null.");
        if (aScaffoldNodeCollection instanceof ScaffoldTree) {
            if (!((ScaffoldTree) aScaffoldNodeCollection).isValid()) {
                throw new IllegalArgumentException("Given scaffold tree is invalid (unconnected or without a single root node).");
            }
        }
        if (aStyleSheet.isBlank()) {
            throw new IllegalArgumentException("Given style sheet is blank.");
        }
        if (!(aGraphStreamUIProperty.equals("swing") || aGraphStreamUIProperty.equals("javafx"))) {
            throw new IllegalArgumentException("Given GraphStream UI property must equal \"swing\" or \"javafx\".");
        }
        System.setProperty("org.graphstream.ui", aGraphStreamUIProperty);
        Graph tmpGraph = GraphStreamUtility.generateGraphFromScaffoldNodeCollection(
                aScaffoldNodeCollection,
                areNodesLabelled,
                aDepictionGenerator,
                aStyleSheet,
                new SingleGraph(GraphStreamUtility.DEFAULT_GRAPH_ID));
        System.setProperty("org.graphstream.ui", aGraphStreamUIProperty);
        tmpGraph.display();
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     *
     * @param aScaffoldNodeCollection scaffold graph (scaffold tree or network) to parse
     * @return GraphStream Graph instance representing the given scaffold graph with structure depictions of the scaffolds on its nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(ScaffoldNodeCollectionBase aScaffoldNodeCollection)
            throws IOException
    {
        return GraphStreamUtility.generateGraphFromScaffoldNodeCollection(aScaffoldNodeCollection,
                GraphStreamUtility.DEFAULT_ARE_NODES_LABELLED,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                new SingleGraph(GraphStreamUtility.DEFAULT_GRAPH_ID));
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection scaffold graph (scaffold tree or network) to parse
     * @param areNodesLabelled adds a label with node level and node index if true
     * @return GraphStream Graph instance representing the given scaffold graph with structure depictions of the scaffolds on its nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                                                boolean areNodesLabelled)
            throws IOException
    {
        return GraphStreamUtility.generateGraphFromScaffoldNodeCollection(aScaffoldNodeCollection,
                areNodesLabelled,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                new SingleGraph(GraphStreamUtility.DEFAULT_GRAPH_ID));
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection scaffold graph (scaffold tree or network) to parse
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aCDKDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @return GraphStream Graph instance representing the given scaffold graph with structure depictions of the scaffolds on its nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                                                boolean areNodesLabelled,
                                                                DepictionGenerator aCDKDepictionGenerator)
            throws IOException
    {
        return GraphStreamUtility.generateGraphFromScaffoldNodeCollection(aScaffoldNodeCollection,
                areNodesLabelled,
                aCDKDepictionGenerator,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                new SingleGraph(GraphStreamUtility.DEFAULT_GRAPH_ID));
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection scaffold graph (scaffold tree or network) to parse
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aCDKDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @param aStyleSheet style sheet property for the graph
     * @return GraphStream Graph instance representing the given scaffold graph with structure depictions of the scaffolds on its nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                                                boolean areNodesLabelled,
                                                                DepictionGenerator aCDKDepictionGenerator,
                                                                String aStyleSheet)
            throws IOException
    {
        return GraphStreamUtility.generateGraphFromScaffoldNodeCollection(aScaffoldNodeCollection,
                areNodesLabelled,
                aCDKDepictionGenerator,
                aStyleSheet,
                new SingleGraph(GraphStreamUtility.DEFAULT_GRAPH_ID));
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection scaffold graph (scaffold tree or network) to parse
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aCDKDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @param aStyleSheet style sheet property for the graph
     * @param aGraphID id given to the newly created Graph instance
     * @return GraphStream Graph instance representing the given scaffold graph with structure depictions of the scaffolds on its nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                                                boolean areNodesLabelled,
                                                                DepictionGenerator aCDKDepictionGenerator,
                                                                String aStyleSheet,
                                                                String aGraphID)
            throws IOException
    {
        return GraphStreamUtility.generateGraphFromScaffoldNodeCollection(aScaffoldNodeCollection,
                areNodesLabelled,
                aCDKDepictionGenerator,
                aStyleSheet,
                new SingleGraph(aGraphID));
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection scaffold graph (scaffold tree or network) to parse
     * @param areNodesLabelled adds a label with node level and node index if true
     * @param aCDKDepictionGenerator CDK depiction generator used for generating the structure images on the graph nodes
     * @param aStyleSheet style sheet property for the graph
     * @param aGraph empty(!) Graph instance (convenience here, e.g. to connect a file sink image instance to the graph before it is constructed)
     * @return GraphStream Graph instance representing the given scaffold graph with structure depictions of the scaffolds on its nodes
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(ScaffoldNodeCollectionBase aScaffoldNodeCollection,
                                                                boolean areNodesLabelled,
                                                                DepictionGenerator aCDKDepictionGenerator,
                                                                String aStyleSheet,
                                                                Graph aGraph
                                                                )
            throws NullPointerException, IllegalArgumentException, IOException
    {
        Objects.requireNonNull(aScaffoldNodeCollection, "Given scaffold graph is null.");
        Objects.requireNonNull(aCDKDepictionGenerator, "Given depiction generator is null.");
        Objects.requireNonNull(aStyleSheet, "Given style sheet is null");
        Objects.requireNonNull(aGraph, "Given Graph instance is null.");
        if (aScaffoldNodeCollection instanceof ScaffoldTree) {
            if (!((ScaffoldTree) aScaffoldNodeCollection).isValid()) {
                throw new IllegalArgumentException("Given scaffold tree is invalid (unconnected or without a single root node).");
            }
        }
        if (aStyleSheet.isBlank()) {
            throw new IllegalArgumentException("Given style sheet is blank.");
        }
        if (aGraph.getNodeCount() != 0 || aGraph.getEdgeCount() != 0) {
            throw new IllegalArgumentException("Given Graph instance should be empty! " +
                    "If you want to merge multiple scaffold collections, use the respective methods on the cdk-scaffold level.");
        }
        aGraph.setAttribute("ui.stylesheet", aStyleSheet);
        aGraph.setAttribute("ui.quality");
        aGraph.setAttribute("ui.antialias");
        if (!GraphStreamUtility.tempFolder.exists()) {
            GraphStreamUtility.tempFolder.mkdirs();
        }
        Integer[][] tmpMatrix = aScaffoldNodeCollection.getMatrix();
        //used as edge index
        int tmpEdgeCount = 0;
        //for each matrix row, representing the matrix nodes
        for (int tmpRowIndex = 0; tmpRowIndex < tmpMatrix.length; tmpRowIndex++) {
            ScaffoldNodeBase<IAtomContainer> tmpCollectionLevelNode =  aScaffoldNodeCollection.getMatrixNode(tmpRowIndex);
            //each node is assigned its matrix index as id
            aGraph.addNode(String.valueOf(tmpRowIndex));
            Node tmpNode = aGraph.getNode(String.valueOf(tmpRowIndex));
            //the respective ScaffoldNodeBase instance of the tree or network node is stored on the respective graph node as attribute
            tmpNode.setAttribute(GraphStreamUtility.GRAPH_NODE_PROPERTY_KEY_SCAFFOLD_NODE_BASE_INSTANCE, tmpCollectionLevelNode);
            //Add a label to each node that corresponds to the level in the collection and its index in the matrix if true
            if (areNodesLabelled) {
                String tmpLabel = "Level: " + tmpCollectionLevelNode.getLevel() + "; Index: " + tmpRowIndex;
                tmpNode.setAttribute("ui.label", tmpLabel);
            }
            /*Add the structure image*/
            IAtomContainer tmpCollectionNodeMolecule = tmpCollectionLevelNode.getMolecule();
            try {
                BufferedImage tmpNodeImg = aCDKDepictionGenerator.depict(tmpCollectionNodeMolecule).toImg();
                /*The images need to be stored temporarily but are deleted on JRE exit*/
                File tmpTemporaryImageFile = File.createTempFile("GraphStream", ".png", GraphStreamUtility.tempFolder);
                ImageIO.write(tmpNodeImg, "png", tmpTemporaryImageFile);
                //set the images
                //alternative fill-mode: image-scaled-ratio-max
                tmpNode.setAttribute("ui.style", "fill-mode: image-scaled;" + "fill-image: url('" + tmpTemporaryImageFile.getAbsolutePath() + "');");
                tmpTemporaryImageFile.deleteOnExit();
            } catch (CDKException aCDKException) {
                Logger.getLogger(GraphStreamUtility.class.getName()).log(Level.WARNING,
                        "Unable to depict structure at index " + tmpRowIndex + ". Displaying empty node.");
            }
            /*Add edges*/
            for (int tmpColumnIndex = 0; tmpColumnIndex < tmpMatrix[tmpRowIndex].length; tmpColumnIndex++) {
                //Skip a diagonal half to get edges in one direction only.
                if (tmpRowIndex < tmpColumnIndex) {
                    continue;
                }
                //Insert an edge if there is 1 in the respective matrix cell
                if (tmpMatrix[tmpRowIndex][tmpColumnIndex].equals(1)) {
                    aGraph.addEdge(String.valueOf(tmpEdgeCount), tmpRowIndex, tmpColumnIndex);
                    tmpEdgeCount++;
                }
            }
        }
        return aGraph;
    }
    //
    /**
     * Creates a screenshot of the given graph using the "ui.screenshot" attribute.
     *
     * @param aGraph graph to screenshot
     * @param aFilePath file path were the screenshot should be created (existing files will be overridden)
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if an I/O error occurs while writing
     */
    public static void screenshotGraph(Graph aGraph, String aFilePath) throws NullPointerException, IllegalArgumentException, IOException {
        Objects.requireNonNull(aGraph, "Given Graph instance is null.");
        Objects.requireNonNull(aFilePath, "Given file path is null.");
        if (aFilePath.isBlank()) {
            throw new IllegalArgumentException("Given file path is blank.");
        }
        File tmpTargetFile = new File(aFilePath);
        try {
            if (!tmpTargetFile.exists()) {
                boolean tmpWasMKDirSuccessful = tmpTargetFile.getParentFile().mkdirs();
                boolean tmpWasFileCreationSuccessful = tmpTargetFile.createNewFile();
                if (!tmpWasFileCreationSuccessful || !tmpWasMKDirSuccessful) {
                    throw new IOException("File creation was unsuccessful in " + aFilePath);
                }
            }
            if (!tmpTargetFile.canRead() || !tmpTargetFile.canWrite() || !tmpTargetFile.isFile()) {
                throw new IllegalArgumentException("Given file path " + aFilePath +" is not a file or cannot be read from or written to.");
            }
        } catch (SecurityException aSecurityException) {
            throw new IllegalArgumentException("Given file path " + aFilePath +" is protected by a security manager.");
        }
        if (Objects.isNull(System.getProperty("org.graphStream.ui"))) {
            System.setProperty("org.graphstream.ui", GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
        }
        aGraph.setAttribute("ui.screenshot", aFilePath);
    }
    //
    /**
     * Creates a high quality screenshot of the given graph using the FileSinkImages method writeAll().
     * <br>NOTE: It is not recommended screenshotting multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aGraph graph to screenshot
     * @param aFilePath file path were the screenshot should be created (existing files will be overridden)
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if an I/O error occurs while writing
     */
    public static void screenshotGraphHighQuality(Graph aGraph, String aFilePath) throws NullPointerException, IllegalArgumentException, IOException {
        FileSinkImages tmpFileSinkImages = FileSinkImages.createDefault();
        tmpFileSinkImages.setOutputType(FileSinkImages.OutputType.png);
        tmpFileSinkImages.setQuality(FileSinkImages.Quality.HIGH);
        tmpFileSinkImages.setResolution(Resolutions.QSXGA);
        tmpFileSinkImages.setAutofit(false);
        tmpFileSinkImages.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        GraphStreamUtility.screenshotGraphHighQuality(aGraph, aFilePath, tmpFileSinkImages);
    }
    //
    /**
     * Creates a high quality screenshot of the given graph using the FileSinkImages method writeAll().
     * <br>NOTE: It is not recommended screenshotting multiple graphs at the same time using this method because of
     * multithreading issues.
     *
     * @param aGraph graph to screenshot
     * @param aFilePath file path were the screenshot should be created (existing files will be overridden)
     * @param aFileSinkImages FileSinkImages instance with custom configuration to create the screenshot
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if any parameter does not fulfil its requirements
     * @throws IOException if an I/O error occurs while writing
     */
    public static void screenshotGraphHighQuality(Graph aGraph, String aFilePath, FileSinkImages aFileSinkImages) throws NullPointerException, IllegalArgumentException, IOException {
        Objects.requireNonNull(aGraph, "Given Graph instance is null.");
        Objects.requireNonNull(aFilePath, "Given file path is null.");
        Objects.requireNonNull(aFileSinkImages, "Given FileSinkImages instance is null.");
        if (aFilePath.isBlank()) {
            throw new IllegalArgumentException("Given file path is blank.");
        }
        File tmpTargetFile = new File(aFilePath);
        try {
            if (!tmpTargetFile.exists()) {
                boolean tmpWasMKDirSuccessful = tmpTargetFile.getParentFile().mkdirs();
                boolean tmpWasFileCreationSuccessful = tmpTargetFile.createNewFile();
                if (!tmpWasFileCreationSuccessful || !tmpWasMKDirSuccessful) {
                    throw new IOException("File creation was unsuccessful in " + aFilePath);
                }
            }
            if (!tmpTargetFile.canRead() || !tmpTargetFile.canWrite() || !tmpTargetFile.isFile()) {
                throw new IllegalArgumentException("Given file path " + aFilePath +" is not a file or cannot be read from or written to.");
            }
        } catch (SecurityException aSecurityException) {
            throw new IllegalArgumentException("Given file path " + aFilePath +" is protected by a security manager.");
        }
        if (Objects.isNull(System.getProperty("org.graphStream.ui"))) {
            System.setProperty("org.graphstream.ui", GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
        }
        aFileSinkImages.writeAll(aGraph, aFilePath);
    }
}

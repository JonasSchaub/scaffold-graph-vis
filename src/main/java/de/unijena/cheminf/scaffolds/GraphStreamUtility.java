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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Contains functionalities to visualise CDK-Scaffold-generated scaffold networks and trees in a
 * very basic way, employing the GraphStream open graph library.
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 * @version 1.0.1.0
 */
public class GraphStreamUtility {
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is displayed in a Java Swing application window using GraphStream.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection displayed scaffold graph
     * @param areNodesLabelled adds a label with node level and node number if true.
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static void displayWithGraphStream(ScaffoldNodeCollectionBase aScaffoldNodeCollection, boolean areNodesLabelled) throws IOException {
        System.setProperty("org.graphstream.ui", "swing");
        Graph tmpGraph = GraphStreamUtility.generateGraphFromScaffoldNodeCollection(aScaffoldNodeCollection,
                areNodesLabelled,
                "Graph",
                "node { shape: rounded-box; size-mode: fit; padding: 60px; } graph { shape: box; size-mode: fit; padding: 70px; }",
                new DepictionGenerator().withSize(2048,2048).withFillToFit());
        /*Display graph*/
        System.setProperty("org.graphstream.ui", "swing");
        tmpGraph.display();
        /*
        FileSinkImages tmpFileSinkImages = FileSinkImages.createDefault();
        tmpFileSinkImages.setOutputPolicy(FileSinkImages.OutputPolicy.BY_GRAPH_EVENT);
        tmpFileSinkImages.setOutputType(FileSinkImages.OutputType.jpg);
        tmpFileSinkImages.setQuality(FileSinkImages.Quality.HIGH);
        tmpFileSinkImages.setViewCenter(10000, 10000);
        //tmpFileSinkImages.setResolution(2048, 2048);
        tmpFileSinkImages.setResolution(Resolutions.UHD_4K);
        tmpFileSinkImages.setAutofit(false);
        tmpFileSinkImages.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        BufferedImage bufferedImage = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_RGB);
        Graphics2D tmpTestImage = bufferedImage.createGraphics();
        File tmpImageFile = new File((new File("")).getAbsolutePath() + File.separatorChar + "GraphStreamDisplay" + File.separatorChar + "temp" + File.separatorChar + tmpGraph.getId() + ".png");
        ImageIO.write(bufferedImage, "png", tmpImageFile);
        tmpFileSinkImages.writeAll(tmpGraph, tmpImageFile.getAbsolutePath());
        tmpGraph.setAttribute("ui.screenshot", "screenshot.jpg"); // Saved at: C:\Users\zande\IdeaProjects\ScaffoldGenerator\ScaffoldGenerator
        */
    }
    //
    /**
     * The ScaffoldNodeCollectionBase (scaffold network or tree) is parsed into a GraphStream Graph object with nodes depicting the scaffolds.
     * The optional numbering of the nodes reflects their respective indices in the exported adjacency matrix and their level in the graph.
     *
     * @param aScaffoldNodeCollection scaffold graph, a scaffold tree or network
     * @param areNodesLabelled adds a label with node level and node index if true.
     * @param aGraphID identifier string to assign the created graph
     * @param aStyleSheet style sheet for the created graph
     * @param aCDKDepictionsGenerator used for creating the scaffold images on the nodes
     * @throws IOException if image files cannot be written for the depicted scaffold structures
     */
    public static Graph generateGraphFromScaffoldNodeCollection(
            ScaffoldNodeCollectionBase aScaffoldNodeCollection,
            boolean areNodesLabelled,
            String aGraphID,
            String aStyleSheet,
            DepictionGenerator aCDKDepictionsGenerator)
            throws IOException
    {
        //TODO: input checks
        Graph tmpGraph = new SingleGraph(aGraphID);
        //tmpGraph.setAttribute("ui.stylesheet", "node { shape: rounded-box; size-mode: fit; padding: 60px; } graph { shape: box; size-mode: fit; padding: 70px; }");
        tmpGraph.setAttribute("ui.stylesheet", aStyleSheet);
        tmpGraph.setAttribute("ui.quality");
        tmpGraph.setAttribute("ui.antialias");
        //DepictionGenerator tmpGenerator = new DepictionGenerator().withSize(2048,2048).withFillToFit();
        //TODO: move to constant
        File tmpTempFolderPath = new File((new File("")).getAbsolutePath() + File.separatorChar + "GraphStreamDisplay" + File.separatorChar + "temp" + File.separatorChar);
        if (!tmpTempFolderPath.exists()) {
            tmpTempFolderPath.mkdirs();
        }
        Integer[][] tmpMatrix = aScaffoldNodeCollection.getMatrix();
        int tmpEdgeCount = 0;
        //for each matrix row, representing the matrix nodes
        for (int tmpRowIndex = 0; tmpRowIndex < tmpMatrix.length; tmpRowIndex++) {
            ScaffoldNodeBase tmpCollectionLevelNode =  aScaffoldNodeCollection.getMatrixNode(tmpRowIndex);
            //each node is assigned its matrix index as id
            tmpGraph.addNode(String.valueOf(tmpRowIndex));
            Node tmpNode = tmpGraph.getNode(String.valueOf(tmpRowIndex));
            //the respective ScaffoldNodeBase instance of the tree or network node is stored on the respective graph node as attribute
            //TODO: move attribute name to constant and describe in doc
            tmpNode.setAttribute("ScaffoldNodeBase", tmpCollectionLevelNode);
            //Add a label to each node that corresponds to the level in the collection and its index in the matrix if true
            if (areNodesLabelled) {
                String tmpLabel = tmpCollectionLevelNode.getLevel() + ";" + tmpRowIndex;
                tmpNode.setAttribute("ui.label", tmpLabel);
            }
            /*Add the structure image*/
            IAtomContainer tmpCollectionNodeMolecule = (IAtomContainer) tmpCollectionLevelNode.getMolecule();
            try {
                BufferedImage tmpNodeImg = aCDKDepictionsGenerator.depict(tmpCollectionNodeMolecule).toImg();
                /*The images are stored temporarily*/
                File tmpTemporaryImageFile = new File(tmpTempFolderPath.getPath() + File.separatorChar + "GraphStream" + tmpRowIndex + ".png");
                ImageIO.write(tmpNodeImg, "png", tmpTemporaryImageFile);
                //set the images
                //alternative fill-mode: image-scaled-ratio-max
                tmpNode.setAttribute("ui.style", "fill-mode: image-scaled;" + "fill-image: url('" + tmpTemporaryImageFile.getAbsolutePath() + "');");
                tmpTemporaryImageFile.deleteOnExit();
            } catch (CDKException aCDKException) {
                Logger.getLogger(GraphStreamUtility.class.getName()).log(Level.WARNING, "Unable to depict structure at index " + tmpRowIndex + ". Displaying empty node.");
            }
            /*Add edges*/
            for (int tmpColumnIndex = 0; tmpColumnIndex < tmpMatrix[tmpRowIndex].length; tmpColumnIndex++) {
                //Skip a diagonal half to get edges in one direction only.
                if (tmpRowIndex < tmpColumnIndex) {
                    continue;
                }
                //Insert an edge if there is 1 in the respective matrix cell
                if (tmpMatrix[tmpRowIndex][tmpColumnIndex].equals(1)) {
                    tmpGraph.addEdge(String.valueOf(tmpEdgeCount), tmpRowIndex, tmpColumnIndex);
                    tmpEdgeCount++;
                }
            }
        }
        return tmpGraph;
    }
}

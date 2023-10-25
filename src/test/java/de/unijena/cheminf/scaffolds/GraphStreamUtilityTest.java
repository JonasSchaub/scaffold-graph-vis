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
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.FormatFactory;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV3000Reader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.scaffold.NetworkNode;
import org.openscience.cdk.tools.scaffold.ScaffoldGenerator;
import org.openscience.cdk.tools.scaffold.ScaffoldNetwork;
import org.openscience.cdk.tools.scaffold.ScaffoldNodeBase;
import org.openscience.cdk.tools.scaffold.ScaffoldTree;
import org.openscience.cdk.tools.scaffold.TreeNode;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Test class showcasing the capabilities of the GraphStream scaffold graph visualisation functionality.
 * Please note that most of this is code for example usage of the functionality, not actual test code.
 *
 *  @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 *  @version 1.0.0.0
 */
public class GraphStreamUtilityTest {
    /**
     * Imports Sertraline (PubChem CID 68617) from a SMILES string.
     * Generates the Schuffenhauer tree of this molecule and displays it with GraphStream.
     * This molecule is also used in Scheme 15 in the <a href="https://doi.org/10.1021/ci600338x">"The Scaffold Tree"</a> paper.
     *
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void graphStreamTreeTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        //shortcut:
        //GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree);
        //alternative with more options:
        Graph tmpGraph = new SingleGraph("Sertraline-Scaffold-Tree");
        GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldTree,
                GraphStreamUtility.DEFAULT_ARE_NODES_LABELLED,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                tmpGraph);
        System.setProperty("org.graphstream.ui", GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
        tmpGraph.display();
        //screenshot the graph in both ways available
        GraphStreamUtility.screenshotGraph(tmpGraph, GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Sertraline_ScaffoldTree_low.png");
        GraphStreamUtility.screenshotGraphHighQuality(tmpGraph, GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Sertraline_ScaffoldTree.png");
        //to make the window stay open for ten seconds, it would close again immediately otherwise
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Imports Sertraline (PubChem CID 68617) from a SMILES string.
     * Generates the scaffold network of this molecule and displays it with GraphStream.
     * This molecule is also used in Scheme 15 in the <a href="https://doi.org/10.1021/ci600338x">"The Scaffold Tree"</a> paper.
     *
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void graphStreamNetworkTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule);
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldNetwork),
                GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Sertraline_ScaffoldNetwork.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Imports four molecules from SMILES strings, generates a Schuffenhauer tree of these molecules,
     * and displays the resulting tree with GraphStream.
     *
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void graphStreamTreeMergeTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CCC2C(C1)C3=CN=CN=C3S2"); //PubChem CID 141755869
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C1CC2=C(C1)SC3=C2C(=NC=N3)Cl"); //PubChem CID 789817
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("CC1=C(SC=[N+]1CC2=CN=C(N=C2N)C)CCO.[Cl-]"); //PubChem CID 6042, Thiamine
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("COCCOC1=C(C=C2C(=C1)C(=NC=N2)NC3=CC=CC(=C3)C#C)OCCOC"); //PubChem CID 176870, Erlotinib
        List<IAtomContainer> tmpMoleculeList = new ArrayList<>();
        tmpMoleculeList.add(tmpMolecule);
        tmpMoleculeList.add(tmpMolecule1);
        tmpMoleculeList.add(tmpMolecule2);
        tmpMoleculeList.add(tmpMolecule3);
        List<ScaffoldTree> tmpScaffoldTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpMoleculeList);
        //shortcut would be:
        //GraphStreamUtility.displayWithGraphStream(tmpScaffoldTreeList.get(0));
        //alternative with more options:
        Graph tmpGraph = new SingleGraph("Pyrimidine-Scaffold-Tree");
        GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldTreeList.get(0),
                GraphStreamUtility.DEFAULT_ARE_NODES_LABELLED,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                tmpGraph);
        System.setProperty("org.graphstream.ui", GraphStreamUtility.DEFAULT_GRAPHSTREAM_UI);
        tmpGraph.display();
        //screenshot the graph in both ways available
        GraphStreamUtility.screenshotGraph(tmpGraph, GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar +
              "Pyrimidine_ScaffoldTree_low.png");
        GraphStreamUtility.screenshotGraphHighQuality(tmpGraph, GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar +
                "Pyrimidine_ScaffoldTree.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Imports two molecules from SMILES strings, generates a scaffold network of these molecules,
     * and displays the result with GraphStream.
     *
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void graphStreamNetworkMergeTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("ClC2NC1SCNN1N2"); //fantasy molecule for testing
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2"); //fantasy molecule for testing
        List<IAtomContainer> tmpMoleculeList = new ArrayList<>();
        tmpMoleculeList.add(tmpMolecule);
        tmpMoleculeList.add(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMoleculeList);
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Creates a scaffold tree from a V2000 or V3000 mol file of Flucloxacillin and displays it as a scaffold tree with GraphStream.
     * Some additional information is printed for every scaffold node.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void graphStreamTreeTestFlucloxacillin() throws Exception {
        String tmpFileName = "Test3" ;
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile(tmpFileName);
        //Generate a tree
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        //Print some further information
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for (ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldTree.getAllNodes()) {
            TreeNode tmpTestNode = (TreeNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            System.out.println("First Origin: " + tmpTestNode.getOriginSmilesList().get(0));
            if (!tmpTestNode.getNonVirtualOriginCount().equals(0)){
                System.out.println("First NonVirtualOrigin: " +tmpTestNode.getNonVirtualOriginSmilesList().get(0));
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                TreeNode tmpChildNode = (TreeNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Contains molecule: " + tmpScaffoldTree.containsMolecule(tmpChildMolecule));
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        System.out.println("Max Lvl: " + tmpScaffoldTree.getMaxLevel());
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldTree),
                GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Flucloxacillin_ScaffoldTree.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Creates a ScaffoldNetwork from a V2000 or V3000 mol file of Flucloxacillin and displays it as a scaffold network with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void graphStreamNetworkTestFlucloxacillin() throws Exception {
        String tmpFileName = "Test3" ;
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile(tmpFileName);
        //Generate scaffold network
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule);
        //Print some further information
        System.out.println("Roots size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            System.out.println("First Origin: " + tmpTestNode.getOriginSmilesList().get(0));
            if(!tmpTestNode.getNonVirtualOriginCount().equals(0)){
                System.out.println("First NonVirtualOrigin: " +tmpTestNode.getNonVirtualOriginSmilesList().get(0));
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        System.out.println("Max Lvl: " + tmpScaffoldNetwork.getMaxLevel());
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldNetwork),
                GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Flucloxacillin_ScaffoldNetwork.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Recreates Figure 1 from the <a href="https://doi.org/10.1021/ci2000924">"Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper by Varin et al</a>.
     * Creates the scaffold network of Ondasetron. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void scaffoldNetworkArticleFigure1ANetworkTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpOndasetron = tmpParser.parseSmiles("CC1=NC=CN1CC2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Ondasetron
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpOndasetronScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpOndasetron);
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpOndasetronScaffoldNetwork, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpOndasetronScaffoldNetwork),
                GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Ondasetron_ScaffoldNetwork.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Recreates Figure 1 from the <a href="https://doi.org/10.1021/ci2000924">"Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper by Varin et al</a>.
     * Creates the scaffold networks of Alosetron. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void scaffoldNetworkArticleFigure1BNetworkTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpAlosetron = tmpParser.parseSmiles("CC1=C(N=CN1)CN2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Alosetron
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpAlosetronScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpAlosetron);
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpAlosetronScaffoldNetwork, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpAlosetronScaffoldNetwork),
              GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Alosetron_ScaffoldNetwork.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Recreates Figure 1 from the <a href="https://doi.org/10.1021/ci2000924">"Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper by Varin et al</a>.
     * Creates the scaffold networks of Ramosetron. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void scaffoldNetworkArticleFigure1CNetworkTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpRamosetron = tmpParser.parseSmiles("CN1C=C(C2=CC=CC=C21)C(=O)C3CCC4=C(C3)NC=N4");//Ramosetron
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpRamosetronScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpRamosetron);
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpRamosetronScaffoldNetwork, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpRamosetronScaffoldNetwork),
              GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Ramosetron_ScaffoldNetwork.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Recreates Figure 1 from the <a href="https://doi.org/10.1021/ci2000924">"Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper by Varin et al</a>.
     * Creates the scaffold network of Ondasetron, Alosetron, Ramosetron. The result is visualised with GraphStream and written to a screenshot file.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void scaffoldNetworkArticleFigure1NetworkTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpOndasetron = tmpParser.parseSmiles("CC1=NC=CN1CC2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Ondasetron
        IAtomContainer tmpAlosetron = tmpParser.parseSmiles("CC1=C(N=CN1)CN2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Alosetron
        IAtomContainer tmpRamosetron = tmpParser.parseSmiles("CN1C=C(C2=CC=CC=C21)C(=O)C3CCC4=C(C3)NC=N4");//Ramosetron
        List<IAtomContainer> tmpInputList = new ArrayList<>(3);
        tmpInputList.add(tmpOndasetron);
        tmpInputList.add(tmpAlosetron);
        tmpInputList.add(tmpRamosetron);
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpInputList);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldNetwork),
                GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Figure1_ScaffoldNetwork.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Creates multiple scaffold trees of some "fantasy molecules" and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void mergeTreeDisplayTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c3ccc(C2NC1SCNN1N2)cc3");
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        ScaffoldTree tmpScaffoldTree2 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule2);
        ScaffoldTree tmpScaffoldTree3 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule3);
        ScaffoldTree tmpScaffoldTree4 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule4);
        ScaffoldTree tmpScaffoldTree5 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule5);
        ScaffoldTree tmpScaffoldTree6 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule6);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree5);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree6);
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldTree),
                GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Fantasy_ScaffoldTree.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Creates multiple scaffold networks and merges them. The result is visualised with GraphStream.
     * A network is added here that has no connection to the rest of the network. It is displayable in principle, but not optimal.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void mergeNetworkDisplayTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        //Molecule without connection to the network
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("C3CC1CC1CC4CCC2CC2CC34");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        /*Generate Networks*/
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork2 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule2);
        ScaffoldNetwork tmpScaffoldNetwork3 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule3);
        ScaffoldNetwork tmpScaffoldNetwork4 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule4);
        /*Merge Networks*/
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork2);
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork3);
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork4);
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Creates multiple scaffold trees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Disabled
    @Test
    public void mergeMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c1ncc2c(n1)SC3CCCCC23"); //CID: 141755869
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c1ncc2c(n1)SC3CCCC23");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc1ncncc1c2"); //CID: 9210 QUINAZOLINE
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc2nc(CC1NCNCN1)ncc2c3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c1ccc3c(c1)oc2cncnc23"); //CID: 12711630 benzofuropyrimidine
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c1cnc3c(c1)oc2cncnc23"); //CID: 12711630 benzofuropyrimidine
        IAtomContainer tmpMolecule7 = tmpParser.parseSmiles("c3ccc(N2NCc1cncnc12)cc3");
        IAtomContainer tmpMolecule8 = tmpParser.parseSmiles("c2cnc(N1CCCCC1)nc2"); //CID: 11446518 2-(Piperidin-1-yl)pyrimidine
        IAtomContainer tmpMolecule9 = tmpParser.parseSmiles("c2ncc1NCNc1n2"); //CID: 18322245  8-dihydropurine
        IAtomContainer tmpMolecule10 = tmpParser.parseSmiles("c1ccc2c(c1)[nH]c3ncncc23"); //CID: 12509330
        List<IAtomContainer> tmpTreeList = new ArrayList<>();
        tmpTreeList.add(tmpMolecule1);
        tmpTreeList.add(tmpMolecule2);
        tmpTreeList.add(tmpMolecule3);
        tmpTreeList.add(tmpMolecule4);
        tmpTreeList.add(tmpMolecule5);
        tmpTreeList.add(tmpMolecule6);
        tmpTreeList.add(tmpMolecule7);
        tmpTreeList.add(tmpMolecule8);
        tmpTreeList.add(tmpMolecule9);
        tmpTreeList.add(tmpMolecule10);
        List<ScaffoldTree> tmpFinalForest = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTreeList);
        System.out.println("Forest size: " + tmpFinalForest.size());
        ScaffoldTree tmpScaffoldTree = tmpFinalForest.get(0);
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        //GraphStream throws an error here for an unknown reason
        //GraphStreamUtility.screenshotGraphHighQuality(GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldTree),
        //        GraphStreamUtility.getGraphStreamDisplayFolder().getAbsolutePath() + File.separatorChar + "Pyrimidines_ScaffoldTree_2.png");
        TimeUnit.SECONDS.sleep(10);
    }
    //
    /**
     * Imports Sertraline (PubChem CID 68617) from a SMILES string.
     * Generates the Schuffenhauer tree of this molecule.
     * This molecule is also used in Scheme 15 in the <a href="https://doi.org/10.1021/ci600338x">"The Scaffold Tree"</a> paper.
     * This test checks the edge and node count of the created GraphStream graph and that the creation goes without problems.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    public void treeTest() throws Exception {
        SmilesParser tmpParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        Graph tmpGraph = new SingleGraph("Sertraline-Scaffold-Tree");
        GraphStreamUtility.generateGraphFromScaffoldNodeCollection(tmpScaffoldTree,
                GraphStreamUtility.DEFAULT_ARE_NODES_LABELLED,
                GraphStreamUtility.DEFAULT_CDK_DEPICTION_GENERATOR,
                GraphStreamUtility.DEFAULT_GRAPH_STYLE_SHEET,
                tmpGraph);
        Assertions.assertEquals(2, tmpGraph.getEdgeCount());
        Assertions.assertEquals(3, tmpGraph.getNodeCount());
    }
    //
    /**
     * Loads a mol file of a specific path and returns it as IAtomContainer object.
     * Supports V2000 and V3000 mol files.
     * @param aFileName Path of the molecule to be loaded, without the extension
     * @return IAtomContainer of the charged molecule
     * @throws Exception if anything goes wrong
     */
    protected IAtomContainer loadMolFile(String aFileName) throws Exception {
        /*Get molecule path*/
        BufferedInputStream tmpInputStream = new BufferedInputStream(this.getClass().getResourceAsStream(aFileName + ".mol"));
        /*Get mol file version*/
        FormatFactory tmpFactory = new FormatFactory();
        IChemFormat tmpFormat = tmpFactory.guessFormat(tmpInputStream);
        IAtomContainer tmpMolecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        /*Load V2000 mol file*/
        if(tmpFormat.getReaderClassName().contains("V2000")) {
            MDLV2000Reader tmpReader = new MDLV2000Reader(tmpInputStream);
            IChemObjectBuilder tmpBuilder = SilentChemObjectBuilder.getInstance();
            tmpMolecule = tmpReader.read(tmpBuilder.newAtomContainer());
            /*Load V3000 mol file*/
        } else if(tmpFormat.getReaderClassName().contains("V3000")) {
            MDLV3000Reader tmpReader = new MDLV3000Reader(tmpInputStream);
            IChemObjectBuilder tmpBuilder = SilentChemObjectBuilder.getInstance();
            tmpMolecule = tmpReader.read(tmpBuilder.newAtomContainer());
        }
        tmpInputStream.close();
        return tmpMolecule;
    }
}

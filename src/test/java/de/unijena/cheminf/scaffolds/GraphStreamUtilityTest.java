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

import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
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

public class GraphStreamUtilityTest {
    /**
     * Loads Sertraline (CID 68617) out of a SMILES.
     * Generates the Schuffenhauer tree of this molecule and displays it with GraphStream.
     * This molecule is also used in Scheme 15 in the <a href="https://doi.org/10.1021/ci600338x">"The scaffold Tree"</a> paper.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void graphStreamTreeTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        //SMILES to IAtomContainer
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        /*Display the Tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Loads Sertraline (CID 68617) out of a SMILES.
     * Generates the enumerative network of this molecule and displays it with GraphStream.
     * This molecule is also used in Scheme 15 in the <a href="https://doi.org/10.1021/ci600338x">"The scaffold Tree"</a> paper.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void graphStreamNetworkTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        //SMILES to IAtomContainer
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl");
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule);
        /*Display the Tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Loads four molecules out of a SMILES.
     * Generates Schuffenhauer trees of these molecules, merges them to one tree and displays the tree with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void graphStreamTreeMergeTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        //SMILES to IAtomContainer
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("C1CCC2C(C1)C3=CN=CN=C3S2");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C1CC2=C(C1)SC3=C2C(=NC=N3)Cl");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("CC1=C(SC=[N+]1CC2=CN=C(N=C2N)C)CCO.[Cl-]"); //6042
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("COCCOC1=C(C=C2C(=C1)C(=NC=N2)NC3=CC=CC(=C3)C#C)OCCOC"); //176870
        List<IAtomContainer> tmpMoleculeList = new ArrayList<>();
        tmpMoleculeList.add(tmpMolecule);
        tmpMoleculeList.add(tmpMolecule1);
        tmpMoleculeList.add(tmpMolecule2);
        tmpMoleculeList.add(tmpMolecule3);
        List<ScaffoldTree> tmpScaffoldTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpMoleculeList);
        /*Display the Tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTreeList.get(0), true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Loads two molecules out of a SMILES.
     * Generates networks of these molecules, merges them to one network and displays the network with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void graphStreamNetworkMergeTest() throws Exception {
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        //SMILES to IAtomContainer
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("ClC2NC1SCNN1N2");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        List<IAtomContainer> tmpMoleculeList = new ArrayList<>();
        tmpMoleculeList.add(tmpMolecule);
        tmpMoleculeList.add(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldTree = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMoleculeList);
        /*Display the Tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates a ScaffoldTree from a V2000 or V3000 mol file and displays it as a network with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void graphStreamTreeTest3() throws Exception {
        String tmpFileName = "Test3" ;
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile(tmpFileName);
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);
        /*Remove some nodes. Nodes can be removed from the non-root end.
        If nodes are removed in the middle of the tree, it cannot be displayed with Graphstream.*/
        System.out.println(tmpScaffoldTree.getAllNodes().size());
        //TreeNode tmpRemoveNode = (TreeNode) tmpScaffoldTree.getMatrixNode(3);
        //tmpScaffoldTree.removeNode(tmpRemoveNode);
        System.out.println(tmpScaffoldTree.getAllNodes().size());
        /*Print some further information*/
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldTree.getAllNodes()) {
            TreeNode tmpTestNode = (TreeNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            System.out.println("Origin" +tmpTestNode.getOriginSmilesList().get(0) + "Size" + tmpTestNode.getOriginSmilesList().size());
            if(!tmpTestNode.getNonVirtualOriginCount().equals(0)){
                System.out.println("NonVirtualOrigin" +tmpTestNode.getNonVirtualOriginSmilesList().get(0) + "Size" + tmpTestNode.getNonVirtualOriginSmilesList().size());
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                TreeNode tmpChildNode = (TreeNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Contains molecule: " + tmpScaffoldTree.containsMolecule(tmpChildMolecule));
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        System.out.println("Max Lvl: " + tmpScaffoldTree.getMaxLevel());
        /*Display the Tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates a ScaffoldNetwork from a V2000 or V3000 mol file and displays it as a network with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void graphStreamNetworkTest3() throws Exception {
        String tmpFileName = "Test3" ;
        //Load molecule from molfile
        IAtomContainer tmpMolecule = this.loadMolFile(tmpFileName);
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule);
        /*Remove some nodes. Nodes can be removed from the non-root end.
        If nodes are removed in the middle of the tree, it cannot be displayed with Graphstream.*/
        System.out.println(tmpScaffoldNetwork.getAllNodes().size());
        //tmpScaffoldNetwork.removeNode(tmpScaffoldNetwork.getMatrixNode(0));
        NetworkNode tmpRemoveNode = (NetworkNode) tmpScaffoldNetwork.getMatrixNode(9);
        tmpScaffoldNetwork.removeNode(tmpRemoveNode);
        System.out.println(tmpScaffoldNetwork.getAllNodes().size());
        /*Print some further information*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            System.out.println("Origin" +tmpTestNode.getOriginSmilesList().get(0) + "Size" + tmpTestNode.getOriginSmilesList().size());
            if(!tmpTestNode.getNonVirtualOriginCount().equals(0)){
                System.out.println("NonVirtualOrigin" +tmpTestNode.getNonVirtualOriginSmilesList().get(0) + "Size" + tmpTestNode.getNonVirtualOriginSmilesList().size());
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
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Loads Figure 1 from the "Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper by Varin et al.
     * Creates the Scaffold Networks of Ondasetron, Alosetron or Ramosetron. The result is visualised with GraphStream.
     * By selecting the corresponding lines, the molecule to be displayed can be chosen.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void getFigure1NetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("CC1=NC=CN1CC2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Ondasetron
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("CC1=C(N=CN1)CN2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Alosetron
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("CN1C=C(C2=CC=CC=C21)C(=O)C3CCC4=C(C3)NC=N4");//Ramosetron
        //Generate a Network of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        /*Uncomment the molecule to display it*/
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule1);//Ondasetron
        //ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule2);//Alosetron
        //ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule3);//Ramosetron
        /*Print some further information*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeTreeDisplayTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c3ccc(C2NC1SCNN1N2)cc3");
        //Generate a tree of molecules with iteratively removed terminal rings
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldTree tmpScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        ScaffoldTree tmpScaffoldTree2 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule2);
        ScaffoldTree tmpScaffoldTree3 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule3);
        ScaffoldTree tmpScaffoldTree4 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule4);
        ScaffoldTree tmpScaffoldTree5 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule5);
        ScaffoldTree tmpScaffoldTree6 = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule6);
        //System.out.println(tmpScaffoldTree.mergeTree(tmpScaffoldTree2));
        tmpScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree5);
        tmpScaffoldTree.mergeTree(tmpScaffoldTree6);
        ScaffoldTree tmpOverlapScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        //tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree2);
        //tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree3);
        //tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree4);
        //tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree6);
        //tmpScaffoldTree.mergeTree(tmpOverlapScaffoldTree);
        //ScaffoldTree tmpUnfitScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);//Molecule does not fit
        //System.out.println("Tree does not fit: " + tmpScaffoldTree.mergeTree(tmpUnfitScaffoldTree));
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates different ScaffoldNetworks and merges them. The result is visualised with GraphStream.
     * A network is added here that has no connection to the rest of the network.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeNetworkDisplayTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        /*Generate IAtomContainer from SMILES*/
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        //Molecule without connection to the network
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("C3CC1CC1CC4CCC2CC2CC34");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        //Generate a Network of molecules with iteratively removed terminal rings
        /*Generate Networks*/
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork2 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule2);
        ScaffoldNetwork tmpScaffoldNetwork3 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule3);
        ScaffoldNetwork tmpScaffoldNetwork4 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule4);
        /*Merge Networks*/
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork2);
        //tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork3);
        //tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork4);
        /*Add edges and nodes*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates different ScaffoldNetworks and merges them. The result is visualised with GraphStream.
     * A network is added here that has no connection to the rest of the network.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void generateEnumerativeForestTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        /*Generate IAtomContainer from SMILES*/
        //IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("BrC3CC2CC1CC1CCC2CC4CC34");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c4ccc(C3NC2SC(c1ccccc1)NN2N3)cc4");
        //Molecule without connection to the network
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("C3CC1CC1CC4CCC2CC2CC34");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc(C2NNC(c1ccccc1)N2)cc3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("C1CCCPCCC1");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("BrC2CCc1ccccc12");
        //Generate a Network of molecules with iteratively removed terminal rings
        /*Generate Networks*/
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        List<IAtomContainer> tmpMoleculeList = new ArrayList<>();
        tmpMoleculeList.add(tmpMolecule1);
        tmpMoleculeList.add(tmpMolecule2);
        tmpMoleculeList.add(tmpMolecule3);
        tmpMoleculeList.add(tmpMolecule4);
        tmpMoleculeList.add(tmpMolecule5);
        tmpMoleculeList.add(tmpMolecule6);
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMoleculeList);
        /*Add edges and nodes*/
        System.out.println("Node number: " + tmpScaffoldNetwork.getAllNodes().size());
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpNonVirtualOrigin : tmpTestNode.getNonVirtualOriginSmilesList()) {
                System.out.println("NonVirtualOrigin: " + tmpNonVirtualOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates the different ScaffoldNetworks from Figure 1 from the "Mining for Bioactive Scaffolds with Scaffold Networks"(2011) Paper
     * by Varin et al. and merges them.
     * The networks of Ondasetron and Alosetron are merged.
     * The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeFigure1NetworkTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("CC1=NC=CN1CC2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Ondasetron
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("CC1=C(N=CN1)CN2CCC3=C(C2=O)C4=CC=CC=C4N3C");//Alosetron
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("CN1C=C(C2=CC=CC=C21)C(=O)C3CCC4=C(C3)NC=N4");//Ramosetron
        /*Generate a network of molecules with iteratively removed terminal rings*/
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        ScaffoldNetwork tmpScaffoldNetwork = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule1);
        ScaffoldNetwork tmpScaffoldNetwork2 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule2);
        //ScaffoldNetwork tmpScaffoldNetwork3 = tmpScaffoldGenerator.generateScaffoldNetwork(tmpMolecule3);
        /*Merge the networks*/
        tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork2);
        //Uncomment if ramosetron should also be added to the network
        //tmpScaffoldNetwork.mergeNetwork(tmpScaffoldNetwork3);//Ramosetron
        /*Print some further information*/
        System.out.println("Root size: " + tmpScaffoldNetwork.getRoots().size());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldNodeBase tmpTestNodeBase : tmpScaffoldNetwork.getAllNodes()) {
            NetworkNode tmpTestNode = (NetworkNode) tmpTestNodeBase;
            IAtomContainer tmpTestMolecule = (IAtomContainer) tmpTestNode.getMolecule();
            System.out.println("--- Node: " + tmpSmilesGenerator.create(tmpTestMolecule) + " ---");
            System.out.println("Node on LvL: " + tmpTestNode.getLevel());
            System.out.println("Children Number: " + tmpTestNode.getChildren().size());
            for(Object tmpOrigin : tmpTestNode.getOriginSmilesList()) {
                System.out.println("Origin: " + tmpOrigin);
            }
            for(Object tmpOrigin : tmpTestNode.getNonVirtualOriginSmilesList()) {
                System.out.println("NonVirtualOrigin: " + tmpOrigin);
            }
            for(Object tmpChildObject : tmpTestNode.getChildren()) {
                NetworkNode tmpChildNode = (NetworkNode) tmpChildObject;
                IAtomContainer tmpChildMolecule = (IAtomContainer) tmpChildNode.getMolecule();
                System.out.println("Child: " + tmpSmilesGenerator.create(tmpChildMolecule));
            }
        }
        /*Display the network*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldNetwork, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeTreeOriginTest() throws Exception {
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("C2NC1SCNN1N2");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("ClC2NC1SCNN1N2");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc(C1NCNN1)cc2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("C1=CC=C(C=C1)C3NNC(C2=CC=CC=C2[Br])N3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c2ccc1CCCc1c2");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("C1=CC=C(C=C1)C3NC2SC(NN2N3)[Br]");
        //Generate a tree of molecules with iteratively removed terminal rings
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
        ScaffoldTree tmpOverlapScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule1);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree2);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree3);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree4);
        tmpOverlapScaffoldTree.mergeTree(tmpScaffoldTree6);
        //tmpScaffoldTree.mergeTree(tmpOverlapScaffoldTree);
        ScaffoldTree tmpUnfitScaffoldTree = tmpScaffoldGenerator.generateSchuffenhauerTree(tmpMolecule);//Molecule does not fit
        System.out.println("Tree does not fit: " + tmpScaffoldTree.mergeTree(tmpUnfitScaffoldTree));
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        System.out.println("Number of Origins: " + tmpScaffoldTree.getRoot().getOriginCount());
        for(ScaffoldNodeBase tmpTreeNodeBase : tmpScaffoldTree.getAllNodes()) {
            TreeNode tmpTreeNode = (TreeNode) tmpTreeNodeBase;
            System.out.println("---Node: " + tmpSmilesGenerator.create((IAtomContainer) tmpTreeNode.getMolecule()));
            for(Object tmpSmiles : tmpTreeNode.getOriginSmilesList()) {
                System.out.println("Origin of the Node: " + tmpSmiles);
            }
            for(Object tmpSmiles : tmpTreeNode.getNonVirtualOriginSmilesList()) {
                System.out.println("nonVirtual: " + tmpSmiles);
            }
        }
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeMoleculesToForestProblemTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("O=C(OC)CCC=1C=2N=C(C=C3N=C(C=C4N=C(C=C5N=C(C2)C(=C5C)CC)C=C4C)C=C3C)C1C");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("N=1C2=CC3=NC(=CC4=NC(=C5C6=NC(=CC1C(=C2C)C)C(=C6CCC5)C)C(=C4C)CC)C(=C3C)CC");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("ON=C1C=CC=2C3=NC(=CC4=NC(=CC5=NC(=CC6=NC(C=C6C)=C13)C(=C5C)CC)C(=C4C)CC)C2C");

        //Generate a tree of molecules with iteratively removed terminal rings
        List<IAtomContainer> tmpTreeList = new ArrayList<>();
        tmpTreeList.add(tmpMolecule);
        tmpTreeList.add(tmpMolecule1);
        tmpTreeList.add(tmpMolecule2);
        List<ScaffoldTree> tmpFinalForest = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTreeList);
        System.out.println("Forest size: " + tmpFinalForest.size());
        ScaffoldTree tmpScaffoldTree = tmpFinalForest.get(0);
        System.out.println("Node size" + tmpFinalForest.get(0).getAllNodes().size());
        for(ScaffoldNodeBase tmpTreeNode : tmpScaffoldTree.getAllNodes()) {
            for(Object tmpSmiles : tmpTreeNode.getNonVirtualOriginSmilesList()) {
                System.out.println("nonVirtual: " + tmpSmiles);
            }
        }
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        //SMILES to IAtomContainer
        SmilesParser tmpParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpParser.parseSmiles("CC1=C(C(=NO1)C2=C(C=CC=C2Cl)F)C(=O)NC3C4N(C3=O)C(C(S4)(C)C)C(=O)O");
        IAtomContainer tmpMolecule1 = tmpParser.parseSmiles("c1ncc2c(n1)SC3CCCCC23");
        IAtomContainer tmpMolecule2 = tmpParser.parseSmiles("c1ncc2c(n1)SC3CCCC23");
        IAtomContainer tmpMolecule3 = tmpParser.parseSmiles("c2ccc1ncncc1c2");
        IAtomContainer tmpMolecule4 = tmpParser.parseSmiles("c3ccc2nc(CC1NCNCN1)ncc2c3");
        IAtomContainer tmpMolecule5 = tmpParser.parseSmiles("c1ccc3c(c1)oc2cncnc23");
        IAtomContainer tmpMolecule6 = tmpParser.parseSmiles("c1cnc3c(c1)oc2cncnc23");
        IAtomContainer tmpMolecule7 = tmpParser.parseSmiles("c3ccc(N2NCc1cncnc12)cc3");
        IAtomContainer tmpMolecule8 = tmpParser.parseSmiles("c2cnc(N1CCCCC1)nc2");
        IAtomContainer tmpMolecule9 = tmpParser.parseSmiles("c2ncc1NCNc1n2");
        IAtomContainer tmpMolecule10 = tmpParser.parseSmiles("c1ccc2c(c1)[nH]c3ncncc23");
        //Generate a tree of molecules with iteratively removed terminal rings
        List<IAtomContainer> tmpTreeList = new ArrayList<>();
        tmpTreeList.add(tmpMolecule);
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
        ScaffoldTree tmpScaffoldTree = tmpFinalForest.get(1);
        for(ScaffoldNodeBase tmpTreeNode : tmpScaffoldTree.getAllNodes()) {
            for(Object tmpSmiles : tmpTreeNode.getNonVirtualOriginSmilesList()) {
                System.out.println("nonVirtual: " + tmpSmiles);
            }
        }
        IAtomContainer tmpRootMolecule = (IAtomContainer) tmpScaffoldTree.getRoot().getMolecule();
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        System.out.println("I am Root: " + tmpSmilesGenerator.create(tmpRootMolecule));
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Test of generateSchuffenhauerForest() with V2000 and V3000 mol files.
     * Loads the 7 Test(Test1.mol-Test7.mol) molfiles from the Resources folder.
     * Creates different ScaffoldTrees and merges them. The result is visualised with GraphStream.
     * @throws Exception if anything goes wrong
     */
    @Test
    public void mergeAllTestMoleculesToForestTest() throws Exception {
        ScaffoldGenerator tmpScaffoldGenerator = new ScaffoldGenerator();
        List<IAtomContainer> tmpTestMoleculeList = new ArrayList<>();
        for (int tmpCount = 1; tmpCount < 8; tmpCount++) {
            String tmpFileName = "Test" + tmpCount;
            //Load molecule from molfile
            IAtomContainer tmpTestMolecule = this.loadMolFile(tmpFileName);
            tmpTestMoleculeList.add(tmpTestMolecule);
        }
        List<ScaffoldTree> tmpTestTreeList = tmpScaffoldGenerator.generateSchuffenhauerForest(tmpTestMoleculeList);
        System.out.println("Number of molecules: " + tmpTestMoleculeList.size());
        System.out.println("Number of trees: " + tmpTestTreeList.size());
        ScaffoldTree tmpScaffoldTree = tmpTestTreeList.get(2);
        System.out.println("Origin SMILES: " + tmpScaffoldTree.getRoot().getOriginSmilesList());
        SmilesGenerator tmpSmilesGenerator = new SmilesGenerator(SmiFlavor.Unique);
        for(ScaffoldTree tmpTestTree : tmpTestTreeList) {
            System.out.println("Number of Nodes:" + tmpTestTree.getAllNodes().size());
            System.out.println("Root:" + tmpSmilesGenerator.create((IAtomContainer) tmpTestTree.getRoot().getMolecule()));
        }
        /*Display the tree*/
        GraphStreamUtility.displayWithGraphStream(tmpScaffoldTree, true);
        TimeUnit.SECONDS.sleep(5);
    }
    //
    /**
     * Loads a mol file of a specific path and ret-----urns it as IAtomContainer.
     * Supports V2000 and V3000 mol files.
     * @param aFilePath Path of the molecule to be loaded
     * @return IAtomContainer of the charged molecule
     * @throws Exception if anything goes wrong
     */
    protected IAtomContainer loadMolFile(String aFilePath) throws Exception {
        /*Get molecule path*/
        File tmpResourcesDirectory = new File(aFilePath);
        BufferedInputStream tmpInputStream = new BufferedInputStream(this.getClass().getResourceAsStream(aFilePath + ".mol"));
        /*Get mol file version*/
        FormatFactory tmpFactory = new FormatFactory();
        IChemFormat tmpFormat = tmpFactory.guessFormat(tmpInputStream);
        IAtomContainer tmpMolecule = new AtomContainer();
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

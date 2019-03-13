package tree.test;

import java.util.HashMap;
import java.util.Map.Entry;
import convolution.lexicon.Lexicon;
import convolution.lexicon.LexiconEntry;
import permutation.Permutation;
import tree.labelled.Node;
import tree.labelled.Tree;
import vector.Vector;
import vector.VectorType;
import vector.real.DenseRealVector;

//using permutations to encode children
//works better than using circular convolution to binder vectors to encode children
//because commutative: binder1 (*) binder2 (*) word = binder2 (*) binder1 (*) word
//but non-commutative: p1(p2(word)) != p2(p1(word))
class LabelledTreeTest<V extends Vector>{
    
    private final VectorType vectorType;
    private final int dimensionality;
    private final Lexicon<String, V> nodeLexicon;
    private final HashMap<String, Permutation> edgePermutationMap;
    
    public LabelledTreeTest(VectorType vectorType, int dimensionality){
        this.vectorType = vectorType;
        this.dimensionality = dimensionality;
        nodeLexicon = new Lexicon<>(dimensionality);
        edgePermutationMap = new HashMap<>();
    }
    
    private V getNodeVector(String nodeString){
        V v;
        LexiconEntry<String, V> nodeEntry = nodeLexicon.getEntry(nodeString);
        
        if(nodeEntry == null){
            v = (V) Vector.createDenseRandomVector(vectorType, dimensionality);
            nodeLexicon.addEntry(nodeString, v);
        }else{
            v = nodeEntry.vector();
        }
        
        return v;
    }
    
    private Permutation getPermutation(String edgeString){
        Permutation p = edgePermutationMap.get(edgeString);
        
        if(p == null){
            p = Permutation.createRandomPermutation(dimensionality, edgeString);
            edgePermutationMap.put(edgeString, p);
        }
        
        return p;
    }

    public V getNodeVector(Node node){
        String nodeString = node.getNodeString();
        V nodeVector = getNodeVector(nodeString);
        
        for(int i=0; i<node.amountOfChildren(); i++){
            Entry<String, Node> edge = node.getEdge(i);
            String edgeString = edge.getKey();
            Node childNode = edge.getValue();
            
            Permutation p = getPermutation(edgeString);
            V childNodeVector = getNodeVector(childNode);
            nodeVector = (V) nodeVector.plus(childNodeVector.permute(p));
        }
        
        return nodeVector;
    }
    
    public V getTreeVector(Tree depTree){
        return getNodeVector(depTree.rootNode());
    }
  
    //this version has higher similarity scores during lexicon search
    //because it is better at successively refining the vector
    //i.e. subtracting all found (permuted) word vectors before passing it on
    //to the next node, even if that node is the uncle of the previous node
    private V extractTree(V vector, Node node, Permutation p){
        System.out.print("searching with p_" + p.name() + " => ");
        LexiconEntry<String, V> entry = nodeLexicon.getNearestEntry((V) vector.reversePermute(p));
        if(entry != null){
            node.setNodeString(entry.key());
            vector = (V) vector.minus(entry.vector());
            
            for(String edgeString : edgePermutationMap.keySet()){
                Node childNode = new Node();
                if(extractTree(vector, childNode, p.composeWith(getPermutation(edgeString))) != null){
                    node.addChild(edgeString, childNode);
                }
            }
        }else{
            return null;
        }
        
        return vector;
    }
    
    public Tree extractTree(V treeVector){
        Node rootNode = new Node();
        
        V v = extractTree(treeVector, rootNode, Permutation.createIdentityPermutation(dimensionality));
        if(v == null){
            return null;
        }else{
            return new Tree(rootNode);
        }
    }
    
    
    public static void main(String[] args){
        int dimensionality = 1024;
        System.out.println("dimensionality: " + dimensionality);
        
        LabelledTreeTest<DenseRealVector> test = new LabelledTreeTest<>(VectorType.REAL, dimensionality);
        
        //String treeString = "like(NSUBJ:I|XCOMP:travel(AUX:to))";
        String treeString = "likes(NSUBJ:man(DET:the|AMOD:old)|XCOMP:travel(AUX:to))";
        Tree tree = Tree.createFrom(treeString);
        DenseRealVector treeVector = test.getTreeVector(tree);
        System.out.println("tree as string: " + treeString);
        System.out.println("tree parse:\n" + tree.toIndentedString());
        System.out.println("tree as vector: " + treeVector);
        
        System.out.println("\n" + test.nodeLexicon);
        
        System.out.println("Permutations:");
        for(String relation : test.edgePermutationMap.keySet()){
            System.out.println("p_" + relation + ": " + test.getPermutation(relation));
        }
        System.out.println();
        
        Tree recoveredTree = test.extractTree(treeVector);
        System.out.println("recovered tree as string: " + recoveredTree);
    }
    
}
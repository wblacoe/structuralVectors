package tree.test;

import java.util.HashMap;
import convolution.lexicon.Lexicon;
import convolution.lexicon.LexiconEntry;
import permutation.Permutation;
import tree.unlabelled.Node;
import tree.unlabelled.Tree;
import vector.Vector;
import vector.VectorType;
import vector.real.DenseRealVector;

//using permutations to encode children
//works better than using circular convolution to binder vectors to encode children
//because commutative: binder1 (*) binder2 (*) word = binder2 (*) binder1 (*) word
//but non-commutative: p1(p2(word)) != p2(p1(word))
class UnlabelledTreeTest<V extends Vector> {

    private final VectorType vectorType;    
    private final int dimensionality;
    private final Lexicon<String, V> nodeLexicon;
    private final HashMap<Integer, Permutation> permutations;
    
    public UnlabelledTreeTest(VectorType vectorType, int dimensionality){
        this.vectorType = vectorType;
        this.dimensionality = dimensionality;
        nodeLexicon = new Lexicon<>(dimensionality);
        permutations = new HashMap<>();
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
    
    private Permutation getPermutation(int i){
        Permutation p = permutations.get(i);
        if(p == null){
            p = Permutation.createRandomPermutation(dimensionality, "p" + i);
            permutations.put(i, p);
        }
        
        return p;
    }
    
    public V getNodeVector(Node node){
        V nodeVector = getNodeVector(node.getNodeString());
        
        for(int i=0; i<node.amountOfChildren(); i++){
            Node childNode = node.getChild(i);
            
            Permutation p = getPermutation(i);
            nodeVector = (V) nodeVector.plus(getNodeVector(childNode).permute(p));
        }
        
        return nodeVector;
    }
    
    public V getTreeVector(Tree tree){
        return getNodeVector(tree.rootNode);
    }
    
    /*public Node extractTree1(RealVector nodeVector, String debug){
        Node node = null;
        
        System.out.println("searching with perms " + debug);
        LexiconEntry wordEntry = nodeLexicon.getNearestEntry(nodeVector);
        if(wordEntry != null){
            node = new Node();
            node.name = wordEntry.name;
            
            nodeVector = nodeVector.minus(wordEntry.vector);
            
            int i=0;
            while(true){
                Permutation p = getPermutation(i);
                RealVector v = p.reversePermute(nodeVector);
                
                Node childNode = extractTree1(v, debug + ", perm" + i);
                if(childNode != null){
                    node.addChild(childNode);
                    RealVector childVector = getWordVector(childNode.name);
                    nodeVector = nodeVector.minus(p.reversePermute(childVector));
                    i++;
                }else{
                    break;
                }
            }
        }
        
        return node;
    }
    
    public Tree extractTree1(RealVector treeVector){
        Tree tree = new Tree();
        Node rootNode = extractTree1(treeVector, "");
        tree.rootNode = rootNode;
        
        return tree;
    }*/
  
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
            
            int i=0;
            while(true){
                Node childNode = new Node();
                if(extractTree(vector, childNode, p.composeWith(getPermutation(i))) == null){
                    break;
                }else{
                    node.addChild(childNode);
                }
                i++;
            }
            
        }else{
            vector = null;
        }
        
        return vector;
    }
    
    public Tree extractTree(V treeVector){
        Node rootNode = new Node();
        
        V v = (V) extractTree(treeVector, rootNode, Permutation.createIdentityPermutation(dimensionality));
        if(v == null){
            return null;
        }else{
            return new Tree(rootNode);
        }
    }
    

    public static void main(String[] args){
        int dimensionality = 1024;
        System.out.println("dimensionality: " + dimensionality);
        
        UnlabelledTreeTest<DenseRealVector> test = new UnlabelledTreeTest<>(VectorType.REAL, dimensionality);
        
        //String treeString = "like(I|travel(to))";
        String treeString = "likes(man(the|old)|travel(to))";
        Tree tree = Tree.createFrom(treeString);
        DenseRealVector treeVector = test.getTreeVector(tree);
        System.out.println("tree as string: " + treeString);
        System.out.println("tree parse:\n" + tree.toIndentedString());
        System.out.println("tree as vector: " + treeVector);
        
        System.out.println(test.nodeLexicon);
        
        Tree recoveredTree = test.extractTree(treeVector);
        System.out.println(recoveredTree);
    }
    
}
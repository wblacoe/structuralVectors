package tree.labelled;;

public class Tree {

    private Node rootNode;
    
    public Tree(){
    }
    
    public Tree(Node rootNode){
        this.rootNode = rootNode;
    }

    public Node rootNode(){
        return rootNode;
    }
    
    public void setRootNode(Node rootNode){
        this.rootNode = rootNode;
    }

    public static Tree createFrom(String string){
        Tree tree = new Tree();
        Node node = Node.createFrom(string);
        tree.rootNode = node;
        
        return tree;
    }
    
    @Override
    public String toString(){
        return rootNode.toString();
    }
    
    public String toIndentedString(){
        return rootNode.toIndentedString("");
    }

    public static void main(String[] args){
        Tree tree = Tree.createFrom("like(NSUBJ:man(DET:the|AMOD:old)|XCOMP:travel(AUX:to))");
        System.out.println(tree.toString());
    }
    
}

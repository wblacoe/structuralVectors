package tree.unlabelled;

public class Tree {

    public Node rootNode;
    
    public Tree(){
        
    }
    
    public Tree(Node rootNode){
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
        String treeString = "like(man(the|old)|travel(to))";
        Tree depTree = Tree.createFrom(treeString);
        System.out.println(depTree.toString());
    }
    
}
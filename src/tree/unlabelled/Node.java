package tree.unlabelled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Node {

    private String nodeString;
    private Node parentNode;
    private final ArrayList<Node> children;
    
    public Node(){
        nodeString = null;
        parentNode = null;
        children = new ArrayList<>();
    }
    
    public String getNodeString(){
        return nodeString;
    }
    
    public void setNodeString(String nodeString){
        this.nodeString = nodeString;
    }
    
    public int amountOfChildren(){
        return children.size();
    }
    
    public Node getChild(int index){
        if(index < amountOfChildren()){
            return children.get(index);
        }else{
            return null;
        }
    }
    
    public boolean isLeaf(){
        return children.isEmpty();
    }
    
    public int getDepth(){
        if(parentNode == null){
            return 0;
        }else{
            return 1 + parentNode.getDepth();
        }
    }
    
    public void addChild(Node childNode){
        childNode.parentNode = this;
        children.add(childNode);
    }
    
    public static ArrayList<Node> createFrom(InputStream stream) throws IOException{
        ArrayList<Node> nodes = new ArrayList<>();
        
        String s = "";
        char c;
        while(stream.available() > 0){
            c = (char) stream.read();
            switch (c) {
                case '(':
                    Node node = new Node();
                    node.nodeString = s;
                    s = "";
                    nodes.add(node);
                    ArrayList<Node> children = createFrom(stream);
                    for(Node child : children){
                        node.addChild(child);
                    }
                    break;
                case ')':
                    if(!s.isEmpty()){
                        node = new Node();
                        node.nodeString = s;
                        nodes.add(node);
                    }
                    return nodes;
                case '|':
                    if(!s.isEmpty()){
                        node = new Node();
                        node.nodeString = s;
                        s = "";
                        nodes.add(node);
                    }
                    break;
                default:
                    s += c;
                    break;
            }
        }
        
        return nodes;
    }
    
    public static Node createFrom(String string){
        Node node = null;
        
        try{
            node = createFrom(new ByteArrayInputStream(string.getBytes())).get(0);
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return node;
    }

    @Override
    public String toString(){
        String s = nodeString;
        if(amountOfChildren() > 0) s += "(";
        
        for(int i=0; i<amountOfChildren(); i++){
            Node childNode = getChild(i);
            s += childNode.toString();
            if(i < amountOfChildren() - 1) s += "|";
        }
        
        if(amountOfChildren() > 0) s += ")";
        
        return s;
    }
    
    public String toIndentedString(String indent){
        String s = indent + nodeString;
        
        for(Node childNode : children){
            s += "\n" + childNode.toIndentedString(indent + "  ");
        }
        
        return s;
    }
    
}
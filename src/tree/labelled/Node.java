package tree.labelled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

public class Node {

    private String nodeString, edgeStringWithParent;
    private Node parentNode;
    private final ArrayList<Entry<String, Node>> edgeStringChildNodeMap;
    
    public Node(){
        nodeString = null;
        edgeStringWithParent = "ROOT"; //default is to have no parent node
        parentNode = null;
        edgeStringChildNodeMap = new ArrayList<>();
    }
    
    public int amountOfChildren(){
        return edgeStringChildNodeMap.size();
    }
    
    public Node getChild(int childIndex){
        if(childIndex < amountOfChildren()){
            return edgeStringChildNodeMap.get(childIndex).getValue();
        }else{
            return null;
        }
    }
    
    public Entry<String, Node> getEdge(int childIndex){
        if(childIndex < amountOfChildren()){
            return edgeStringChildNodeMap.get(childIndex);
        }else{
            return null;
        }
    }
    
    public boolean isLeaf(){
        return edgeStringChildNodeMap.isEmpty();
    }
    
    public int getDepth(){
        if(parentNode == null){
            return 0;
        }else{
            return 1 + parentNode.getDepth();
        }
    }
    
    public String getNodeString(){
        return nodeString;
    }
    
    public void setNodeString(String nodeString){
        this.nodeString = nodeString;
    }
    
    public String getEdgeStringWithParent(){
        return edgeStringWithParent;
    }
    
    public void setEdgeStringWithParent(String edgeStringWithParent){
        this.edgeStringWithParent = edgeStringWithParent;
    }
    
    public void addChild(String edgeStringWithThisNode, Node childNode){
        childNode.setEdgeStringWithParent(edgeStringWithThisNode);
        childNode.parentNode = this;
        edgeStringChildNodeMap.add(new SimpleEntry(edgeStringWithThisNode, childNode));
    }
    
    public static Node createFrom(InputStream stream) throws IOException{
        Node depNode = new Node();
        
        String s = "";
        char c;
        while(stream.available() > 0){
            c = (char) stream.read();
            switch (c) {
                case '(':
                    depNode.setNodeString(s);
                    s = "";
                    break;
                case ':':
                    String edgeString = s;
                    s = "";
                    Node childNode = createFrom(stream);
                    depNode.addChild(edgeString, childNode);
                    break;
                case ')':
                case '|':
                    if(depNode.getNodeString() == null){
                        depNode.setNodeString(s);
                    }
                    return depNode;
                default:
                    s += c;
                    break;
            }
        }
        
        return depNode;
    }
    
    public static Node createFrom(String string){
        Node node = null;
        
        try{
            node = createFrom(new ByteArrayInputStream(string.getBytes()));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return node;
    }

    @Override
    public String toString(){
        String s = nodeString;
        int amountOfChildren = amountOfChildren();
        if(amountOfChildren > 0) s += "(";
        
        int i = 0;
        for(Entry<String, Node> pair : edgeStringChildNodeMap){
            String edgeString = pair.getKey();
            Node childNode = pair.getValue();
            s += edgeString + ":" + childNode.toString();
            i++;
            if(i < amountOfChildren) s += "|";
        }
        
        if(amountOfChildren > 0) s += ")";
        
        return s;
    }
    
    public String toIndentedString(String indent){
        String s = nodeString;
        
        for(Entry<String, Node> pair : edgeStringChildNodeMap){
            String edgeString = pair.getKey();
            Node childNode = pair.getValue();
            s += "\n" + indent + edgeString + ": " + childNode.toIndentedString(indent + "  ");
        }
        
        return s;
    }
    
}

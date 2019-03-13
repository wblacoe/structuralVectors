package misc;

import java.util.stream.IntStream;

public class PrintableTable {
    
    private final String[][] entries;
    private final int amountOfRows, lengthOfRows;
    private int rowNumber, positionInRow;
    
    public PrintableTable(String[][] entries){
        this.entries = entries;
        amountOfRows = entries.length;
        lengthOfRows = (amountOfRows > 0 ? entries[0].length : 0);
        rowNumber = entries.length;
        positionInRow = 0;
    }
    
    public PrintableTable(int amountOfRows, int lengthOfRows){
        entries = new String[amountOfRows][lengthOfRows];
        this.amountOfRows = amountOfRows;
        this.lengthOfRows = lengthOfRows;
        rowNumber = 0;
        positionInRow = 0;
    }
    
    public void set(String entry, int rowNumber, int positionInRow){
        entries[rowNumber][positionInRow] = entry;
    }
    
    public Object get(int rowNumber, int positionInRow){
        if(0 <= rowNumber && rowNumber < entries.length && 0 <= positionInRow && positionInRow < entries[rowNumber].length){
            return entries[rowNumber][positionInRow];
        }else{
            return null;
        }
    }
    
    public boolean add(String entry){
        if(rowNumber < entries.length && positionInRow < entries[rowNumber].length){
            entries[rowNumber][positionInRow] = entry;
            positionInRow++;
            if(positionInRow >= entries[rowNumber].length){
                rowNumber++;
                positionInRow = 0;
            }
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        String s = IntStream.range(0, lengthOfRows).mapToObj(i -> "\t" + i).reduce(String::concat).get();
        for(int i=0; i<entries.length; i++){
            s += "\n" + i;
            String[] row = entries[i];
            for(int j=0; j<row.length; j++){
                String entry = row[j];
                s += "\t" + (entry == null ? "" : entry);
            }
        }
        
        return s;
    }

}

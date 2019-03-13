package convolution.test;

import convolution.lexicon.LexiconEntry;
import convolution.lexicon.Lexicon;
import java.util.LinkedList;
import java.util.List;
import vector.Vector;
import vector.VectorType;
import vector.real.DenseRealVector;


//sentence = position_1 (*) word_1 + ... + position_n (*) word_n
//where all position_i and word_i are random gaussian vectors
public class SequenceTest<V extends Vector> {
    
    private final VectorType vectorType;    
    private final Lexicon<String, V> wordLexicon, positionLexicon;
    private final int dimensionality;
    
    public SequenceTest(VectorType vectorType, int dimensionality){
        this.vectorType = vectorType;
        wordLexicon = new Lexicon(dimensionality);
        positionLexicon = new Lexicon(dimensionality);
        this.dimensionality = dimensionality;
    }
    
    private void addEntries(String[] words){
        for(int i=0; i<words.length; i++){
            String word = words[i];
            V wordVector = (V) Vector.createDenseRandomVector(vectorType, dimensionality);
            wordLexicon.addEntry(word, wordVector);
            
            String positionVectorName = "position" + (i+1);
            V positionVector = (V) Vector.createDenseRandomVector(vectorType, dimensionality);
            positionLexicon.addEntry(positionVectorName, positionVector);
        }
        
        //for good measure
        String positionVectorName = "position" + (words.length + 1);
        V positionVector = (V) Vector.createDenseRandomVector(vectorType, dimensionality);
        positionLexicon.addEntry(positionVectorName, positionVector);
    }
    
    private V getSentenceVector(String sentence){
        String[] words = sentence.split(" ");
        addEntries(words);
        
        V sentenceVector = (V) Vector.createZeroVector(vectorType, dimensionality);
        for(int i=0; i<words.length; i++){
            
            String word = words[i];
            LexiconEntry<String, V> wordEntry = wordLexicon.getEntry(word);
            V wordVector = wordEntry.vector();
            
            String positionEntryName = "position" + (i+1);
            LexiconEntry<String, V> positionEntry = positionLexicon.getEntry(positionEntryName);
            V positionVector = positionEntry.vector();
            
            V convVector = (V) wordVector.convolute(positionVector);
            //positionLexicon.addEntry(word + " (*) " + positionEntryName, convVector);
            
            sentenceVector = (V) sentenceVector.plus(convVector);
        }
        
        return sentenceVector;
    }    
    
    public List<String> extractSentence(V sentenceVector){
        List<String> sentence = new LinkedList<>();
        
        int i = 0;
        while(true){
            System.out.print("searching for word at position " + (i + 1) + " => ");
            V positionVector = (V) positionLexicon.getEntry("position" + (i + 1)).vector();
            V potentialWordVector = (V) sentenceVector.correlate(positionVector);
            LexiconEntry<String, V> wordEntry = wordLexicon.getNearestEntry(potentialWordVector);
            if(wordEntry != null){
                sentence.add(wordEntry.key());
                V wordVector = wordEntry.vector();
                sentenceVector = (V) sentenceVector.minus(wordVector.convolute(positionVector));
            }else{
                break;
            }
            i++;
        }
        
        return sentence;
    }
    
    public static void main(String[] args){
        int dimensionality = 1024;
        System.out.println("dimensionality: " + dimensionality);
        
        //String sentence = "I like to travel";
        String sentence = "The old man likes to travel";
        SequenceTest<DenseRealVector> test = new SequenceTest(VectorType.REAL, dimensionality);
        DenseRealVector sentenceVector = test.getSentenceVector(sentence);
        
        System.out.println("input sentence: " + sentence);
        System.out.println("sentence as vector: " + sentenceVector);
        
        System.out.println("\nword vectors:\n" + test.wordLexicon);
        System.out.println("position vectors:\n" + test.positionLexicon);
        
        List<String> recoveredSentence = test.extractSentence(sentenceVector);
        System.out.println("\nrecovered sentence: " + recoveredSentence.stream().reduce("", (a, b) -> a + " " + b));
    }
    
}
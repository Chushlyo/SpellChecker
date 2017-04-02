import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CorpusReader 
{
    final static String CNTFILE_LOC = "samplecnt.txt";
    final static String VOCFILE_LOC = "samplevoc.txt";
    
    private int maximumC;
    private HashMap<String,Integer> ngrams;
    private Set<String> vocabulary;
    private HashMap <Integer, Integer> Frequency;
    
    final static double K = 0.005; // K smoothing constant
        
    public CorpusReader() throws IOException
    {  
        readNGrams();
        readVocabulary();
    }
    
    /**
     * Returns the n-gram count of <NGram> in the corpus
     * 
     * @param nGram : space-separated list of words, e.g. "adopted by him"
     * @return count of <NGram> in corpus
     */
     public int getNGramCount(String nGram) throws  NumberFormatException
    {
        if(nGram == null || nGram.length() == 0)
        {
            throw new IllegalArgumentException("NGram must be non-empty.");
        }
        return ngrams.getOrDefault(nGram, 0);
    }
    
    private void readNGrams() throws 
            FileNotFoundException, IOException, NumberFormatException
    {
        ngrams = new HashMap<>();

        FileInputStream fis;
        fis = new FileInputStream(CNTFILE_LOC);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));

        while (in.ready()) {
            String phrase = in.readLine().trim();
            String s1, s2;
            int j = phrase.indexOf(" ");

            s1 = phrase.substring(0, j);
            s2 = phrase.substring(j + 1, phrase.length());

            int count = 0;
            try {
                count = Integer.parseInt(s1);
                ngrams.put(s2, count);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException("NumberformatError: " + s1);
            }
        }
    }
    
    
    private void readVocabulary() throws FileNotFoundException, IOException {
        vocabulary = new HashSet<>();
        
        FileInputStream fis = new FileInputStream(VOCFILE_LOC);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        
        while(in.ready())
        {
            String line = in.readLine();
            vocabulary.add(line);
        }
    }
    
    /**
     * Returns the size of the number of unique words in the corpus
     * 
     * @return the size of the number of unique words in the corpus
     */
    public int getVocabularySize() 
    {
        return vocabulary.size();
    }
    
    /**
     * Returns the subset of words in set that are in the vocabulary
     * 
     * @param set
     * @return 
     */
    public HashSet<String> inVocabulary(Set<String> set) 
    {
        HashSet<String> h = new HashSet<>(set);
        h.retainAll(vocabulary);
        return h;
    }
    
    public boolean inVocabulary(String word) 
    {
       return vocabulary.contains(word);
    }    
    
    public double getSmoothedCount(String NGram)
    {
        if(NGram == null || NGram.length() == 0)
        {
            throw new IllegalArgumentException("NGram must be non-empty.");
        }
        
        double smoothedCount = 0.0;
        
        /** ADD CODE HERE **/  
        
        
        return smoothedCount;        
    }
    
    public double ProbabilityWithDifferentWord(String w, String secondw, boolean followingWord){
        if (w == null || secondw == null || w.length() == 0 || secondw.length() == 0){
            throw new IllegalArgumentException ("The NGrams is empty");
        }
        double cBigrams;
        if (followingWord){
            cBigrams = getNGramCount(w + " " +secondw);
        }
        else{
            cBigrams = getNGramCount(secondw + " " +w);
        }
        int cpw = getNGramCount (secondw);
        double vs = getVocabularySize();
        //K smoothing added according to definition
        double probabillity =  (cBigrams + K) / (cpw + K * vs);
        
        return probabillity;  
    }
    
    public double getProbabillity (String nGram){
        double count = getNGramCount(nGram);
        double vs = getVocabularySize();
        double probabillity  = count/vs;
        return probabillity;
    }
    // Add one smoothing added, not used
    public double ProbabilityWithDifferentWordAddOneSmoothing(String w, String secondw, boolean followingWord){
        if (w == null || secondw == null || w.length() == 0 || secondw.length() == 0){
            throw new IllegalArgumentException ("The NGrams is empty");
        }
        double cBigrams;
        if (followingWord){
            cBigrams = getNGramCount(w + " " +secondw);
        }
        else{
            cBigrams = getNGramCount(secondw + " " +w);
        }
        int cpw = getNGramCount (secondw);
        double vs = getVocabularySize();
        //K smoothing added according to definition
        double probabillity =  (cBigrams + 1) / (cpw + vs);
        
        return probabillity;  
    }
    
    /**
     * Calculate frequencies of counts beforehand. Used for Good-Turing
     * smoothing.
     */
    private void determineNGramCountFrequencies() {
        this.Frequency = new HashMap<>();

        List<Integer> counts = new ArrayList<>(this.ngrams.values());
        int i;
        // For all counts get frequency 
        for (i = 1; counts.contains(i); i++) {
            this.Frequency.put(i, Collections.frequency(counts, i));
        }
        this.maximumC = i;
    }    
    /**
     * Good-Turing smoothing. -- NOT USED!
     */
    private int getGoodTuringSmoothedCount(String ngram) {
        int N = this.ngrams.size();

        // If bigram not contained in vocabulary, use frequency for a bigram with count 1
        if (!this.ngrams.containsKey(ngram) || this.ngrams.get(ngram) == 0) {
            return this.Frequency.get(1) / N;
        } else { // For other bigrams with count > 0 use frequency of count + 1
            int c = this.ngrams.get(ngram);
            if (c < this.maximumC) { // For some high bigram counts there is no frequency for count + 1
                return (c + 1) * (this.Frequency.get(c + 1)
                        / (this.Frequency.get(c) * N));
            } else { // use normal probability
                return c / N;
            }
        }
}

    
}

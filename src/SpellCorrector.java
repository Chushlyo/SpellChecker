import java.util.Map;

public class SpellCorrector {
    final private CorpusReader cr;
    final private ConfusionMatrixReader cmr;
    
    public SpellCorrector(CorpusReader cr, ConfusionMatrixReader cmr) {
        this.cr = cr;
        this.cmr = cmr;
    }
    
    public String correctPhrase(String phrase) {
        if(phrase == null || phrase.length() == 0)
        {
            throw new IllegalArgumentException("phrase must be non-empty.");
        }
            
        String[] words = phrase.split(" ");
        String finalSuggestion = "";
        
        /** CODE TO BE ADDED **/
        
        return finalSuggestion.trim();
    }    
    
    /**
     * Returns the Damerau-Levenshtein distance between strings a and b. Makes
     * use of the dynamic programming algorithm.
     */
    private int getDLDistance(String a, String b) {
        // Stop if a or b is empty
        if (b.length() == 0) {
            return a.length();
        }
        if (a.length() == 0) {
            return b.length();
        }

        // Initialize matrix
        int[][] m = new int[b.length() + 1][a.length() + 1];

        // Fill in initial values
        for (int i = 0; i <= b.length(); i++) {
            m[i][0] = i;
        }
        for (int j = 0; j <= a.length(); j++) {
            m[0][j] = j;
        }

        // Fill in the rest of matrix
        for (int i = 1; i <= b.length(); i++) {
            for (int j = 1; j <= a.length(); j++) {
                if (b.charAt(i - 1) == a.charAt(j - 1)) { // if characters are equal
                    m[i][j] = m[i - 1][j - 1]; // no cost
                } else if (i > 1 && j > 1 && b.charAt(i - 1) == a.charAt(j - 2) && b.charAt(i - 2) == a.charAt(j - 1)) {// check if transposition is allowed
                        
                    m[i][j] = Math.min(m[i][j - 1] + 1,         // insertion
                              Math.min(m[i - 1][j] + 1,         // deletion
                              Math.min(m[i - 1][j - 1] + 1,     // substitution
                                       m[i - 2][j - 2] + 1)));  // transposition 
                } else {
                    m[i][j] = Math.min(m[i][j - 1] + 1,         // insertion
                              Math.min(m[i - 1][j] + 1,         // deletion
                                       m[i - 1][j - 1] + 1));   // substitution
                }
            }
        }

        return m[b.length()][a.length()]; // return DL distance for a and b
    }
    
    
    /** returns a map with candidate words and their noisy channel probability. **/
    public Map<String,Double> getCandidateWords(String typo)
    {
        return new WordGenerator(cr,cmr).getCandidateCorrections(typo);
    }            
}
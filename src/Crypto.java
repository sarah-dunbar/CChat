/** Implements Caesar cypher */
public class Crypto {
    private int rotation;
    
    /** Make a Crypto using a particular key
     * @param the key
     */
    public Crypto(int r) {
        rotation = r;
    }
    
    /** Encrypt data using the key
     * @param plain the plaintext to encrypt
     * @returns the cyphertext
     */
    public String encrypt(String plain) {
        char[] c=plain.toCharArray();
        for(int i=0; i<c.length; i++) {
            if('a'<=c[i] && 'z'>=c[i]) {
                c[i] = (char)(((c[i]-'a')+rotation)%26 + 'a');
            } else if('A'<=c[i] && 'Z'>=c[i]) {
                c[i] = (char)(((c[i]-'A')+rotation)%26 + 'A');
            } else if((48 <= c[i] && 57 >= c[i])) {
            	c[i] = (char) (((c[i]- '0')+rotation)%10 + '0');
            }
        }
        return new String(c);
    }
    
    /** Decrypt data using the key
     * @param cypher the cyphertext to decrypt
     * @returns the plaintext
     */
    public String decrypt(String cypher) {
        char[] c=cypher.toCharArray();
        for(int i=0; i<c.length; i++) {
            if('a'<=c[i] && 'z'>=c[i]) {
                c[i] = (char)(((c[i]-'a')-rotation+26)%26 + 'a');
            } else if('A'<=c[i] && 'Z'>=c[i]) {
                c[i] = (char)(((c[i]-'A')-rotation+26)%26 + 'A');
            } else if((48 <= c[i] && 57 >= c[i])) {
            	c[i] = (char) (((c[i]- '0')-rotation+30)%10 + '0');
            }
        }
        return new String(c);
    }
}
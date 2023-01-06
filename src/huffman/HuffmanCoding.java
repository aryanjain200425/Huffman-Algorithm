package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;



/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);

    /* Your code goes here */
    sortedCharFreqList = new ArrayList<CharFreq>();
    int[] occ = new int[128];
    double numChar = 1;
    

    boolean allSame = true;
    Character previous = StdIn.readChar();
    occ[previous] = 1;


    while (StdIn.hasNextChar()){
        numChar++;
       Character next =  StdIn.readChar();
       if(previous != next){
        allSame = false;
       }
       occ[next]++;
       
       previous = next;
    }

    

    
    for (int i = 0; i < occ.length; i ++){
        if (occ[i] >  0){
            CharFreq charNode = new CharFreq((char)i, occ[i]/numChar);
            sortedCharFreqList.add(charNode);
        }
    }

    if(allSame){
        Character x = sortedCharFreqList.get(0).getCharacter();
        int index = x + 1;
        if (x == 127){
            x = 0;
        }
        CharFreq newChar = new CharFreq((char)index,0);
        sortedCharFreqList.add(newChar);
    }

    

    Collections.sort(sortedCharFreqList);

    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {

    /* Your code goes here */

    Queue<CharFreq> source = new Queue<CharFreq>();
    Queue<TreeNode> target = new Queue<TreeNode>();

        for (CharFreq x : sortedCharFreqList){
            source.enqueue(x);
        }


        while( target.size()!= 1 || !source.isEmpty()){

            if(target.isEmpty()){
                CharFreq leftChar = source.dequeue();
                CharFreq rightChar = source.dequeue();

                double newOcc = leftChar.getProbOcc() + rightChar.getProbOcc();

                CharFreq newCharNode = new CharFreq(null, newOcc);

                TreeNode rightTreeNode = new TreeNode(rightChar, null, null);
                TreeNode leftTreeNode = new TreeNode(leftChar, null, null);

                TreeNode newTreeNode = new TreeNode(newCharNode, leftTreeNode,rightTreeNode);

                target.enqueue(newTreeNode);


            }

            else if (source.isEmpty()){

                TreeNode leftTree = target.dequeue();
                TreeNode rightTree = target.dequeue();

                double newOcc = leftTree.getData().getProbOcc() + rightTree.getData().getProbOcc();

                CharFreq newCharNode = new CharFreq(null, newOcc);

                TreeNode newTreeNode = new TreeNode(newCharNode,leftTree,rightTree);

                target.enqueue(newTreeNode);

            }

            else{

                TreeNode leftTree = null;
                
                if (source.peek().getProbOcc() <= target.peek().getData().getProbOcc()){
                    leftTree = new TreeNode(source.dequeue(),null,null);
                }
                else{
                    leftTree = target.dequeue();
                }

                TreeNode rightTree = null;

                if (source.isEmpty()){
                    rightTree = target.dequeue();
                }
                else if (target.isEmpty()){
                    rightTree = new TreeNode(source.dequeue(),null,null);
                }
                else{
                    if (source.peek().getProbOcc() <= target.peek().getData().getProbOcc()){
                        rightTree = new TreeNode(source.dequeue(),null,null);
                    }
                    else{
                        rightTree = target.dequeue();
                    }
                }

                double newOcc = leftTree.getData().getProbOcc() + rightTree.getData().getProbOcc();

                CharFreq newCharNode = new CharFreq(null, newOcc);

                TreeNode newTreeNode = new TreeNode(newCharNode, leftTree, rightTree);

                target.enqueue(newTreeNode);

            }

        }

        huffmanRoot = target.dequeue();
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {



    /* Your code goes here */

        encodings = new String[128];
        for (int i = 0; i < encodings.length; i++) {
            encodings[i] = " ";
        }

        makeEncodings(huffmanRoot,false, true, "");


        for (int i = 0; i < encodings.length; i++) {
            if(encodings[i] == " "){
                encodings[i] = null;
            }
            
        }
   


    }


    private void makeEncodings(TreeNode n, boolean isRight, boolean isRoot, String binary) {

        // Print out either a right connection or a left connection
        Character character = n.getData().getCharacter();
        if (!isRoot ) {
            if(isRight){
                binary = binary + "1";
            }
            else{
                binary = binary + "0";
            }

        }

        if(character != null){
            encodings[character] = binary;
        }
        

        // If no more children we're done
        if (n.getLeft() == null && n.getRight() == null){
            binary = "";
            return;
        }



        makeEncodings(n.getRight(), true, false,binary);
        makeEncodings(n.getLeft(),  false, false, binary);
    }







    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);

    /* Your code goes here */   
        String encodedString = "";

        while(StdIn.hasNextChar()){
            Character  character = StdIn.readChar();
            encodedString += encodings[character];
        }

        writeBitString(encodedFile, encodedString);


    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);

    /* Your code goes here */


        String bString = readBitString(encodedFile);
        int counter = 0;
        TreeNode treeNode = huffmanRoot;
        String decodedString = "";

        while(counter != bString.length()){
            while(treeNode.getLeft() != null && treeNode.getRight() != null){
                String x = bString.charAt(counter)+"";
                if(x.equals("1")){
                    treeNode = treeNode.getRight();
                }
                else{
                    treeNode = treeNode.getLeft();
                }
                counter++;
            }
            decodedString += treeNode.getData().getCharacter()+ "";
            treeNode = huffmanRoot;
        }

        StdOut.print(decodedString);

    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
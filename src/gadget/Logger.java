package gadget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private String path = "log.txt";
    private File filename = new File(path);
    private FileWriter fw;
    
    public Logger() {
        // TODO Auto-generated constructor stub
        if (!filename.exists()) {
            try {
                filename.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(filename + " created!"); 
        }
        else{
            filename.delete();
            try {
                filename.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(filename + " existed!"); 
        }
                   
    }
    
    /**
     * Ð´ÎÄ¼þ.
     * 
     */
    public void print(String newStr){
        try {
            fw = new FileWriter(filename, true);
            fw.flush();
            fw.append(newStr + "\n");
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }
    
    public void print(int i){
        try {
            fw = new FileWriter(filename, true);
            fw.flush();
            fw.append(Integer.toString(i) + "\n");
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
    }

}

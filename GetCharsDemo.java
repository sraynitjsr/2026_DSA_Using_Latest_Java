import java.nio.CharBuffer;

public class MyGetCharsDemo {

    public static void executeDemo() {
        CharSequence str = "JDK25_Feature";
        CharSequence sb = new StringBuilder("StringBuilder_Test");
        CharSequence cb = CharBuffer.wrap("CharBuffer_Test");
        CharSequence custom = new SimpleSequence("Custom_Sequence");

        printBulkData(str);
        printBulkData(sb);
        printBulkData(cb);
        printBulkData(custom);
    }

    public static void printBulkData(CharSequence cs) {
        char[] buffer = new char[5];
        
        cs.getChars(0, 5, buffer, 0);

        System.out.println(cs.getClass().getSimpleName() + " (first 5): " + new String(buffer));
    }

    static class SimpleSequence implements CharSequence {
        private final String internal;
        
        SimpleSequence(String internal) { 
            this.internal = internal; 
        }
        
        @Override public int length() { 
            return internal.length(); 
        }
        
        @Override public char charAt(int index) { 
            return internal.charAt(index); 
        }
        
        @Override public CharSequence subSequence(int start, int end) { 
            return internal.subSequence(start, end); 
        }
        
        @Override public String toString() { 
            return internal; 
        }
    }
}

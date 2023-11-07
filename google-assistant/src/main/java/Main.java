import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    
    public static void main( String args[] ) throws Exception {
        //printResults(exec(new String[] {"cmd", "/c", "dir"},new File(".")));
        
        Process process = exec(new String[] {
                "google-oauthlib-tool", "--client-secrets", "credentials.json",
                "--credentials", "devicecredentials.json",
                "--scope", "https://www.googleapis.com/auth/assistant-sdk-prototype",
                "--scope", "https://www.googleapis.com/auth/gcm",
                "--save", "--headless" }, new File("/home/ec2-user/google-assistant-grpc"));
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            Pattern p = Pattern.compile( "(https.*)" );
            Matcher m = p.matcher( line );
            if ( m.find() ) {
                System.out.println( m.group( 1 ));
            }
        }
        //printResults();
    }
    
    private static void writeLine( Process p, String value ) throws IOException {
        BufferedWriter bufferedwriter = new BufferedWriter( new OutputStreamWriter( p.getOutputStream() ) );
        bufferedwriter.write( value );
        bufferedwriter.flush();
    }

    private static Process exec(String[] cmdarray, File dir) throws IOException {
        return new ProcessBuilder(cmdarray)
            .directory(dir)
            .start();
    }

    /**
     * Reads next line from process if available. Non-blocking.
     * @param process
     * @return next output line or null if nothing to read
     * @throws IOException
     */
    private static String parseOutput( Process process ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        String line = "";
        if ( reader.ready() ) {
            return reader.readLine();
        }
        return null;
    }

    private static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}

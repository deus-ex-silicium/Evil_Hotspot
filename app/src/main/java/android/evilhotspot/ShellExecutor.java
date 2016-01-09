package android.evilhotspot;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Nibiru on 2016-01-03.
 */
public class ShellExecutor {
    final static String TAG = "ShellExecutor";
    public ShellExecutor(){}

    public String Execute (String command){
        StringBuilder output = new StringBuilder();
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader((p.getInputStream())));
            String line;
            while ( (line = reader.readLine()) != null){
                output.append(line).append('\n');
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return output.toString();
    }

    public boolean RunAsRoot(String command){
        try {
            Process p = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command + '\n');
            os.writeBytes("exit\n");
            p.waitFor();
            os.flush();
            if (p.exitValue() == 0)
                return true;
            else return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String RunAsRootWithException(String command) throws RuntimeException {
        try{
            String output = "";
            String line;
            Process p = Runtime.getRuntime().exec("su");
            OutputStream stdin = p.getOutputStream();
            InputStream stderr = p.getErrorStream();
            InputStream stdout = p.getInputStream();

            stdin.write((command + '\n').getBytes());
            stdin.write(("exit\n").getBytes());
            stdin.flush();
            stdin.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            while ( (line = reader.readLine())!= null ){
                output += line;
            }
            reader.close();

            reader = new BufferedReader(new InputStreamReader(stderr));
            while ( (line = reader.readLine()) != null){
                Log.e(TAG, line);
                throw new RuntimeException();
            }
            reader.close();
            p.waitFor();
            p.destroy();

            return output;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String RunAsRootOutput(String command) {
        String output = "";
        String line;
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();

            stdin.write((command + '\n').getBytes());
            stdin.write(("exit\n").getBytes());
            stdin.flush();
            stdin.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                output = output + line;
            }
            br.close();
            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                Log.e("Shell Error:", line);
            }
            br.close();
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            Log.d(TAG, "An IOException was caught: " + e.getMessage());
        } catch (InterruptedException ex) {
            Log.d(TAG, "An InterruptedException was caught: " + ex.getMessage());
        }
        return output;
    }

    public boolean isRootAvailable() {
        String result = RunAsRootOutput("busybox id -u");
        return result.equals("0");
    }

}

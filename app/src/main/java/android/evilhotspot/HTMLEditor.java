package android.evilhotspot;


import android.content.Context;
import android.content.res.Resources;

import org.apache.commons.io.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.jsoup.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.helper.*;
import org.jsoup.select.Elements;

/**
 * Created by Natalia on 2015-11-28.
 */
public class HTMLEditor {


    public static void Reader(Context context){

        Document htmlFile = null;

            htmlFile = Jsoup.parse(Reader2(context), "ISO-8859-1");

        Elements elem = htmlFile.select("img[src]");

        Log.i("bla",elem.first().toString());

        for(Element img: htmlFile.select("img")){
            img.attr("src", "DDDDDDDDD");

        }


        final File f= new File(context.getFilesDir(),"testnew.html");
        try {
            FileUtils.writeStringToFile(f, htmlFile.outerHtml(), "UTF-8");
        }catch(IOException e) {
            e.printStackTrace(); }
    }

    public static String Reader2(Context context){


               //get the application's resources
               String output ="";
               Resources resources = context.getResources();

                try {
                    output = LoadFile(context, "teststrona", true);
                    //Toast t = Toast.makeText(context, output,Toast.LENGTH_LONG);
                    //t.show();
                }catch (IOException e)
                {
                    //display an error toast message
                    Toast toast = Toast.makeText(context,"Exception",Toast.LENGTH_LONG);
                    toast.show();
                }
        return output;


    }
    public static String LoadFile(Context context, String fileName, boolean loadFromRawFolder) throws IOException
    {
        //Create a InputStream to read the file into
        InputStream iS;
        Resources resources = context.getResources();
        if (loadFromRawFolder)
        {
            //get the resource id from the file name
            int rID = resources.getIdentifier("teststrona", "raw", context.getPackageName());
            //get the file as a stream
            iS = resources.openRawResource(rID);
        }
        else
        {
            //get the file as a stream
            iS = resources.getAssets().open(fileName);
        }

        //create a buffer that has the same size as the InputStream
        byte[] buffer = new byte[iS.available()];
        //read the text file as a stream, into the buffer
        iS.read(buffer);
        //create a output stream to write the buffer into
        ByteArrayOutputStream oS = new ByteArrayOutputStream();
        //write this buffer to the output stream
        oS.write(buffer);
        //Close the Input and Output streams
        oS.close();
        iS.close();

        //return the output stream as a String
        return oS.toString();
    }
}



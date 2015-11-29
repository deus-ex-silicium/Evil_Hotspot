package android.evilhotspot;
import android.os.AsyncTask;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Nibiru
 */
public class requestTask extends AsyncTask<String, Void, Document> {

    Document doc;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Document doInBackground(String... urls) {
        //make a request for a website, return response
        try {
            doc = Jsoup.connect(urls[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return doc;
    }

}

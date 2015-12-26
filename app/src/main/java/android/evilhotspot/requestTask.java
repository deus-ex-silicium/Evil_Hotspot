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

    private Document doc;

    @Override
    protected Document doInBackground(String... url) {
        //make a request for a website, return response
        try {
            doc = Jsoup.connect(url[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return doc;
    }
}

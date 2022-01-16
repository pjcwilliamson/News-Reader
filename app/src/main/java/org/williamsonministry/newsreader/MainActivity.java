package org.williamsonministry.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ArrayList<NewsItem> news;

    private RecyclerView recyclerView;

    private NewsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        news = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetNews().execute();
    }

    private class GetNews extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getInputStream();
            if (null != inputStream) {
                try {
                    initXMLPullParser(inputStream);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter.setNewsItems(news);
        }

        private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
            Log.d(TAG, "initXMLPullParser: Initializing XML Pull Parser");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.next();

            parser.require(XmlPullParser.START_TAG, null, "rss");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                parser.require(XmlPullParser.START_TAG, null, "channel");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }

                    if (parser.getName().equals("item")) {
                        parser.require(XmlPullParser.START_TAG, null, "item");

                        String title = "";
                        String description = "";
                        String link = "";
                        String coverImages = "";
                        String pubDate = "";

                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }

                            String tagName = parser.getName();
                            if (tagName.equals("title")) {
                                title = getContent(parser, "title");
                            } else if (tagName.equals("description")) {
                                description = getContent(parser, "description");
                            } else if (tagName.equals("link")) {
                                link = getContent(parser, "link");
                            } else if (tagName.equals("pubDate")) {
                                pubDate = getContent(parser, "pubDate");
                            } else if (tagName.equals("coverImages")) {
                                coverImages = getContent(parser, "coverImages");
                            } else {
                                skipTag(parser);
                            }
//                            switch (tagName) {
//                                case "title":
//                                    title = getContent(parser, tagName);
//                                    break;
//                                case "description":
//                                    description = getContent(parser, tagName);
//                                    break;
//                                case "link":
//                                    link = getContent(parser, tagName);
//                                    break;
//                                case "coverImages":
//                                    coverImages = getContent(parser, tagName);
//                                    break;
//                                case "pubDate":
//                                    pubDate = getContent(parser, tagName);
//                                    break;
//                                default:
//                                    skipTag(parser);
//                                    break;
//                            }
                        }

                        NewsItem item = new NewsItem(title, description, link, pubDate, coverImages);
                        news.add(item);
                    } else {
                        skipTag(parser);
                    }
                }
            }
        }

        private InputStream getInputStream() {
            try {
                URL url = new URL("https://www.espncricinfo.com/rss/content/story/feeds/5.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getContent(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
            String content = "";
            parser.require(XmlPullParser.START_TAG, null, tagName);

            if (parser.next() == XmlPullParser.TEXT) {
                content = parser.getText();
                parser.next();
            }

            return content;
        }

        private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }

            int number = 1;

            while (number != 0) {
                switch (parser.next()) {
                    case XmlPullParser.START_TAG:
                        number++;
                        break;
                    case XmlPullParser.END_TAG:
                        number--;
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
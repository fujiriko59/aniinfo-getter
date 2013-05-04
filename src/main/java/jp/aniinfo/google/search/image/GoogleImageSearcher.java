package jp.aniinfo.google.search.image;


import jp.aniinfo.google.exception.GoogleImageSearchException;
import jp.aniinfo.google.search.image.entity.ImageInfo;
import jp.aniinfo.wikipedia.httpclient.HttpClientFactory;
import net.arnx.jsonic.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoogleImageSearcher {
    public static final String apiUrl = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";

    public static List<ImageInfo> search(String query) throws GoogleImageSearchException {
        if (StringUtils.isBlank(query)) {

        }
        List<ImageInfo> results = new ArrayList<ImageInfo>();
        HttpClient client = HttpClientFactory.create();
        try {
            HttpGet httpGet = new HttpGet(apiUrl + query);
            HttpResponse response = client.execute(httpGet);
            String json = EntityUtils.toString(response.getEntity());
            Pojo pojo = JSON.decode(json, Pojo.class);

            for (Map<String, String> map : pojo.responseData.results) {
                ImageInfo info = new ImageInfo();
                info.setUrl(map.get("unescapedUrl"));
                info.setWidth(Long.parseLong(map.get("width")));
                info.setHeight(Long.parseLong(map.get("height")));
                info.setTbUrl(map.get("tbUrl"));
                info.setTbWidth(Long.parseLong(map.get("tbWidth")));
                info.setTbHeight(Long.parseLong(map.get("tbHeight")));
                results.add(info);
            }

        } catch (Exception e) {
            throw new GoogleImageSearchException(e);
        } finally {
            client.getConnectionManager().shutdown();
        }

        return results;
    }

    public static class Pojo {
        public ResponseData responseData;
    }

    public static class ResponseData {
        public List<Map<String, String>> results;
    }

}

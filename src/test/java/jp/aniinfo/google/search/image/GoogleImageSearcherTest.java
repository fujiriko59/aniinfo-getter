package jp.aniinfo.google.search.image;


import jp.aniinfo.google.search.image.entity.ImageInfo;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

public class GoogleImageSearcherTest extends TestCase {
    public void test_search_success() {
        String[] titles = {"灰羽連盟", "広橋涼 OR 平野綾"};
        for (String title : titles) {
            System.out.println(title);
            try {
                title = URLEncoder.encode(title, "UTF-8");
                List<ImageInfo> list = GoogleImageSearcher.search(title);

                assertTrue(list.size() > 0);
                for (ImageInfo info : list) {
                    System.out.println(info.getUrl());
                    System.out.println(info.getTbUrl());
                    assertTrue(StringUtils.isNotBlank(info.getUrl()));
                    assertTrue(info.getWidth() > 0);
                    assertTrue(info.getHeight() > 0);
                    assertTrue(StringUtils.isNotBlank(info.getTbUrl()));
                    assertTrue(info.getTbWidth() > 0);
                    assertTrue(info.getTbHeight() > 0);
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
    }
}

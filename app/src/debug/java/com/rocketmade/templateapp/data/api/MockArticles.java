package com.rocketmade.templateapp.data.api;

import com.rocketmade.templateapp.models.Article;
import com.rocketmade.templateapp.models.Outlet;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eliasbagley on 11/24/15.
 */
public class MockArticles {
    private MockArticles() {
        throw new AssertionError("No instances.");
    }


    public static final Outlet outlet1 = Outlet.builder()
            .id(1)
            .name("Outlet 1")
            .image("mock:///outlet_images/outlet1.png")
            .build();
    public static final Outlet outlet2 = Outlet.builder()
            .id(2)
            .name("Outlet 2")
            .image("mock:///outlet_images/outlet2.png")
            .build();

    public static final Article article1 = Article.builder().id(1)
            .title("test 1")
            .image("mock:///article_images/article1.jpg")
            .outlet(outlet1)
            .publicationDate("2017-12-10")
            .build();

    public static final Article article2 = Article.builder().id(2)
            .title("test 2")
            .image("mock:///article_images/article1.jpg")
            .outlet(outlet1)
            .publicationDate("2017-12-10")
            .build();

    public static final Article article3 = Article.builder().id(3)
            .title("test 3")
            .image("mock:///article_images/article1.jpg")
            .outlet(outlet1)
            .publicationDate("2017-12-10")
            .build();

    public static final Article article4 = Article.builder().id(4)
            .title("test 4")
            .image("mock:///article_images/article1.jpg")
            .outlet(outlet2)
            .publicationDate("2017-12-8")
            .build();

    public static final Article article5 = Article.builder().id(5)
            .title("test 5")
            .image("mock:///article_images/article1.jpg")
            .outlet(outlet2)
            .publicationDate("2017-12-9")
            .build();

    public static final Article article6 = Article.builder().id(6)
            .title("test 6")
            .image("mock:///article_images/article1.jpg")
            .outlet(outlet2)
            .publicationDate("2017-12-11")
            .build();

    public static final List<Article> articlesList() {
        return null;
    }

    public static final List<Outlet> outletList() {
        return Arrays.asList(outlet1, outlet2);
    }
}

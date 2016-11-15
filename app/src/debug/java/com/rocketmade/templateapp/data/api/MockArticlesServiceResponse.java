package com.rocketmade.templateapp.data.api;

import com.rocketmade.templateapp.network.serviceresponse.ArticlesServiceResponse;

import java.util.Arrays;
import java.util.Collections;

public enum MockArticlesServiceResponse {
    SUCCESS("Success", new ArticlesServiceResponse(Arrays.asList( //
            MockArticles.article1,
            MockArticles.article2,
            MockArticles.article3,
            MockArticles.article4,
            MockArticles.article5,
            MockArticles.article6
    ), MockArticles.outletList())),
    ONE("One", new ArticlesServiceResponse(Collections.singletonList(MockArticles.article1), MockArticles.outletList())),
    EMPTY("Empty", new ArticlesServiceResponse(null, null));

    public final String                  name;
    public final ArticlesServiceResponse response;

    MockArticlesServiceResponse(String name, ArticlesServiceResponse response) {
        this.name = name;
        this.response = response;
    }

    @Override
    public String toString() {
        return name;
    }
}

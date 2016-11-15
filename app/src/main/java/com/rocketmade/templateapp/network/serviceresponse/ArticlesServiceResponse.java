package com.rocketmade.templateapp.network.serviceresponse;

import com.rocketmade.templateapp.models.Article;
import com.rocketmade.templateapp.models.Outlet;

import java.util.List;

import lombok.Value;

/**
 * Created by eliasbagley on 11/24/15.
 */

@Value
public class ArticlesServiceResponse {
    List<Article> articles;
    List<Outlet>  outlets;
}

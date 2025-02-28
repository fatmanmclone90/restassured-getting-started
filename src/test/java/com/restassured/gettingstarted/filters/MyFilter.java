package com.restassured.gettingstarted.filters;

import io.restassured.builder.ResponseBuilder;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class MyFilter implements io.restassured.filter.Filter{
    @Override
    public Response filter(
            FilterableRequestSpecification filterableRequestSpecification,
            FilterableResponseSpecification filterableResponseSpecification,
            FilterContext filterContext) {
        filterableRequestSpecification.replaceHeader("jip", "jap");

        return filterContext.next(filterableRequestSpecification, filterableResponseSpecification);
    }
}

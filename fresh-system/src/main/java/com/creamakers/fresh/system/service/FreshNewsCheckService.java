package com.creamakers.fresh.system.service;

import com.creamakers.fresh.system.domain.dto.FreshNews;

import java.io.IOException;

public interface FreshNewsCheckService {

    boolean addFreshNewsCheck(FreshNews freshNews, String finalUrls) throws IOException;
}

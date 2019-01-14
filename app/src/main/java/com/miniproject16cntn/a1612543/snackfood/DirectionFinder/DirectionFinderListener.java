package com.miniproject16cntn.a1612543.snackfood.DirectionFinder;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

package com.example.workstasion.myapplication.Workers;

import java.util.Comparator;

/**
 * Created by WORKSTASION on 15.12.2016.
 */

public class DirectoryComparator implements Comparator<Loader.DirectoryPreview> {
    @Override
    public int compare(Loader.DirectoryPreview x, Loader.DirectoryPreview y) {
        int startComparison = compare(x.getLastModified(), y.getLastModified());
        return startComparison != 0 ? startComparison
                : compare(x.getLastModified(), y.getLastModified());
    }

    private static int compare(long a, long b) {
        return a < b ? 1
                : a > b ? -1
                : 0;
    }
}

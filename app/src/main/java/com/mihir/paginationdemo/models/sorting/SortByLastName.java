package com.mihir.paginationdemo.models.sorting;

import com.mihir.paginationdemo.models.UserList;

import java.util.Comparator;

/**
 * Created by Mihir on 15-09-2017.
 */

public class SortByLastName implements Comparator {
    public int compare(Object o1, Object o2) {
        UserList p1 = (UserList) o1;
        UserList p2 = (UserList) o2;
        return p1.getLastName().compareTo(p2.getLastName());
    }
}

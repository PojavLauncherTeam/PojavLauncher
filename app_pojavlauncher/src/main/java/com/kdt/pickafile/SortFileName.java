package com.kdt.pickafile;

import java.io.*;
import java.util.*;

public class SortFileName implements Comparator<File>
{
    @Override
    public int compare(File f1, File f2) {
		return f1.getName().compareToIgnoreCase(f2.getName());
    }
}


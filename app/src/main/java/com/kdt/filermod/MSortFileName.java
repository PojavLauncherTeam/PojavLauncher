package com.kdt.filermod;

import java.io.*;
import java.util.*;

public class MSortFileName implements Comparator<File>
{
    @Override
    public int compare(File f1, File f2) {
		return f1.getName().compareToIgnoreCase(f2.getName());
    }
}


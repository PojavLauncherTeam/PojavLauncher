package net.kdt.pojavlaunch.colorselector;

public interface ColorSelectionListener {
    /**
     * This method gets called by the ColorSelector when the color is selected
     * @param color the selected color
     */
    void onColorSelected(int color);
}
